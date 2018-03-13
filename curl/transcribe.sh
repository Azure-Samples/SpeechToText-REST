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

request_url="https://speech.platform.bing.com/synthesize"

curl -X POST $request_url\
	-H "Content-type: application/ssml+xml"\
	-H "X-Microsoft-OutputFormat: riff-8khz-8bit-mono-mulaw"\
	-H "X-Search-AppID: 1FAB0C18BF26467BA26E16EC30F568D2"\
	-H "X-Search-ClientID: 30290D544C9C4FF2AF4D391DAE3417B6"\
	-H "User-Agent: SpeechToText-REST"\
	-H "Authorization: Bearer $token"\
	--data-binary @$filename

echo ""
