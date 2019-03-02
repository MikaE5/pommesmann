package de.androidmika.pommesmann;

public class Vec {
    public float x;
    public float y;

    public Vec (float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void add(Vec vec) {
        this.x += vec.x;
        this.y += vec.y;
    }

    public void limit(float limit) {
        float len = (float) Math.sqrt(x * x + y * y);

        if (len > limit) {
            this.mult(1 / len);
            this.mult(limit);
        }
    }

    public void mult(float number) {
        this.x *= number;
        this.y *= number;
    }

    public Vec copy() {
        return new Vec(x,y);
    }

    public void constrain(float width, float height) {
        if (x > width) {
            x = width;
        }
        if (y> height) {
            y = height;
        }
        if (x < 0) {
            x = 0;
        }
        if (y < 0) {
            y = 0;
        }
    }


}



