package org.buildmlearn.practicehandwriting.helper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Environment;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.buildmlearn.practicehandwriting.R;
import org.buildmlearn.practicehandwriting.activities.SplashActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

//Custom View implementing the trace engine.
public class DrawingView extends View {

    private Path mDrawPath; //The drawing path
    private Paint mDrawPaint, mCanvasPaint; //The paints used to draw on the canvas
    private Canvas mDrawCanvas; //The canvas of the view
    private Bitmap mCanvasBitmap; //The bitmap that is set
    private Vibrator mVibrator; //Vibrator instance to vibrate if the user traces outside the boundary of the string
    private long mWrongTouches, mCorrectTouches; //number of correct/wrong touches
    private boolean mDraw, mVibrate, mScoring; //Boolean variables that allow the view to be redrawn and to vibrate
    private long mVibrationStartTime; //The start time of the vibration
    private Toast mErrorToast; //Toast to display if user has been tracing wrongly for a long time
    private int minX, minY, maxX, maxY; //bounds of the touches
    private int mTouchColour; //colour of the touch
    private ArrayList<ArrayList<Point>> mTouchPoints; //List of strokes. Each ArrayList<Point> is the touches from one MOTION_DOWN even to a MOTION_UP even
    private Context mContext;// The context of the view

    public int mWidth, mHeight, mTextWidth, mTextHeight; //View dimensions and text dimensions

    public DrawingView(Context context) {
        super(context);
        init(context);
    }

    public DrawingView(Context context,AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DrawingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        //context based init goes here
        mContext = context;
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        //Getting display width and height
        mWidth = SplashActivity.mDisplayMetrics.widthPixels;
        mHeight = SplashActivity.mDisplayMetrics.heightPixels;

        ((Activity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mErrorToast = new Toast(mContext);
                mErrorToast.setGravity(Gravity.CENTER_HORIZONTAL, 0, mHeight / 4);
                mErrorToast.setDuration(Toast.LENGTH_SHORT);
                mErrorToast.setView(((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.toast,
                        (ViewGroup) findViewById(R.id.toast_layout_root)));
            }
        });

        //initializing without context
        init();
    }

    public void init() {
        //get drawing area setup for interaction
        mTouchColour = getResources().getColor(R.color.Red);

        mDrawPath = new Path();
        mDrawPaint = new Paint();
        mDrawPaint.setColor(mTouchColour);
        mDrawPaint.setAntiAlias(true);
        mDrawPaint.setStrokeWidth(15);
        //Setting the paint to draw round strokes
        mDrawPaint.setStyle(Paint.Style.STROKE);
        mDrawPaint.setStrokeJoin(Paint.Join.ROUND);
        mDrawPaint.setStrokeCap(Paint.Cap.ROUND);
        mCanvasPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCorrectTouches = 0;
        mWrongTouches = 0;
        mDraw = true;
        mVibrationStartTime = 0;

        minX = mWidth;
        maxX = -1;
        minY = mHeight;
        maxY = -1;

        mScoring = true;

        mVibrate = true; //Vibration on by default

        mTouchPoints = new ArrayList<>(); //Empty list as no touches yet

        System.gc();
        if(mCanvasBitmap!=null) {
            mCanvasBitmap.recycle();
            mCanvasBitmap = null;
            mDrawCanvas = null;
        }
        mCanvasBitmap = Bitmap.createBitmap(mWidth,mHeight, Bitmap.Config.ARGB_4444);
        mDrawCanvas = new Canvas(mCanvasBitmap);
    }

    //function to set text to be traced to the view
    public void setBitmapFromText(String str) {
        init();

        Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintText.setColor(Color.BLACK);
        paintText.setStyle(Paint.Style.FILL);
        int size = SplashActivity.mDisplayMetrics.densityDpi/str.length();//Starting size of the text
        float textHeight;
        do {
            paintText.setTextSize(++size);
            textHeight = paintText.descent() - paintText.ascent();

        } while(paintText.measureText(str) < mWidth * 0.8 && textHeight < mHeight *0.8);//setting the max size of the text for the given screen
        mDrawPaint.setStrokeWidth(size * 3 / 182); //values got from experimenting

        float textOffset = textHeight/ 2 - paintText.descent();
        mTextWidth = (int) paintText.measureText(str);
        mTextHeight = (int) textHeight;
        //Drawing the text at the center of the view
        mDrawCanvas.drawText(str, (mWidth - paintText.measureText(str)) / 2, (mHeight / 2) + textOffset, paintText);
        invalidate();
    }

