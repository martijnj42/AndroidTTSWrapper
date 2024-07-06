package com.martijn.androidttswrapper;

import android.app.Activity;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import java.util.Locale;
import java.util.ArrayList;


public final class TtsWrapper{
    public static final String LOGTAG = "Unity";

    protected TextToSpeech mTts;
    private boolean isInitialised = false;
    private ArrayList<String> availableVoices;
    private ArrayList<String> unavailableVoices;
    protected final TtsInitialisationCallBack callback;

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
        this.callback = callback;
        checkAndInitialiseTtsData(currentActivity);
    }

    /**
     * Engine defined initialiser
     **/
    public TtsWrapper(Activity currentActivity, TtsInitialisationCallBack callback, String engineName){
        this.callback = callback;
        this.engineName = engineName;
        checkAndInitialiseTtsData(currentActivity);
    }

    /**
     * Engine defined, and Language defined initialiser
     * Use empty string to for default engine
     **/
    public TtsWrapper(Activity currentActivity, TtsInitialisationCallBack callback, String engineName, Locale language) {
        this.callback = callback;
        this.engineName = engineName;
        this.language = language;
        checkAndInitialiseTtsData(currentActivity);
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
    public void checkAndInitialiseTtsData(Activity currentActivity) {
        Log.e(LOGTAG, "Start check/init");

        try {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setClass(currentActivity,OnResultCallback.class);
            OnResultCallback.onVoiceDataCheckResult = new OnVoiceDataCheckResult(this);
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
        if(mTts != null){
            mTts.shutdown();
        }
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
}