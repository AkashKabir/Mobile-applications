package org.buildmlearn.practicehandwriting.practice;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.buildmlearn.practicehandwriting.R;

import java.util.Date;


public class DrawingView extends View {
    //drawing path
    private Path drawPath;
    //drawing and canvas paint
    private Paint drawPaint, canvasPaint;
    //colour
    private int touch_colour = 0xFFFF0000; //Red is more visible against the background
    //canvas
    private Canvas drawCanvas;
    //canvas bitmap
    private Bitmap canvasBitmap;
    //Canvas width and height
    private int width;
    private int height;

    private Vibrator vibrator;

    private long wrongTouches;
    private long correctTouches;

    private boolean mDraw;

    private long vibrationStartTime;

    private Toast errorToast;


    public DrawingView(Context context,AttributeSet attrs) {
        super(context,attrs);
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        //getting height and width of the current display
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        width = metrics.widthPixels;
        height = metrics.heightPixels;
        canvasBitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);

        errorToast = new Toast(context);
        errorToast.setGravity(Gravity.CENTER_HORIZONTAL, 0, height/4);
        errorToast.setDuration(Toast.LENGTH_SHORT);
        errorToast.setView(((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.toast,
                (ViewGroup) findViewById(R.id.toast_layout_root)));

        setupDrawing();
    }

    public void setBitmap(Bitmap b) {
        canvasBitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
        drawCanvas.drawBitmap(b, (width - b.getWidth()) / 2,(height - b.getHeight()) / 2, canvasPaint);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //draw view
        canvas.drawBitmap(canvasBitmap, 0,0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(mDraw) {
            //detect user touch
            float touchX = event.getX();
            float touchY = event.getY();

            // mapping screen touch co-ordinates to image pixel co-ordinates
            int x = (int) (touchX * canvasBitmap.getWidth() / width);
            int y = (int) (touchY * canvasBitmap.getHeight() / height);

            if ((x >= 0 && x < width && y >= 0 && y < height && canvasBitmap.getPixel(x, y) == 0) || (x < 0 || x >= width || y < 0 || y >= height)) {
                vibrator.vibrate(100);
                wrongTouches++;
                if(vibrationStartTime == 0) {
                    vibrationStartTime = new Date().getTime();
                    errorToast.cancel();
                } else if(new Date().getTime() - vibrationStartTime > 1000 && errorToast.getView().getWindowVisibility() != View.VISIBLE) {
                    errorToast.show();
                }
            } else {
                vibrationStartTime = 0;
                correctTouches++;
            }

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    drawPath.moveTo(touchX, touchY);
                    break;
                case MotionEvent.ACTION_MOVE:
                    drawPath.lineTo(touchX, touchY);
                    break;
                case MotionEvent.ACTION_UP:
                    vibrationStartTime = 0;
                    drawCanvas.drawPath(drawPath, drawPaint);
                    drawPath.reset();
                    break;
                default:
                    return false;
            }
            invalidate();
            return true;
        }
        return false;
    }

    public void setupDrawing() {
        //get drawing area setup for interaction
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(touch_colour);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(15);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
        correctTouches = 0;
        wrongTouches = 0;
        mDraw = true;
        vibrationStartTime = 0;
    }

    public void canDraw(boolean draw) {
        mDraw = draw;
    }

    public float score() {
        return (correctTouches+wrongTouches!=0)?100*correctTouches/(correctTouches+wrongTouches):0;
    }
}