    public void setBitmap(Bitmap b) {
        init();
        //Drawing the bitmap at the center of the view
        mDrawCanvas.drawBitmap(b, (mWidth - b.getWidth()) / 2, (mHeight - b.getHeight()) / 2, mCanvasPaint);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //draw view
        canvas.drawBitmap(mCanvasBitmap, 0, 0, mCanvasPaint);
        canvas.drawPath(mDrawPath, mDrawPaint);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if(mDraw) {//Draws the touches only if mDraw is true

            //detect user touch
            float touchX = event.getX();
            float touchY = event.getY();


            // mapping screen touch co-ordinates to image pixel co-ordinates
            int x = (int) (touchX * mCanvasBitmap.getWidth() / mWidth);
            int y = (int) (touchY * mCanvasBitmap.getHeight() / mHeight);

            //updating the touch bounds
            if (x < minX)
                minX = x;
            if (x > maxX)
                maxX = x;
            if (y < minY)
                minY = y;
            if (y > maxY)
                maxY = y;
            if(mScoring) {
                //checking if the touches are correct or wrong (inside or outside the boundary
                if ((x >= 0 && x < mWidth && y >= 0 && y < mHeight && mCanvasBitmap.getPixel(x, y) == Color.TRANSPARENT) || (x < 0 || x >= mWidth || y < 0 || y >= mHeight)) {
                    mWrongTouches++;
                    if (mVibrate) {//Device will vibrate only if mVibrate is true
                        mVibrator.vibrate(100);
                        if (mVibrationStartTime == 0) {
                            mVibrationStartTime = new Date().getTime();
                            mErrorToast.cancel();
                        } else if (new Date().getTime() - mVibrationStartTime > 1000 && mErrorToast.getView().getWindowVisibility() != View.VISIBLE) {
                            mErrorToast.show();
                        }
                    }
                } else {
                    mVibrationStartTime = 0;
                    mCorrectTouches++;
                }
            }

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mDrawPath.moveTo(touchX, touchY);
                    mTouchPoints.add(new ArrayList<Point>());//ACTION_DOWN event means a new stroke so a new ArrayList
                    break;
                case MotionEvent.ACTION_MOVE:
                    mDrawPath.lineTo(touchX, touchY);
                    break;
                case MotionEvent.ACTION_UP:
                    mVibrationStartTime = 0;
                    mDrawCanvas.drawPath(mDrawPath, mDrawPaint);
                    mDrawPath.reset();//End of the current stroke
                    break;
                default:
                    return false;
            }
            //Adding the touch point to the last ArrayList
            mTouchPoints.get(mTouchPoints.size() - 1).add(new Point((int) touchX, (int) touchY));
            invalidate();
            return true;
        }
        return false;
    }

    public void canScore(boolean scoring){
        mScoring = scoring;
    }

    public void canVibrate(boolean vibrate){
        mVibrate = vibrate;
    }

    public void canDraw(boolean draw) {
        mDraw = draw;
    }

    public Bitmap getBitmap() {
        //Get the image of the view
        Bitmap overlayBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_4444);
        overlayBitmap.eraseColor(getResources().getColor(R.color.AppBg));
        Canvas canvas = new Canvas(overlayBitmap);
        canvas.drawBitmap(mCanvasBitmap,0,0,null);
        return overlayBitmap;
    }

    public Bitmap getCanvasBitmap() {
        return mCanvasBitmap;
    }

    public int[] getTouchBounds() {
        return new int[] {minX,minY,maxX,maxY};
    }

    public float score() {
        return (mCorrectTouches + mWrongTouches !=0)?100* mCorrectTouches /(mCorrectTouches + mWrongTouches):0;
    }

    public ArrayList<ArrayList<Point>> getTouchesList() {
        return mTouchPoints;
    }

    public Bitmap getTouchesBitmap() {
        if(minX!=mWidth && minY!=mHeight && maxX!=-1 && maxY!=-1)
            return Bitmap.createBitmap(mCanvasBitmap,Math.max(minX - 30, 0),Math.max(minY -30,0),Math.min(maxX - minX +40, mWidth - minX),Math.min(maxY - minY +40, mHeight - minY));
        else//This implies no touch events were received
            return Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_4444);
    }

    public String saveBitmap(String practiceString, String dirExtra) {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory() + File.separator + getResources().getString(R.string.app_name) + dirExtra);
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            return "Could not save trace. Unable to create directory";
        } else {//Compress the bitmap and then store it
            File file;
            if (!dirExtra.equals(""))
                file = new File(mediaStorageDir.getPath() + File.separator + practiceString + "_" + String.valueOf(score()) + ".jpg");
            else
                file = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + "_" + practiceString + ".jpg");

            int actualHeight = mHeight;
            int actualWidth = mWidth;

            float maxHeight = 800.0f;
            float maxWidth = 600.0f;
            float imgRatio = actualWidth / actualHeight;
            float maxRatio = maxWidth/maxHeight;

            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                if (imgRatio < maxRatio) {
                    imgRatio = maxHeight / actualHeight;
                    actualWidth = (int) (imgRatio * actualWidth);
                    actualHeight = (int) maxHeight;
                } else if (imgRatio > maxRatio) {
                    imgRatio = maxWidth / actualWidth;
                    actualHeight = (int) (imgRatio * actualHeight);
                    actualWidth = (int) maxWidth;
                } else {
                    actualHeight = (int) maxHeight;
                    actualWidth = (int) maxWidth;
                }
            }
            Bitmap scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_4444);

            float ratioX = actualWidth / (float) mWidth;
            float ratioY = actualHeight / (float) mHeight;
            float middleX = actualWidth / 2.0f;
            float middleY = actualHeight / 2.0f;

            Matrix scaleMatrix = new Matrix();
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

            Canvas canvas = new Canvas(scaledBitmap);
            canvas.setMatrix(scaleMatrix);
            canvas.drawBitmap(getBitmap(), middleX - mWidth / 2, middleY - mHeight / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

            ByteArrayOutputStream tempStream = new ByteArrayOutputStream();
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, tempStream);

            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight());

            try{
                FileOutputStream fOut = new FileOutputStream(file);
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
                fOut.flush();
                fOut.close();
                return file.getPath();
            } catch (FileNotFoundException e) {
                file.delete();
                return "Could not save trace. Unable to open file";
            } catch (IOException e) {
                file.delete();
                return "Could not save trace. Unable to save file";
            }
        }
    }
}