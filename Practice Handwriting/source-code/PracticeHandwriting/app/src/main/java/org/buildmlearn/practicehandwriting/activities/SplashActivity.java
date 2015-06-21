package org.buildmlearn.practicehandwriting.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;

import org.buildmlearn.practicehandwriting.R;


public class SplashActivity extends Activity implements TextToSpeech.OnInitListener{

    private static int SPLASH_TIME_OUT = 1500;
    private static final int MY_DATA_CHECK_CODE = 100;

    public static TextToSpeech TTSobj;

    public static String[] CHARACTER_LIST, EASY_WORD_LIST, MEDIUM_WORD_LIST, HARD_WORD_LIST;

    private boolean mTtsInitDone, mListsDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //TODO change variables in activities to follow specified coding conventions
        mTtsInitDone = false;
        mListsDone = false;

        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);

        CHARACTER_LIST = getResources().getStringArray(R.array.English_characters);
        EASY_WORD_LIST = getResources().getStringArray(R.array.English_words_easy);
        MEDIUM_WORD_LIST = getResources().getStringArray(R.array.English_words_medium);
        HARD_WORD_LIST = getResources().getStringArray(R.array.English_words_hard);
        mListsDone = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(!(mTtsInitDone && mListsDone));
                Intent intent = new Intent(SplashActivity.this, LanguageActivity.class);
                startActivity(intent);
            }
        }).start();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == MY_DATA_CHECK_CODE) {

            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // success, create the TTS instance
                TTSobj = new TextToSpeech(this, this);
            }
            else {
                // missing data, install it
                Intent installIntent = new Intent();
                installIntent.setAction(
                        TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }
    }

    public void onInit(int i) {
        try {
            Thread.sleep(SPLASH_TIME_OUT);
            mTtsInitDone = true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown!
        if (TTSobj != null) {
            TTSobj.stop();
            TTSobj.shutdown();
        }
        super.onDestroy();
    }
}
