package de.androidmika.pommesmann.GameParts.Game;

import android.graphics.Canvas;

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



}
