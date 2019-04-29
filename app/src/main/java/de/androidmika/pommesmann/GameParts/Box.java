package de.androidmika.pommesmann.GameParts;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import de.androidmika.pommesmann.App;
import de.androidmika.pommesmann.R;
import de.androidmika.pommesmann.Vec;

public class Box {

    private Vec pos;
    private Vec vel;
    private float len;
    private boolean animating = false;


    public Box(float width, float height, float len, float maxVel) {
        initPos(width, height, len);

        // vel always between 0.25*maxVel and 1*maxVel
        float tempX = 0.25f * maxVel + (float) Math.random() * 0.75f * maxVel;
        float tempY = 0.25f * maxVel + (float) Math.random() * 0.75f * maxVel;
        this.vel = new Vec(tempX, tempY);

        this.len = len;
    }

    public void setToAnimating() {
        animating = true;
    }


    private void initPos(float width, float height, float length) {
        // set the position of the box near to one of the edges of the canvas
        int random = (int) (Math.random() * 4);
        float tempX = 0;
        float tempY = 0;
        switch (random) {
            case 0: // TOP
                tempY = 0.5f * length;
                tempX = (float) Math.random() * width;
                break;
            case 1: // RIGHT
                tempX = width - 1.5f * length;
                tempY = (float) Math.random() * height;
                break;
            case 2: // BOTTOM
                tempY = height - 1.5f * length;
                tempX = (float) Math.random() * width;
                break;
            case 3: // LEFT
                tempX = 0.5f * length;
                tempY = (float) Math.random() * height;
                break;
        }
        this.pos = new Vec(tempX, tempY);
    }

    public Vec getPos() {
        return pos.copy();
    }

    public float getLen() {
        return len;
    }

    public void update(float width, float height) {
        if (animating) {
           removeAnimation(2);
        } else {
            pos.add(vel);
            constrain(width, height);
        }
    }

    private void constrain(float width, float height) {
        if (pos.x + len > width) {
            pos.x = width - len;
            vel.x *= -1;  // bouncy-ball effect
        }
        if (pos.y + len > height) {
            pos.y = height - len;
            vel.y *= -1;
        }
        if (pos.x < 0) {
            pos.x = 0;
            vel.x *= -1;
        }
        if (pos.y < 0) {
            pos.y = 0;
            vel.y *= -1;
        }
    }

    public void show(Canvas canvas) {
        if (animating) {
            animationShow(canvas);
        } else {
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setStrokeWidth(len * 0.05f);
            paint.setColor(App.getContext().getResources().getColor(R.color.boxColor));
            canvas.drawRect(pos.x, pos.y, pos.x + len, pos.y + len, paint);
        }
    }

    private void animationShow(Canvas canvas) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(App.getContext().getResources().getColor(R.color.boxColor));
        canvas.drawRect(pos.x, pos.y, pos.x + len, pos.y + len, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.argb(100, 255, 0, 0));
        paint.setStrokeWidth(len * 0.1f);
        canvas.drawRect(pos.x, pos.y, pos.x + len, pos.y + len, paint);
    }

    private void removeAnimation(float animSpeed) {
        pos.x += animSpeed;
        pos.y += animSpeed;
        len -= 2 * animSpeed;
    }

    public boolean animationFinished() {
        return (len <= 0);
    }
}
