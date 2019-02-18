package de.androidnewcomer.pommesmann.GameParts.Powerups;

import de.androidnewcomer.pommesmann.Vec;

public class Powerup {

    Vec pos;
    float len;
    int duration;

    public Powerup(float width, float height, float length, int dur) {
        float tempX = (float) Math.random() * width;
        float tempY = (float) Math.random() * height;
        this.pos = new Vec(tempX, tempY);
        this.len = length;
        this.pos.constrain(width - len, height - len);
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

    public boolean isRemovable() {
        return duration < 0;
    }
}
