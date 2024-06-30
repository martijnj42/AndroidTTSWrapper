package com.martijn.androidttswrapper;

import android.app.Activity;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import java.util.Locale;
import java.util.ArrayList;


public final class TtsWrapper{
    protected static final String LOGTAG = "Unity";

    private final Activity currentActivity;

    private TextToSpeech mTts;
    private boolean isInitialised = false;
    private ArrayList<String> availableVoices;
    private ArrayList<String> unavailableVoices;
    private final TtsInitialisationCallBack callback;

    public String engineName = "";
    public Locale language;

    public interface TtsInitialisationCallBack{
        void onSuccess(int languageSetResultCode);
        void onFailure();
    }

    // Initialisation functions

    /**
     * Basic initialiser
     **/
    public TtsWrapper(Activity currentActivity, TtsInitialisationCallBack callback){
        this.currentActivity = currentActivity;
        this.callback = callback;
    }

    /**
     * Engine defined initialiser
     **/
    public TtsWrapper(Activity currentActivity, TtsInitialisationCallBack callback, String engineName){
        this.currentActivity = currentActivity;
        this.callback = callback;
        this.engineName = engineName;
    }

    /**
     * Engine defined, and Language defined initialiser
     * Use empty string to for default engine
     **/
    public TtsWrapper(Activity currentActivity, TtsInitialisationCallBack callback, String engineName, Locale language) {
        this.currentActivity = currentActivity;
        this.callback = callback;
        this.engineName = engineName;
        this.language = language;
    }


    // Setter functions

    void setAvailableVoices(ArrayList<String> newAvailableVoices){
        availableVoices = newAvailableVoices;
    }

    void setUnavailableVoices(ArrayList<String> newUnavailableVoices){
        unavailableVoices = newUnavailableVoices;
    }

    void setIsInitialised(boolean newIsInitialised){
        isInitialised = newIsInitialised;
    }


    // Getter functions

    public ArrayList<String> getAvailableVoices() {
        return availableVoices;
    }

    public ArrayList<String> getUnavailableVoices() {
        return unavailableVoices;
    }

    public boolean getIsInitialised() {
        return isInitialised;
    }

    public TextToSpeech getTtsObject(){
        return mTts;
    }

    // Main

    /**
     * Start Activity to check if TTS engine correctly configured
     **/
    public void checkAndInitialiseTtsData() {
        Log.e(LOGTAG, "Start check/init");

        try {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setClass(currentActivity,OnResultCallback.class);
            OnResultCallback.onVoiceDataCheckResult = new onVoiceDataCheckResult(this);
            currentActivity.startActivity(shareIntent);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.i(LOGTAG,"Error sharing intent to ResultCallBack failed: " + e);
        }
    }

    /**
     * Shutdown TTS if initialised
     **/
    public void onDestroy(){
        if(isInitialised){
            mTts.shutdown();
        }
    }

    /**
     * Class to hold actions for OnVoiceDataPass
     **/
    public class onVoiceDataCheckResult {
        private final TtsWrapper main;

        /**
         * Store a reference to the main TtsWrapper class object
         **/
        public onVoiceDataCheckResult(TtsWrapper mainTtsWrapper){
            this.main = mainTtsWrapper;
        }

        /**
         * After a successful TTS DATA check, continue initialisation by creating the tts object
         **/
        public void onVoiceDataPass(ArrayList<String> availableVoices, ArrayList<String> unavailableVoices) {
            main.setAvailableVoices(availableVoices);
            main.setUnavailableVoices(unavailableVoices);

            // if EngineName declared use this
            if (main.engineName.isEmpty()) {
                main.mTts = new TextToSpeech(main.currentActivity.getApplicationContext(), new TextToSpeechInitializer());
            } else {
                main.mTts = new TextToSpeech(main.currentActivity.getApplicationContext(), new TextToSpeechInitializer(), main.engineName);
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


    /**
     * Class to handle init of tts
     **/
    public class TextToSpeechInitializer implements TextToSpeech.OnInitListener {
        @Override
        public void onInit(int status) {
            if (status != TextToSpeech.SUCCESS) {
                callback.onFailure();
                Log.e(LOGTAG, "Failed TTS initialisation");
                return;
            }

            setIsInitialised(true);

            // If a language is set, use this language
            if (language != null){
                int languageSetResultCode = getTtsObject().setLanguage(language);
                if (languageSetResultCode < 0){
                    Log.e(LOGTAG, "Language set failed code: " + languageSetResultCode);
                }

                // Call on success Callback with setLanguageResult code
                callback.onSuccess(languageSetResultCode);
            }

            // Call on success call back with 0
            callback.onSuccess(0);
        }
    }

    /**
     * Get all available engine names
     **/
    public String getAllEngineNames() {
        if (!isInitialised){
            return "TTS not initialised";
        }
        return mTts.getEngines().toString();
    }

    /**
     * Set language of TTS if it exists
     * @return the output code of the TTS API or -13 if not initialised
     */
    public int setLanguage(String language, String country){
        if (!isInitialised){
            return -13;
        }

        return mTts.setLanguage(new Locale(language, country));
    }

    /**
     * Check if module correctly imported, test function
     **/
    public static String sayHello(){
        return "Hello from from .AAR module";
    }
}
