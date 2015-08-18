package org.buildmlearn.practicehandwriting.activities;

import android.os.Bundle;
import android.view.View;

import org.buildmlearn.practicehandwriting.R;
import org.buildmlearn.practicehandwriting.helper.CalculateFreehandScore;
import org.buildmlearn.practicehandwriting.helper.PracticeBaseActivity;

public class FreehandActivity extends PracticeBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDrawView.canVibrate(false);
        mDrawView.canScore(false);
    }

    @Override
    public void practiceOnClick(View v) {
        super.practiceOnClick(v);
        switch (v.getId()) {
            case R.id.reset_button:
                mDrawView.init();
                mDrawView.canVibrate(false);
                mDrawView.canScore(false);
                break;
            case R.id.done_save_button:
                if(!mDone) {
                    mDone = true;
                    //Using an asyncTask to calculate the score inorder to reduce the amount of computation done on UI thread
                    new CalculateFreehandScore(this, mDrawView, mPracticeString).execute();
                }
                break;
        }
    }
}
