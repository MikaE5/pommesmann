package de.androidmika.pommesmann.GameParts.Powerups;

import android.graphics.Canvas;

import de.androidmika.pommesmann.Vec;

public abstract class Powerup {

    Vec pos;
    float len;
    int duration; // duration of visibility

    // square powerups
    Powerup(float width, float height, float length, int dur) {
        float tempX = (float) Math.random() * width;
        float tempY = (float) Math.random() * height;
        this.pos = new Vec(tempX, tempY);
        this.len = length;
        this.pos.constrain(width - len, height - len);
        this.duration = dur;
    }

    // round powerups
    Powerup(float width, float height, int dur, float radius) {
        float tempX = (float) Math.random() * width;
        float tempY = (float) Math.random() * height;
        this.pos = new Vec(tempX, tempY);
        this.len = radius;
        this.pos.constrain(width - len, height - len);
        if (this.pos.x <= len) this.pos.x = len;
        if (this.pos.y <= len) this.pos.y = len;
        this.duration = dur;
    }

    public Vec getPos() {
        return pos.copy();
    }

    public float getLen() {
        return len;
    }

    public void update() {
        duration--;
    }

    public abstract void show(Canvas canvas);

    public boolean isRemovable() {
        return duration < 0;
    }
}
