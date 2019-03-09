package de.androidmika.pommesmann.GameParts;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import de.androidmika.pommesmann.Vec;

public class Laser {

    private Vec pos;
    private Vec vel;
    private float r;
    private int wallCount;
    private int maxWallCount;

    Laser(Player player) {
        this.wallCount = 0;
        this.maxWallCount = 2;


        Vec tempPos = player.getPos();
        Vec tempVel = player.getVel();
        float tempR = player.getR();

        this.r = 0.25f * tempR;

        // normalize tempVel
        float velLength = (float) Math.sqrt(tempVel.x * tempVel.x + tempVel.y * tempVel.y);
        tempVel.mult(1 / velLength);
        Vec tempNormVel = tempVel.copy();

        // setting vel of laser
        tempVel.mult(1.75f * player.getMaxVel());
        this.vel = tempVel;

        // setting pos of laser
        tempNormVel.mult(tempR - this.r);
        tempPos.add(tempNormVel);
        this.pos = tempPos;
    }

    Vec getPos() {
        return pos.copy();
    }

    public float getR() {
        return r;
    }

    int getWallCount() {
        return wallCount;
    }

    void update(float width, float height) {
        pos.add(vel);
        hitWall(width, height);
    }

    public void show(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawCircle(pos.x, pos.y, r, paint);
    }

    private void hitWall(float width, float height) {
        if (pos.x > width) {
            wallCount++;
            if (!removeLaser()) {
                pos.x = width - r;
                vel.x *= -1;  // bouncy-ball effect
            }
            return;
        }
        if (pos.y > height) {
            wallCount++;
            if (!removeLaser()) {
                pos.y = height - r;
                vel.y *= -1;
            }
            return;
        }
        if (pos.x < 0) {
            wallCount++;
            if (!removeLaser()) {
                pos.x = r;
                vel.x *= -1;
            }
            return;
        }
        if (pos.y < 0) {
            wallCount++;
            if (!removeLaser()) {
                pos.y = r;
                vel.y *= -1;
            }
        }
    }


    boolean removeLaser() {
        return (wallCount >= maxWallCount);
    }
}
