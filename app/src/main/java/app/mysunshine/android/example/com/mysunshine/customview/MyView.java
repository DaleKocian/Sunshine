package app.mysunshine.android.example.com.mysunshine.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by k557782 on 9/15/14.
 */
public class MyView extends View {
    public MyView(Context context) {
        super(context);
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int hSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int wSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int hSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int wSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int myHeight = hSpecSize;
        int myWidth = wSpecSize;
        if (hSpecMode == MeasureSpec.EXACTLY) {
            myHeight = hSpecSize;
        } else if (hSpecMode == MeasureSpec.AT_MOST) {
            // Wrap Content
        }
        if (wSpecMode == MeasureSpec.EXACTLY) {
            myWidth = wSpecSize;
        } else if (wSpecMode == MeasureSpec.AT_MOST) {
            // Wrap Content
        }
        setMeasuredDimension(myWidth, myHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
    }
}
