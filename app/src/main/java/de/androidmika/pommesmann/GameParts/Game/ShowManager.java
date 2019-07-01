package de.androidmika.pommesmann.GameParts.Game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import de.androidmika.pommesmann.GameParts.Box;
import de.androidmika.pommesmann.GameParts.Laser;
import de.androidmika.pommesmann.GameParts.Player;
import de.androidmika.pommesmann.GameParts.Powerups.Powerup;

public class ShowManager {

    static void showLasers(ArrayList<Laser> lasers, Canvas canvas) {
        try {
            for (Laser laser : lasers) {
                laser.show(canvas);
            }
        } catch (ConcurrentModificationException e) {
            e.printStackTrace();
        }
    }

    static void showBoxes(ArrayList<Box> boxes, Canvas canvas) {
        for (Box box : boxes) {
            box.show(canvas);
        }
    }

    static void showPowerups(ArrayList<Powerup> powerups, Canvas canvas) {
        for (Powerup powerup : powerups) {
            powerup.show(canvas);
        }
    }

    static void showPlayer(Player player, Canvas canvas) {
        player.show(canvas);
    }

    static void showHealthbar(Player player, Canvas canvas) {
        float width = canvas.getWidth();
        float height = canvas.getHeight();

        // health is between 0 and 255
        float health = player.getHealth();
        float drawWidth = width / 255 * health;

        int red = 255 - (int) health;
        int green = (int) health;
        if (red > 255) red = 255;
        if (red < 0) red = 0;
        if (green < 0) green = 0;
        if (green > 255) green = 255;
        Paint paint = new Paint();
        paint.setColor(Color.rgb(red, green, 0));

        canvas.drawRect(0, height - 15, drawWidth, height - 5, paint);

    }



}
