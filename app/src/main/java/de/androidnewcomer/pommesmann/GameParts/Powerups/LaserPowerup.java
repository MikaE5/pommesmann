package de.androidnewcomer.pommesmann.GameParts.Powerups;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class LaserPowerup extends Powerup {

    public LaserPowerup(float width, float height, float radius, int dur) {
        // call super-constructor for circle powerups
        super(width, height, dur, radius);
    }

    @Override
    public void show(Canvas canvas) {
        Paint paint = new Paint();

        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(Color.rgb(255, 215, 0));
        canvas.drawCircle(pos.x, pos.y, len, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(pos.x, pos.y, 0.95f * len, paint);

        paint.setColor(Color.rgb(255, 215, 0));
        canvas.drawCircle(pos.x, pos.y, len * 0.3f, paint);
    }
}
