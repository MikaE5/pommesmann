package de.androidnewcomer.pommesmann.GameParts;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import de.androidnewcomer.pommesmann.Vec;

public class Laser {

    private Vec pos;
    private Vec vel;
    private float r;
    private int wallCount;
    private int maxWallCount;

    public Laser(Vec pos, Vec vel, float r) {
        this.pos = pos;
        this.vel = vel;
        this.r = r;
        wallCount = 0;
        maxWallCount = 2;
    }

    public Vec getPos() {
        return pos.copy();
    }

    public float getR() {
        return r;
    }

    public int getWallCount() {
        return wallCount;
    }

    public void update(float width, float height) {
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


    public boolean removeLaser() {
        return (wallCount >= maxWallCount);
    }
}
