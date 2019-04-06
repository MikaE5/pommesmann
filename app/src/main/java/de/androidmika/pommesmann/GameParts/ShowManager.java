package de.androidmika.pommesmann.GameParts;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import de.androidmika.pommesmann.GameParts.Powerups.Powerup;

public class ShowManager {

    void showLasers(ArrayList<Laser> lasers, Canvas canvas) {
        try {
            for (Laser laser : lasers) {
                laser.show(canvas);
            }
        } catch (ConcurrentModificationException e) {
            e.printStackTrace();
        }
    }

    void showBoxes(ArrayList<Box> boxes, Canvas canvas) {
        for (Box box : boxes) {
            box.show(canvas);
        }
    }

    void showPowerups(ArrayList<Powerup> powerups, Canvas canvas) {
        for (Powerup powerup : powerups) {
            powerup.show(canvas);
        }
    }

    void showPlayer(Player player, Canvas canvas) {
        player.show(canvas);
    }



}
