#!/bin/sh
location="us-central1" # Or anywhere
usage_and_exit() {
    echo "Usage: $0 <project_num_or_id>"
    echo "List glossaries for the specified project."
    exit 1
}

if [ $# -ne 1 ]; then
   usage_and_exit
fi
project_id="$1"

curl -X GET \
-H "Authorization: Bearer "$(gcloud auth application-default print-access-token) \
     https://translation.googleapis.com/v3/projects/${project_id}/locations/${location}/glossaries

