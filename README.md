# AndroidTTSWrapper
AndroidTTSWrapper is a wrapper around the Android Text-To-Speech module, designed to simplify the initialization and management of the Android TextToSpeech engine.

## Usage
Match the sdk version of the plugin, in the file: `androidttswraper/build.gradle`, to match your project. Build the `androidttswrapper` and add the .aar bundle to your Android Project. 

### Unity
[UnityAndroidTTSExample](https://github.com/martijnj42/UnityAndroidTTSExample) shows an example how to use the plugin together with Unity.

## TODO
- Add function `getVoiceByName()`, easily get a voice.
- Add function `setVoiceByName()`, easily set a voice.
- Add function `randomVoiceSpeak()`, simplify speech with randomised voice, for more interesting speech.
- Improve `OnVoiceDataCheckResult.onFailure(int resultCode)` by adding interger resultCodes, to pinpoint where the error happend.
