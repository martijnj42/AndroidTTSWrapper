package com.martijn.androidttswrapper;

import android.util.Log;
import android.speech.tts.TextToSpeech;

/**
 * Class to handle init of tts
 **/
public class TextToSpeechInitializer implements TextToSpeech.OnInitListener {
    private final TtsWrapper main;

    public TextToSpeechInitializer(TtsWrapper mainTtsWrapper){
        this.main = mainTtsWrapper;
    }

    @Override
    public void onInit(int status) {
        if (status != TextToSpeech.SUCCESS) {
            main.callback.onFailure();
            Log.e(TtsWrapper.LOGTAG, "Failed TTS initialisation");
            return;
        }

        main.setIsInitialised(true);

        // If a language is set, use this language
        if (main.language != null){
            int languageSetResultCode = main.getTtsObject().setLanguage(main.language);
            if (languageSetResultCode < 0){
                Log.e(TtsWrapper.LOGTAG, "Language set failed code: " + languageSetResultCode);
            }

            // Call on success Callback with setLanguageResult code
            main.callback.onSuccess(languageSetResultCode);
            return;
        }

        // Call on success call back with 0
        main.callback.onSuccess(0);
    }
}
