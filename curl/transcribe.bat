@echo off

if [%1]==[] goto usage
if [%2]==[] goto usage

setlocal enableextensions

set "subscriptionKey=%~1"
set "filename=%~2"
set "output_format=simple"
set "language=en-US"
set "locale=en-US"
set "recognition_mode=interactive"
 
FOR /F "tokens=*" %%I in ('curl --fail -X POST "https://api.cognitive.microsoft.com/sts/v1.0/issueToken" ^
        -H "Content-type: application/x-www-form-urlencoded" -H "Content-Length: 0" ^
        -H "Ocp-Apim-Subscription-Key: %subscriptionKey%"') do (set token=%%I)

if [%token%]==[] (
  @echo Could not issue Auth Token.
  exit /B 1
)

curl -X POST "https://speech.platform.bing.com/synthesize" ^
 -H "Content-type: application/ssml+xml" -H "X-Microsoft-OutputFormat: riff-8khz-8bit-mono-mulaw" ^
 -H "X-Search-AppID: 1FAB0C18BF26467BA26E16EC30F568D2" -H "X-Search-ClientID: 30290D544C9C4FF2AF4D391DAE3417B6" ^ 
 -H "User-Agent: SpeechToText-REST" -H "Authorization: Bearer %token%" ^
 --data-binary @%filename%

endlocal
goto :eof

:usage
@echo Usage: %0 ^<subscription key^> ^<file to transcribe^>
exit /B 1
