package org.buildmlearn.practicehandwriting.helper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.AsyncTask;
import android.widget.TextView;

import com.software.shell.fab.ActionButton;

import org.buildmlearn.practicehandwriting.R;
import org.buildmlearn.practicehandwriting.activities.SplashActivity;

import java.util.ArrayList;

public class CalculateFreehandScore extends AsyncTask<Void,Void,float[]> {
    Bitmap mTouchImg, mSavedImg;
    String mPracticeString;
    ArrayList<ArrayList<Point>> mTouches;
    ProgressDialog mProgressDialog;
    DrawingView mDrawView;
    Context mContext;
    int[] mTouchBounds;

    public CalculateFreehandScore(Context context, DrawingView drawView, String practiceString) {
        mContext = context;
        mDrawView = drawView;
        mPracticeString = practiceString;
    }

    @Override
    protected void onPreExecute() {
        mTouchImg = mDrawView.getTouchesBitmap();
        mTouches = mDrawView.getTouchesList();
        mTouchBounds = mDrawView.getTouchBounds();

        mDrawView.clearCanvas();
        mDrawView.init();
        mDrawView.setBitmapFromText(mPracticeString);//setting the text to calculate the score
        mDrawView.canDraw(false);

        //Image of the text to calculate the score on
        mSavedImg = mDrawView.getCanvasBitmap();

        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setTitle("Please wait ...");
        mProgressDialog.setMessage("Calculating...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    @Override
    protected float[] doInBackground(Void... voids) {
        float size = 0;
        for (int i = 0; i < mTouches.size(); i++)
            size += mTouches.get(i).size();
        if(size!=0) {//Computes score only if the view has been touched

            int centerX = (mSavedImg.getWidth() - mTouchImg.getWidth())/2, centerY = (mSavedImg.getHeight() - mTouchImg.getHeight())/2;
            int minx = mTouchBounds[0], miny = mTouchBounds[1];
            float correctTouches;
            int i, j, cx, cy, x ,y, count = 0;
            float scaleX, scaleY;
            int textColour = mContext.getResources().getColor(R.color.Black); //Storing locally for faster access inside the loop
            float score, maxScore = 0, scaleXForMaxScore = 1, scaleYForMaxScore = 1, cxForMaxScore = centerX, cyForMaxScore = centerY;
            int[] xTouches = new int[(int)size], yTouches = new int[(int)size];
            //Storing the bitmap height and width locally for faster access
            int width = mSavedImg.getWidth(), height = mSavedImg.getHeight();
            int[][] bitmap = new int[width][height];

            //Top corner of the touch bounds set as origin. Doing this outside the score calculation loop to prevent excess computation in the loop
            //Converting from ArrayList to int array as array access is faster
            for (i = 0; i < mTouches.size(); i++)
                for(j=0;j<mTouches.get(i).size();j++) {
                    xTouches[count] = mTouches.get(i).get(j).x - minx;
                    yTouches[count] = mTouches.get(i).get(j).y - miny;
                    count++;
                }

            //Getting the bitmap as a 2D int array for faster access
            for(i=0;i<width;i++)
                for(j=0;j<height;j++)
                    bitmap[i][j] = mSavedImg.getPixel(i, j);
            outerLoop:
            for (scaleX = 0.8f;  scaleX < 1.4f; scaleX += 0.1f)
                for (scaleY = 0.8f;scaleY < 1.4f; scaleY += 0.1f) {
                    for (cx = centerX - 20; cx <= centerX + 20; cx += 2)
                        for (cy = centerY - 20; cy <= centerY + 20; cy += 2) {
                            correctTouches = 0;
                            for (i = 0; i < size; i++) {
                                //The transformed touch points at the new scale and center
                                x = (int) (xTouches[i] * scaleX) + cx;
                                y = (int) (yTouches[i] * scaleY) + cy;

                                if (x >= 0 && x < width && y >= 0 && y < height && bitmap[x][y] == textColour)
                                        correctTouches += 1;
                            }
                            score = correctTouches / size;
                            if (score > maxScore) { //updating the values for a maximum score
                                maxScore = score;
                                scaleXForMaxScore = scaleX;
                                scaleYForMaxScore = scaleY;
                                cxForMaxScore = cx;
                                cyForMaxScore = cy;
                                if (score == 1)
                                    break outerLoop;//as best score possible has been obtained
                            }
                        }
                }
            return new float[]{100 * maxScore, scaleXForMaxScore, scaleYForMaxScore, cxForMaxScore, cyForMaxScore};
        } else {
            return new float[]{0, 1, 1, (mSavedImg.getWidth() - mTouchImg.getWidth())/2, (mSavedImg.getHeight() - mTouchImg.getHeight())/2};
        }
    }

    @Override
    protected void onPostExecute(float[] result) {
        mProgressDialog.dismiss();
        // getting the best score for the given letter and updating it if the current score is better
        float best = SplashActivity.mDbHelper.getScore(mPracticeString);
        if (best < result[0]) {
            best = result[0];
            SplashActivity.mDbHelper.writeScore(mPracticeString,best);
        }

        mDrawView.clearCanvas();
        mDrawView.init();
        //Overlaying the touches bitmap on the text at the best scale and position and setting it to the DrawingView
        mDrawView.setBitmap(bitmapOverlay(mSavedImg, scaleBitmap(mTouchImg, result[1], result[2]), (int) result[3], (int) result[4]));
        mDrawView.canDraw(false);
        //Animations for when the user is done with the trace
        mDrawView.startAnimation(Animator.createScaleDownAnimation());

        ((TextView) ((Activity) mContext).findViewById(R.id.score_and_timer_View)).setText("Score: " + String.valueOf(result[0]));
        ((Activity) mContext).findViewById(R.id.score_and_timer_View).setAnimation(Animator.createFadeInAnimation());
        ((TextView) ((Activity) mContext).findViewById(R.id.best_score_View)).setText("Best: " + String.valueOf(best));
        ((Activity) mContext).findViewById(R.id.best_score_View).setAnimation(Animator.createFadeInAnimation());

        Animator.createYFlipForwardAnimation(((Activity) mContext).findViewById(R.id.done_save_button));
        ((ActionButton) ((Activity) mContext).findViewById(R.id.done_save_button)).setImageResource(R.drawable.ic_save);
        Animator.createYFlipBackwardAnimation(((Activity) mContext).findViewById(R.id.done_save_button));
    }

    private Bitmap scaleBitmap(Bitmap originalImage, float scaleX, float scaleY) {
        //new dimensions
        float width = originalImage.getWidth() * scaleX;
        float height = originalImage.getHeight() * scaleY;

        //initializing an empty canvas
        Bitmap background = Bitmap.createBitmap((int) width, (int) height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(background);

        //Transformation matrix for the given scale
        float xTranslation = 0.0f, yTranslation = (height - originalImage.getHeight() * scaleY)/2.0f;
        Matrix transformation = new Matrix();
        transformation.postTranslate(xTranslation, yTranslation);
        transformation.preScale(scaleX, scaleY);

        Paint paint = new Paint();
        paint.setFilterBitmap(true);

        //Drawing the bitmap at the given scale
        canvas.drawBitmap(originalImage, transformation, paint);
        return background;
    }

    //function to place a bitmap over another
    private Bitmap bitmapOverlay(Bitmap bitmap1, Bitmap bitmap2, int xOffset, int yOffset) {
        //initializing an empty canvas and drawing the first bitmap on top
        Bitmap resultBitmap = Bitmap.createBitmap(bitmap1.getWidth(), bitmap1.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(resultBitmap);
        c.drawBitmap(bitmap1, 0, 0, null);

        Paint p = new Paint();
        p.setAlpha(255);

        //Drawing the second bitmap over the first with the given offset
        c.drawBitmap(bitmap2, xOffset, yOffset, p);
        return resultBitmap;
    }
}