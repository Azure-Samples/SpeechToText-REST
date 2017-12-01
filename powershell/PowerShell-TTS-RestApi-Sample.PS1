Function Play-MediaFile($Filename)
{
# PowerShell Function to play WAV file programatically without invoking Media Player
# Supplied file must be a standard Windows WAV

# Note, function has no error checking
$PlayMedia=New-object System.Media.Soundplayer
$PlayMedia.SoundLocation=($Filename)
$PlayMedia.playsync()
}

# Generate Access token to allow use of Cognitive Text to Speech API
# and store in $AccessToken

$Uri="https://api.cognitive.microsoft.com/sts/v1.0/issueToken"
$Method="POST"
$ContentType="application/json"
$APIKey='-----' 
$Headers=@{'Ocp-Apim-Subscription-Key' = $APIKey }

# Call up Access Token API with Provided Subscription Key
$AccessToken=Invoke-RestMethod -Uri $URI `
-Method $METHOD -ContentType $ContentType `
–Headers $HEADERS

# Define Headers for REST API
# Type of Audio File
$AudioOutputType='riff-16khz-16bit-mono-pcm' 

# SearchAppID Guid - Generate using (New-Guid).guid | Set-Clipboard and "Paste" in section between ''
$XSearchAppId=''

# SearchClientID Guid - Generate using (New-Guid).guid | Set-Clipboard and "Paste" in section between ''

$XSearchClientId=''

# Name of Calling application
$UserAgent='SampleApplicationName'

# Populate the header values
$Headers=@{ `
'Content-Type' = 'application/ssml+xml'; `
'X-Microsoft-OutputFormat' = $AudioOutputType; `
'X-Search-AppId' = $XSearchAppId; `
'X-Search-ClientId' = $XSearchClientId; `
'Authorization' = $AccessToken `
}

$Uri= 'https://speech.platform.bing.com/synthesize'
$Method='POST'
$ContentType='application/ssml+xml'
$Filename='output.wav'

#$Headers=@{'Content-Type' = 'application/ssml+xml';'X-Microsoft-OutputFormat' = $AudioOutputType;'X-Search-AppId' = $XSearchAppId; 'X-Search-ClientId' = $XSearchClientId; 'Authorization' = $AccessToken }
#^^^ ^^^ ^^^
# Or does THIS make more sense to you for the header?

# Define the Region for the Voice (You will note there *is* a unique region for each voice
# as the Spanish voice region (es-ES) will pronounce Spanish words far better than the
# English voice (en-US) as it is TUNED to that language.   But you CAN mix if you're interested

$Region='en-US'
$Voice='"Microsoft Server Speech Text to Speech Voice (en-US, JessaRUS)"'
$Content="It's time for a little PowerShell fun"

$Body='<speak version="1.0" xmlns="http://www.w3.org/2001/10/synthesis" xmlns:mstts="http://www.w3.org/2001/mstts" xml:lang="'+$region+'"><voice xml:lang="' +$Region+'" name='+$Voice+'>'+$Content+'</voice></speak>'

Invoke-RestMethod -Uri $Uri -Method $Method `
-Headers $Headers -ContentType $ContentType `
-Body $Body -UserAgent $UserAgent `
-OutFile $Filename
Play-MediaFile -Filename $Filename
break

# Body in Spanish
$Region='es-ES'
$Voice='"Microsoft Server Speech Text to Speech Voice (es-ES, Pablo, Apollo)"'
$Content="Es hora de un poco de diversión PowerShell"

$Body='<speak version="1.0" xmlns="http://www.w3.org/2001/10/synthesis" xmlns:mstts="http://www.w3.org/2001/mstts" xml:lang="'+$region+'"><voice xml:lang="' +$Region+'" name='+$Voice+'>'+$Content+'</voice></speak>'

Invoke-RestMethod -Uri $Uri -Method $Method `
-Headers $Headers -ContentType $ContentType `
-Body $Body -UserAgent $UserAgent `
-OutFile $Filename
Play-MediaFile -Filename $Filename
break

# Body in French
$Region='fr-FR'
$Voice='"Microsoft Server Speech Text to Speech Voice (fr-FR, HortenseRUS)"'
$Content="Il est temps pour un peu de fun PowerShell"

$Body='<speak version="1.0" xmlns="http://www.w3.org/2001/10/synthesis" xmlns:mstts="http://www.w3.org/2001/mstts" xml:lang="'+$region+'"><voice xml:lang="' +$Region+'" name='+$Voice+'>'+$Content+'</voice></speak>'

Invoke-RestMethod -Uri $Uri -Method $Method `
-Headers $Headers -ContentType $ContentType `
-Body $Body -UserAgent $UserAgent `
-OutFile $Filename
Play-MediaFile -Filename $Filename
