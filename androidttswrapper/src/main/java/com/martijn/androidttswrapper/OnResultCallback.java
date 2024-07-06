package com.martijn.androidttswrapper;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import java.util.ArrayList;


public class OnResultCallback extends Activity {
    public static final String LOGTAG = TtsWrapper.LOGTAG;
    private static final int DATA_CHECK_CODE = 1;
    private static final int DATA_INSTALL_CODE = 2;
    private static final int DATA_CHECK_CODE_AFTER_INSTALL = 3;
    public static OnVoiceDataCheckResult onVoiceDataCheckResult;

    private void endActivity(int resultCode, ArrayList<String> availableVoices, ArrayList<String> unavailableVoices){
        if (onVoiceDataCheckResult != null){
            if (resultCode < 0){
                onVoiceDataCheckResult.onVoiceDataFail();
            } else {
                onVoiceDataCheckResult.onVoiceDataPass(this.getApplicationContext(), availableVoices, unavailableVoices);
            }
        }
        onVoiceDataCheckResult = null;
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(LOGTAG, "onCreateBundle ResultCallback");

        try {
            Intent newIntent = new Intent();
            newIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
            Log.i(LOGTAG,"Created CHECK_DATA Intent");
            startActivityForResult(newIntent, DATA_CHECK_CODE);
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.i(LOGTAG,"error: " + e.getLocalizedMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(LOGTAG, "onActivityResult: requestCode= " + requestCode + ", resultCode= " + resultCode);

        // Check if data code matched the requested one
        if (requestCode == DATA_CHECK_CODE || requestCode == DATA_CHECK_CODE_AFTER_INSTALL) {
            // Check the result
            if (resultCode != TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // Give up it install attempted failed
                if (requestCode == DATA_CHECK_CODE_AFTER_INSTALL){
                    Log.e(LOGTAG,"Failed to install Voice Data");
                    endActivity(-1, null, null);
                    return;
                }

                // Missing voice data, try to install it
                try {
                    Intent installIntent = new Intent();
                    Log.i(LOGTAG, "Created install data Intent");
                    installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                    startActivityForResult(installIntent, DATA_INSTALL_CODE);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(LOGTAG,"error: " + e.getLocalizedMessage());
                }
            }

            // On success continue initialisation
            ArrayList<String> availableVoices = data.getStringArrayListExtra(TextToSpeech.Engine.EXTRA_AVAILABLE_VOICES);
            ArrayList<String> unavailableVoices = data.getStringArrayListExtra(TextToSpeech.Engine.EXTRA_UNAVAILABLE_VOICES);
            endActivity(0, availableVoices, unavailableVoices);

        } else if (requestCode == DATA_INSTALL_CODE) {
            // After attempted download, check engine again
            try {
                Intent newIntent = new Intent();
                newIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
                Log.i(LOGTAG, "Created CHECK_DATA Intent after attempted install");
                startActivityForResult(newIntent, DATA_CHECK_CODE);
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(LOGTAG,"error: " + e.getLocalizedMessage());
            }
        }
    }
}