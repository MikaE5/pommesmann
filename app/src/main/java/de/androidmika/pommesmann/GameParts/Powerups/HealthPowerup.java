package de.androidmika.pommesmann.GameParts.Powerups;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class HealthPowerup extends Powerup {

    public HealthPowerup(float width, float height, float length, int dur) {
        super(width, height, length, dur);
    }

    @Override
    public  void show(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.rgb(255,0,0));
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawRect(pos.x, pos.y, pos.x + len, pos.y + len, paint);
        paint.setColor(Color.WHITE);
        // horizontal part of white cross
        float xoff = pos.x + 0.125f * len;
        float yoff = pos.y + 0.375f * len;
        canvas.drawRect(xoff, yoff, xoff + 0.75f * len, yoff + 0.25f * len, paint);
        // vertical part of white cross
        xoff = pos.x + 0.375f * len;
        yoff = pos.y + 0.125f * len;
        canvas.drawRect(xoff, yoff, xoff + 0.25f * len, yoff + 0.75f * len, paint);
    }
}
