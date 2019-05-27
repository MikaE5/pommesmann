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

    static void showHealthbar(float health, Canvas canvas) {
        // input validation, playerhealth is between 0 and 255
        if (health > 255) health = 255;
        if (health < 0) health = 0;


        float width = canvas.getWidth();
        float height = canvas.getHeight();
        float step = width / 255;

        Paint paint = new Paint();
        float red = 255 - health;
        float green = health;

        paint.setColor(Color.rgb((int)red, (int)green, 0));
        canvas.drawRect(0, height - 4 * step, health * step, height - step, paint);
    }





}
