package app.mysunshine.android.example.com.mysunshine.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Custom Forecast Control
 */
public class WindControl extends View {
    private final String LOG_TAG = getClass().getSimpleName();

    private Paint mWindmillPaint;
    private Paint mArrowPaint;
    private float mSpeed;
    private float mDegrees;
    private float mRotation = 359f;
    private Bitmap mRotor;
    private Bitmap mStand;

    public WindControl(Context context) {
        super(context);
        init();
    }

    public WindControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WindControl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mWindmillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mWindmillPaint.setStyle(Paint.Style.FILL);
        mArrowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mArrowPaint.setStyle(Paint.Style.FILL);
        mArrowPaint.setColor(Color.GRAY);
        mArrowPaint.setStrokeWidth(20f);
//        mRotor = BitmapFactory.decodeResource(getResources(), R.drawable.rotor);
//        mStand = BitmapFactory.decodeResource(getResources(), R.drawable.windmill);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.v(LOG_TAG, "Wind speed: " + mSpeed);
        Log.v(LOG_TAG, "Wind direction: " + mDegrees);
        // Draw rotor
        int h = 0;
        int w = 0;
        canvas.drawBitmap(mRotor, rotate(mRotor, h, w), mWindmillPaint);
        canvas.drawBitmap(mStand, 0, 10, mWindmillPaint);
        invalidate();
    }

    public Matrix rotate(Bitmap bm, int x, int y) {
        Matrix mtx = new Matrix();
        mtx.postRotate(mRotation, bm.getWidth() / 2, bm.getHeight() / 2);
        mtx.postTranslate(x, y);  //The coordinates where we want to put our bitmap
        mRotation -= mSpeed; //degree of rotation
        return mtx;
    }

    public void setSpeed(float speed) {
        mSpeed = speed;
    }

    public void setDegrees(float degrees) {
        mDegrees = degrees;
    }
}
