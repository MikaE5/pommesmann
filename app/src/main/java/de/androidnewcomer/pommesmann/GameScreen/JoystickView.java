package de.androidnewcomer.pommesmann.GameScreen;

// https://www.instructables.com/id/A-Simple-Android-UI-Joystick/


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import de.androidnewcomer.pommesmann.App;
import de.androidnewcomer.pommesmann.R;

public class JoystickView extends SurfaceView implements SurfaceHolder.Callback,
        View.OnTouchListener {

    private float centerX;
    private float centerY;
    private float baseRadius;
    private float hatRadius;

    private JoystickListener joystickCallback;


    public JoystickView(Context context) {
        super(context);
        if (context instanceof JoystickListener) {
            joystickCallback = (JoystickListener) context;
        }
        additionalConstructor();
    }

    public JoystickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (context instanceof JoystickListener) {
            joystickCallback = (JoystickListener) context;
        }
        additionalConstructor();
    }

    public JoystickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (context instanceof JoystickListener) {
            joystickCallback = (JoystickListener) context;
        }
        additionalConstructor();
    }

    private void additionalConstructor() {
        getHolder().addCallback(this);
        setOnTouchListener(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        setupDimensions();
        drawJoystick(centerX, centerY);
    }

    private void setupDimensions() {
        centerX = getWidth() / 2;
        centerY = getHeight() / 2;
        float temp = Math.min(getWidth(), getHeight());
        baseRadius = temp / 3;
        hatRadius = temp / 7;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    private void drawJoystick(float newX, float newY) {
        if (getHolder().getSurface().isValid()) {
            Canvas myCanvas = this.getHolder().lockCanvas();

            Paint paint = new Paint();
            myCanvas.drawColor(App.getContext().getResources().getColor(R.color.toolbarBackground));

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(hatRadius * 0.125f);
            paint.setColor(App.getContext().getResources().getColor(R.color.joystickColor));
            myCanvas.drawCircle(centerX, centerY, baseRadius, paint);

            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setColor(App.getContext().getResources().getColor(R.color.joystickColor));
            myCanvas.drawCircle(newX, newY, hatRadius, paint);

            getHolder().unlockCanvasAndPost(myCanvas);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.equals(this)) {
            if (event.getAction() != event.ACTION_UP) {

                float displacement = (float) Math.sqrt(Math.pow(event.getX() - centerX, 2)
                        + Math.pow(event.getY() - centerY, 2));

                if (displacement < baseRadius) {
                    drawJoystick(event.getX(), event.getY());
                    joystickCallback.onJoystickMoved((event.getX() - centerX) / baseRadius,
                            (event.getY() - centerY) / baseRadius);
                } else {
                    float ratio = baseRadius / displacement;
                    float constrainedX = centerX + (event.getX() - centerX) * ratio;
                    float constrainedY = centerY + (event.getY() - centerY) * ratio;
                    drawJoystick(constrainedX, constrainedY);
                    joystickCallback.onJoystickMoved((constrainedX - centerX) / baseRadius,
                            (constrainedY - centerY) / baseRadius);
                }
            } else {
                drawJoystick(centerX, centerY);
                joystickCallback.onJoystickMoved(0, 0);
            }
        }

        return true;
    }

    public interface JoystickListener {
        void onJoystickMoved(float xPercent, float yPercent);
    }
}

