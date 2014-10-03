package com.darkriddle.testrosapp.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.darkriddle.testrosapp.R;

/**
 * Created by segf4ult on 12/17/13.
 * Adjusted by DarkRiddle on 12/17/13.
 */
public class JoystickView extends View {

    private Context mCtx;

    private RectF mOutCircleBounds;

    private Bitmap mJoystick;
    private Bitmap mJoystickScaled;

    private Paint mLinePaint;
    private float width;
    private float height;

    private float mOffsetX = 0f;
    private float mOffsetY = 0f;
    private int iDiameter;
    private float oDiameter;

    public JoystickView(Context context) {
        super(context);
        mCtx = context;
        initDraw();
    }

    public JoystickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mCtx = context;
        initDraw();
    }

    public void setOffset(float sx, float sy) {
        mOffsetX = sx;
        mOffsetY = sy;
        invalidate();
    }

    private void initDraw() {
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(5);
        mLinePaint.setColor(Color.parseColor("#70ffffff"));

        mJoystick = BitmapFactory.decodeResource(getResources(), R.drawable.joystick_3);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawLine((width/2),0,(width/2),height,mLinePaint);
        canvas.drawLine(0,(height/2),width,(height/2),mLinePaint);
        float rdiff = (float)((oDiameter/2.0)-(iDiameter/2.0));
        float iX = (float)(rdiff+mOutCircleBounds.left + (mOffsetX*rdiff));
        float iY = (float)(rdiff+mOutCircleBounds.top + (mOffsetY*rdiff));
        canvas.drawBitmap(mJoystickScaled, iX, iY, null);
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float xpad = (float)(getPaddingLeft()+getPaddingRight()+20);
        float ypad = (float)(getPaddingTop()+getPaddingBottom()+20);

        float ww = (float)w-xpad;
        float hh = (float)h-ypad;

        width = w;
        height = h;

        oDiameter = Math.min(ww,hh);
        iDiameter = (int)(oDiameter/5.0);
        mOutCircleBounds = new RectF(0.0f,0.0f,oDiameter,oDiameter);
        mOutCircleBounds.offsetTo(getPaddingLeft() + 10, (float)(h/2-(oDiameter/2.0)));
        mJoystickScaled = Bitmap.createScaledBitmap(mJoystick,iDiameter, iDiameter,true);
    }
}
