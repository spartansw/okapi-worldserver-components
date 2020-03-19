#!/bin/sh

# https://cloud.google.com/translate/docs/advanced/glossary
location="us-central1" # Or anywhere

usage_and_exit() {
    echo "Usage: $0 <project_num_or_id> <target_lang> <bucket_name> <glossary_filename> <glossary_id>"
    echo "<project_num_or_id> is the number or the id of the project."
    echo "<target_lang> is the language code of the target language."
    echo "<bucket_name> is the name of the bucket where the CSV or TSV file is stored."
    echo "<glossary_filename> is a name of CSV or TSV file already added to the bucket."
    echo "<glossary_id> is the id of glossary to be created."
    exit 1
}

if [ $# -ne 5 ]; then
   usage_and_exit
fi

if [ "${GOOGLE_APPLICATION_CREDENTIALS}" = "" ]; then
    echo "The environment variable GOOGLE_APPLICATION_CREDENTIALS is missing."
    echo "It must exist and point to a JSON credential file."
    exit 2
fi

project_id="$1"
target_lang="$2"
bucket_name="$3"
glossary_filename="$4"
glossary_id="$5"


# First create a json file.

jsonpath=/tmp/create-gl-$$.json

cat > ${jsonpath} <<EOF
{
  "name":"projects/${project_id}/locations/${location}/glossaries/${glossary_id}",
  "languagePair": {
    "sourceLanguageCode": "en",
    "targetLanguageCode": "${target_lang}"
    },
  "inputConfig": {
    "gcsSource": {
      "inputUri": "gs://${bucket_name}/${glossary_filename}"
    }
  }
}
EOF

# Then POST the JSON file, capturing the output.

resfilepath=/tmp/create-gl-$$-response.txt

curl -X POST \
 -H "Authorization: Bearer $(gcloud auth application-default print-access-token)" \
 -H "Content-Type: application/json; charset=utf-8" \
 -d @${jsonpath} \
 https://translation.googleapis.com/v3/projects/${project_id}/locations/${location}/glossaries | tee ${resfilepath}

jobname=$(sed -n 's/^ *"name": "\([^"]*\)", *$/\1/p' < ${resfilepath} | head -1)

cat <<EOF
If you see "state": "RUNNING", the submission was successful and the glossary is being created.
To check the status of the glossary creation, run the following command:

    curl -X GET -H "Authorization: Bearer $(gcloud auth application-default print-access-token)" https://translation.googleapis.com/v3/${jobname}
EOF

rm "${jsonpath}" "${resfilepath}"
