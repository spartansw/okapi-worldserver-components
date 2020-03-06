# Creating a glossary from the csv file that has been uploaded to Google Bucket
# See https://cloud.google.com/translate/docs/advanced/glossary
# This script requires environment variable
# GOOGLE_APPLICATION_CREDENTIALS is set to point to 
# the token file and PATH includes Google Cloud SDK
# command directory.
param(
    [parameter(Mandatory=$true)][String]$projectId, # project id or number
    [parameter(Mandatory=$true)][String]$targetLang, # target language
    [parameter(Mandatory=$true)][String]$bucketName, # name of the Google bucket where the CSV (or TSV) file is stored
    [parameter(Mandatory=$true)][String]$csvFileName, # name of the CSV (or TSV) file
    [parameter(Mandatory=$true)][String]$glossaryId # id of the glossary to be created
    )
$location="us-central1"

if (-not (Test-Path Env:GOOGLE_APPLICATION_CREDENTIALS)) {
    Throw "Environment variable GOOGLE_APPLICATION_CREDENTIALS must be set to point to the JSON crendential file for Google Cloud.";
}

$token=gcloud auth application-default print-access-token
$headers = @{ "Authorization" = "Bearer $token"; "Content-Type" = "application/json; charset=utf-8" }

$body = @"
{
  "name":"projects/${projectId}/locations/${location}/glossaries/${glossaryId}",
  "languagePair": {
    "sourceLanguageCode": "en",
    "targetLanguageCode": "${targetLang}"
    },
  "inputConfig": {
    "gcsSource": {
      "inputUri": "gs://${bucketName}/${csvFileName}"
    }
  }
}
"@
$uri="https://translation.googleapis.com/v3/projects/${projectId}/locations/${location}/glossaries"

try {
    $res = $body | Invoke-WebRequest -Method POST -Uri ${uri} -Headers ${headers}
} catch {
    $ex = $_.Exception
    $ex.ToString() | Write-Host 
    Exit $_.Exception.Response.StatusCode.value__
} 
$jobName = $res.Content | ConvertFrom-Json | Select -ExpandProperty name
if ( ${jobName} -match 'proj.*/([-0-9a-f]+)') {
    $jobId=$Matches[1]
} else {
    throw "Couldn't capture the job id";
}
Write-Host "
The job ${jobId} started. Run:
        Check-Job ${projectId} ${jobId}
to check the status."
