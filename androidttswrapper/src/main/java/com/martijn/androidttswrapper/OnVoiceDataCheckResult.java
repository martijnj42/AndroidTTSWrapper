package com.martijn.androidttswrapper;


import android.content.Context;
import android.speech.tts.TextToSpeech;
import java.util.ArrayList;

/**
 * Class to hold actions for OnVoiceDataPass and OnVoiceDataFail
 **/
public class OnVoiceDataCheckResult {
    private final TtsWrapper main;

    /**
     * Store a reference to the main TtsWrapper class object
     **/
    public OnVoiceDataCheckResult(TtsWrapper mainTtsWrapper){
        this.main = mainTtsWrapper;
    }

    /**
     * After a successful TTS DATA check, continue initialisation by creating the tts object
     **/
    public void onVoiceDataPass(Context currentContext, ArrayList<String> availableVoices, ArrayList<String> unavailableVoices) {
        main.setAvailableVoices(availableVoices);
        main.setUnavailableVoices(unavailableVoices);

        // if EngineName declared use this
        if (main.engineName.isEmpty()) {
            main.mTts = new TextToSpeech(currentContext, new TextToSpeechInitializer(main));
        } else {
            main.mTts = new TextToSpeech(currentContext, new TextToSpeechInitializer(main), main.engineName);
        }
    }

    /**
     * After a failed TTS DATA check, call failed callback function
     **/
    public void onVoiceDataFail(){
        // Call failed call back function
        main.callback.onFailure();
    }
}
