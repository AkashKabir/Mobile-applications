package org.buildmlearn.practicehandwriting.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;

import org.buildmlearn.practicehandwriting.R;
import org.buildmlearn.practicehandwriting.helper.Animator;


public class MainMenuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        //adding zoom in animation for the buttons
        int[] buttons = new int[] {R.id.character_button,R.id.word_button,R.id.timetrial_button,R.id.freehand_button};
        for(int i=0;i<buttons.length;i++) {
            Animation animation = Animator.createScaleUpCompleteAnimation();
            animation.setStartOffset(500 * i);
            findViewById(buttons[i]).startAnimation(animation);
        }
    }

    public void mainMenuActivityOnClick(View view) {
        Intent intent = null;
        switch(view.getId()) {
            case R.id.character_button:
                intent = new Intent(this, CharacterSelectionActivity.class);
                intent.putExtra(getResources().getString(R.string.practice_mode),getResources().getString(R.string.practice));
                break;

            case R.id.word_button:
                intent = new Intent(this, WordSelectionActivity.class);
                break;

            case R.id.freehand_button:
                intent = new Intent(this, CharacterSelectionActivity.class);
                intent.putExtra(getResources().getString(R.string.practice_mode),getResources().getString(R.string.freehand));
                break;

            case R.id.timetrial_button:
                intent = new Intent(this, TimeTrialActivity.class);
                break;
        }
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(MainMenuActivity.this, LanguageActivity.class));
    }
}
