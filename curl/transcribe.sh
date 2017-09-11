#!/bin/bash

if [ -z $1 ] ; then
  echo "Please, specify a subscription key." && exit 1;
fi

if [ -z $2 ] ; then
  echo "Please, specify a file to transcribe." && exit 2;
fi

subscriptionKey=$1
filename=$2
output_format="simple"
language="en-US"
locale="en-US"
recognition_mode="interactive"

token=$(curl --fail -X POST "https://api.cognitive.microsoft.com/sts/v1.0/issueToken" \
                -H "Content-type: application/x-www-form-urlencoded" -H "Content-Length: 0" \
                -H "Ocp-Apim-Subscription-Key: $subscriptionKey")

if [ -z $token ] ; then
  echo "Request to issue an auth token failed." && exit 1;
fi

request_url="https://speech.platform.bing.com/speech/recognition/$recognition_mode"
request_url+="/cognitiveservices/v1?language=$language&locale=$locale"
request_url+="&format=$output_format&requestid=rest_sample_request_id"

curl -X POST $request_url -H "Transfer-Encoding: chunked" \
        -H "Content-type: audio/wav; codec=\"audio/pcm\"; samplerate=16000" \
        -H "Authorization: Bearer $token" --data-binary @$filename

echo ""