#!/bin/sh
location="us-central1" # Or anywhere
usage_and_exit() {
    echo "Usage: $0 <project_num_or_id> <glossary_id>"
    echo "Delete the specified glossary of the specified project."
    exit 1
}

if [ $# -ne 2 ]; then
   usage_and_exit
fi

if [ "${GOOGLE_APPLICATION_CREDENTIALS}" = "" ]; then
    echo "The environment variable GOOGLE_APPLICATION_CREDENTIALS is missing."
    echo "It must exist and point to a JSON credential file."
    exit 2
fi

project_id="$1"
glossary_id="$2"

curl -X DELETE \
-H "Authorization: Bearer "$(gcloud auth application-default print-access-token) \
     https://translation.googleapis.com/v3/projects/${project_id}/locations/${location}/glossaries/${glossary_id}

