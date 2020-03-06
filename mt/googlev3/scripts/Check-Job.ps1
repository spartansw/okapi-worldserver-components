# Check the job status. Useful to check the status of
# glossary ceation started by the Add-Glossary script.
# This script requires environment variable
# GOOGLE_APPLICATION_CREDENTIALS is set to point to 
# the token file and PATH includes Google Cloud SDK
# command directory.
param(
    [parameter(Mandatory=$true)][String]$projectId, # project id or number
    [parameter(Mandatory=$true)][String]$jobId # id of the job
    )
$location="us-central1"

if (-not (Test-Path Env:GOOGLE_APPLICATION_CREDENTIALS)) {
    Throw "Environment variable GOOGLE_APPLICATION_CREDENTIALS must be set to point to the JSON crendential file for Google Cloud.";
}


$cred = gcloud auth application-default print-access-token
$headers = @{ "Authorization" = "Bearer $cred" }

Invoke-WebRequest `
  -Method GET `
  -Headers ${headers} `
  -Uri "https://translation.googleapis.com/v3/projects/${projectId}/locations/${location}/operations/${jobId}" | Select-Object -Expand Content