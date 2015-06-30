package org.buildmlearn.practicehandwriting.activities;

import android.os.Bundle;
import android.view.View;

import org.buildmlearn.practicehandwriting.R;
import org.buildmlearn.practicehandwriting.helper.CalculateFreehandScore;

public class FreehandActivity extends PracticeBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDrawView.canVibrate(false);
    }

    @Override
    public void practiceActivityOnClick(View v) {
        super.practiceActivityOnClick(v);
        switch (v.getId()) {
            case R.id.reset_button:
                mDrawView.clearCanvas();
                mDrawView.canVibrate(false);
                break;
            case R.id.done_save_button:
                if(!mDone) {
                    mDone = true;
                    new CalculateFreehandScore(this, mDrawView, mPracticeString).execute();
                }
                break;
        }
    }
}
