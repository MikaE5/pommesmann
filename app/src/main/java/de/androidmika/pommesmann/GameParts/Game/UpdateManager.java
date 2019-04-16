package de.androidmika.pommesmann.GameParts.Game;

import java.util.ArrayList;

import de.androidmika.pommesmann.GameParts.Box;
import de.androidmika.pommesmann.GameParts.Laser;
import de.androidmika.pommesmann.GameParts.Player;
import de.androidmika.pommesmann.GameParts.Powerups.Powerup;

public class UpdateManager {

    // Helper ArrayLists for removing elements
    private static ArrayList<Laser> removableLasers = new ArrayList<>();
    private static ArrayList<Box> removableBoxes = new ArrayList<>();
    private static ArrayList<Powerup> removablePowerups = new ArrayList<>();

    private final float animSpeed = 2;


    static void updateLasers(ArrayList<Laser> lasers, float width, float height) {
        for (Laser laser : lasers) {
            laser.update(width, height);
            if (laser.removeLaser()) {
                removableLasers.add(laser);
            }
        }
        lasers.removeAll(removableLasers);
        removableLasers.clear();
    }


    static void updateBoxes(ArrayList<Box> boxes, float width, float height) {
        for (Box box : boxes) {
            box.update(width, height);

            // this function can also be used for AnimationBoxes
            if (box.animationFinished()) {
                removableBoxes.add(box);
            }
        }
        boxes.removeAll(removableBoxes);
        removableBoxes.clear();
    }


    static void updatePlayer(Player player, float width, float height) {
        player.update(width, height);
    }


    static void updatePowerups(ArrayList<Powerup> powerups) {
        for (Powerup powerup : powerups) {
            powerup.update();
            if (powerup.isRemovable()) {
                removablePowerups.add(powerup);
            }
        }
        powerups.removeAll(removablePowerups);
        removablePowerups.clear();
    }
}
