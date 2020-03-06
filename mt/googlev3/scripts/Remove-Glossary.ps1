# Remove a glossary
# See https://cloud.google.com/translate/docs/advanced/glossary
# This script requires environment variable
# GOOGLE_APPLICATION_CREDENTIALS is set to point to 
# the token file and PATH includes Google Cloud SDK
# command directory.
param(
    [parameter(Mandatory=$true)][String]$projectId, # project id or number
    [parameter(Mandatory=$true)][String]$glossaryId # id of the glossary to be created
    )
$location="us-central1"

if (-not (Test-Path Env:GOOGLE_APPLICATION_CREDENTIALS)) {
    Throw "Environment variable GOOGLE_APPLICATION_CREDENTIALS must be set to point to the JSON crendential file for Google Cloud.";
}

$token=gcloud auth application-default print-access-token
$headers = @{ "Authorization" = "Bearer $token" }

$uri="https://translation.googleapis.com/v3/projects/${projectId}/locations/${location}/glossaries/${glossaryId}"

try {
    $res = $body | Invoke-WebRequest -Method DELETE -Uri ${uri} -Headers ${headers}
} catch {
    $ex = $_.Exception
    $ex.ToString() | Write-Host 
    Exit $_.Exception.Response.StatusCode.value__
} 
Write-Host "$res"
