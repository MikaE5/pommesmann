package de.androidmika.pommesmann.GameParts;

import java.util.ArrayList;

import de.androidmika.pommesmann.GameParts.Powerups.Powerup;
import de.androidmika.pommesmann.GameParts.User.User;

public class UpdateManager {

    // Helper ArrayLists for removing elements
    private ArrayList<Laser> removableLasers = new ArrayList<>();
    private ArrayList<Box> removableBoxes = new ArrayList<>();
    private ArrayList<Powerup> removablePowerups = new ArrayList<>();

    private final float animSpeed = 2;

    private boolean assignedSize = false;
    private float width;
    private float height;

    void assignSize(float width, float height) {
        this.width = width;
        this.height = height;
        assignedSize = true;
    }

    boolean isSizeAssigned() {
        return assignedSize;
    }


    void updateLasers(ArrayList<Laser> lasers) {
        for (Laser laser : lasers) {
            laser.update(width, height);
            if (laser.removeLaser()) {
                removableLasers.add(laser);
            }
        }
        lasers.removeAll(removableLasers);
        removableLasers.clear();
    }


    void updateBoxes(ArrayList<Box> boxes) {
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


    void updatePlayer(Player player) {
        player.update(width, height);
    }

    private void updateUserAttribs(User user) {
        user.updateAttribs();
    }


    void updatePowerups(ArrayList<Powerup> powerups) {
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
