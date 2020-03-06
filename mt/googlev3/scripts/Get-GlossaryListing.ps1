# This returns the listing of existing glossaries.
# This script requires environment variable
# GOOGLE_APPLICATION_CREDENTIALS is set to point to 
# the token file and PATH includes Google Cloud SDK
# command directory.
param(
    [parameter(Mandatory=$true)]
	[String]$projectId
)
$location="us-central1"
$token=gcloud auth application-default print-access-token
$headers=@{"Authorization" = "Bearer $token"}
$res=Invoke-WebRequest -Uri https://translation.googleapis.com/v3/projects/$projectId/locations/$location/glossaries -Headers $headers
$res.Content
