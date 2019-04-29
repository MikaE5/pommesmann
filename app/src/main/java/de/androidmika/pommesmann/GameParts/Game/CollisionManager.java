package de.androidmika.pommesmann.GameParts.Game;

import android.content.Context;

import java.util.ArrayList;

import de.androidmika.pommesmann.GameParts.Box;
import de.androidmika.pommesmann.GameParts.Laser;
import de.androidmika.pommesmann.GameParts.Player;
import de.androidmika.pommesmann.GameParts.Powerups.HealthPowerup;
import de.androidmika.pommesmann.GameParts.Powerups.LaserPowerup;
import de.androidmika.pommesmann.GameParts.Powerups.Powerup;
import de.androidmika.pommesmann.GameParts.User.User;
import de.androidmika.pommesmann.Vec;

public class CollisionManager {

    public interface CollisionSoundManager {
        void hitSound();
        void boxSound();
        void powerupSound();
    }

    private CollisionSoundManager soundCallback;


    private ArrayList<Laser> removableLasers = new ArrayList<>();
    private ArrayList<Box> removableBoxes = new ArrayList<>();
    private ArrayList<Powerup> removablePowerups = new ArrayList<>();

    CollisionManager(Context context) {
        if (context instanceof CollisionSoundManager) {
            soundCallback = (CollisionSoundManager) context;
        }
    }


    // does all the collisionDetection and returns ArrayList of Boxes, which change to animating
    ArrayList<Box> collisionDetection(ArrayList<Box> boxes, User user) {

        ArrayList<Box> animationBoxes = new ArrayList<>();
        Box animBox;
        for (Box box : boxes) {
            // one laser hits box
            for (Laser laser : user.lasers) {
                // animBox: the box that is hit by the laser and is then set to animating
                animBox = laserHitsBox(user.player, laser, box, user.getHitBonus());
                if (animBox != null) {
                    // laser hit a box
                    /* idea for secret of pommesmann
                    if (user.increasePoints()) {
                        animBox.setToSpecialAnimating();
                    }
                    */
                    user.increasePoints();
                    animationBoxes.add(animBox);
                }
            }

            // player hits box
            animBox = playerHitsBox(user.player, box, user.getHitDamage());
            if (animBox != null) {
                animationBoxes.add(animBox);
            }
        }
        // laser hits player
        for (Laser laser : user.lasers) {
            laserHitsPlayer(user.player, laser, user.getHitDamage());
        }
        // player hits powerup
        for (Powerup powerup : user.powerups) {
            if (playerHitsPowerup(user.player, powerup, user.getHealthPowerupHealing())) {
                user.increaseMaxLaser();
                user.setLaserPowerupDuration();
            }
        }
        user.lasers.removeAll(removableLasers);
        boxes.removeAll(removableBoxes);
        user.powerups.removeAll(removablePowerups);
        removableLasers.clear();
        removableBoxes.clear();
        removablePowerups.clear();

       return animationBoxes;
    }


    private void laserHitsPlayer(Player player, Laser laser, float hitDamage) {
        if (laser.getWallCount() != 0) {

            float dx = laser.getPos().x - player.getPos().x;
            float dy = laser.getPos().y - player.getPos().y;
            float dist = (float) Math.sqrt(dx * dx + dy * dy);

            float minDist = laser.getR() + player.getR();

            if (dist < minDist) {
                removableLasers.add(laser);
                player.changeHealth(-0.5f * hitDamage);
            }
        }
    }

    // checks if the laser of the player hits the box
    // returns the box, if it is hit, so it can change to animationBoxes
    private Box laserHitsBox(Player player, Laser laser, Box box, float hitBonus) {
            if (circleInSquare(laser.getPos().x, laser.getPos().y, laser.getR(),
                    box.getPos().x, box.getPos().y, box.getLen())) {
                player.changeHealth(hitBonus);
                removableLasers.add(laser);

                box.setToAnimating();
                removableBoxes.add(box);

                soundCallback.hitSound();

                return box;
            }

        return null;
    }


    // checks if the player hits the box
    // returns the box, if it is hit, so it can change to animationBoxes
    private Box playerHitsBox(Player player, Box box, float hitDamage) {

        if (circleInSquare(player.getPos().x, player.getPos().y, player.getR(),
                box.getPos().x, box.getPos().y, box.getLen())) {
            player.changeHealth(-1f * hitDamage);

            box.setToAnimating();
            removableBoxes.add(box);

            soundCallback.boxSound();

            return box;
        }
        return null;
    }


    // check if player hits powerup, based on the type of the powerup
    // returns true if the player hits laserpowerup to set the laserduration in user
    private boolean playerHitsPowerup(Player player, Powerup powerup, float healing) {
        boolean laserPowerup = false;
        if (powerup instanceof HealthPowerup) {
            if (circleInSquare(player.getPos().x, player.getPos().y, player.getR(),
                    powerup.getPos().x, powerup.getPos().y, powerup.getLen())) {
                player.changeHealth(healing);

                removablePowerups.add(powerup);
                soundCallback.powerupSound();
            }
        } else if (powerup instanceof LaserPowerup) {
            if (circleInCircle(player.getPos().x, player.getPos().y, player.getR(),
                    powerup.getPos().x, powerup.getPos().y, powerup.getLen())) {
                laserPowerup = true;

                removablePowerups.add(powerup);
                soundCallback.powerupSound();
            }
        }
        return laserPowerup;
    }

    boolean isBoxNearby(Player player, Box box, float radius) {
        Vec pos = player.getPos();
        Vec boxPos = box.getPos();
        return circleInSquare(pos.x, pos.y, radius * player.getR(), boxPos.x, boxPos.y, box.getLen());
    }

    private boolean circleInSquare(float cx, float cy, float cr, float sx, float sy, float slen) {
        float dx = cx - Math.max(sx, Math.min(cx, sx + slen));
        float dy = cy - Math.max(sy, Math.min(cy, sy + slen));

        return (dx * dx + dy * dy < cr * cr);
    }

    private boolean circleInCircle(float ax, float ay, float ar, float bx, float by, float br) {
        float dx = ax - bx;
        float dy = ay - by;
        double dist = Math.sqrt(dx * dx + dy * dy);

        return dist < ar + br;
    }
}
