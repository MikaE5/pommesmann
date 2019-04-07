package de.androidmika.pommesmann.GameParts;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import de.androidmika.pommesmann.Vec;

public class Player {

    private Vec pos;
    private float r;
    private Vec vel;
    private float maxVel;
    private Vec acc;
    private float health;
    private float healthLoss;

    PlayerAttributes attributes;


    Player() {
        this.pos = new Vec(0, 0);
        this.r = 0;
        this.vel = new Vec(0, 0);
        this.acc = new Vec(0, 0);
        this.maxVel = 1; // initialize it with 1, so it is not 0
        this.health = 255f;
        this.healthLoss = 0f;

        attributes = new PlayerAttributes();
    }

    void setPos(float x, float y) {
        this.pos.x = x;
        this.pos.y = y;
    }

    public void setR(float r) {
        this.r = r;
    }

    void setMaxVel(float maxVel) {
        this.maxVel = maxVel;
    }

    public void setAcc(float x, float y) {
        // x and y are between 0 and 1
        this.acc.x = 0.2f * maxVel * x;
        this.acc.y = 0.2f * maxVel * y;
        //this.acc.x = 0.5f * x;
        //this.acc.y = 0.5f * y;
    }

    Vec getVel() {
        return vel.copy();
    }

    Vec getPos() {
        return pos.copy();
    }

    public float getR() {
        return r;
    }

    float getMaxVel() {
        return maxVel;
    }

    void setHealthLoss(float healthLoss) {
        this.healthLoss = healthLoss;
    }

    void changeHealth(float amount) {
        health += amount;
        if (health > 255) health = 255f;
    }

    float getHealth() {
        return health;
    }

    void update(float width, float height) {
        vel.add(acc);
        vel.limit(maxVel);
        pos.add(vel);
        health -= healthLoss;
        constrain(width, height);
    }

    private void constrain(float width, float height) {
        if (pos.x + r > width) {
            pos.x = width - r;
            vel.x *= -0.3;  // bouncy-ball effect
        }
        if (pos.y + r > height) {
            pos.y = height - r;
            vel.y *= -0.3;
        }
        if (pos.x - r < 0) {
            pos.x = r;
            vel.x *= -0.3;
        }
        if (pos.y - r < 0) {
            pos.y = r;
            vel.y *= -0.3;
        }
    }

    double getAngle(Vec a, Vec b) {
        double dot = (double) a.x * b.x + a.y * b.y;
        double det = (double) a.x * b.y - a.y * b.x;
        return Math.atan2(det, dot);
    }

    public void show(Canvas canvas) {

        canvas.save();
        Paint paint = new Paint();

        if (vel.x != 0 && vel.y != 0) {
            Vec tempVec = new Vec(1, 0);
            float angle = (float) Math.toDegrees(getAngle(tempVec, vel)) + 90f;
            canvas.rotate(angle, pos.x, pos.y);
        }

        int red = 255 - (int) health;
        int green = (int) health;
        if (red > 255) red = 255;
        if (red < 0) red = 0;
        if (green < 0) green = 0;
        if (green > 255) green = 255;
        paint.setColor(Color.rgb(red, green, 0));
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawCircle(pos.x, pos.y, r, paint);

        float temp = 0.65f * r;
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(0.1f * temp);
        paint.setColor(Color.BLACK);
        canvas.drawCircle(pos.x, pos.y - temp, 0.3f * temp, paint);

        canvas.restore();
    }

}
