package de.androidmika.pommesmann.GameParts;

import android.content.Context;

import java.util.ArrayList;

import de.androidmika.pommesmann.GameParts.Powerups.HealthPowerup;
import de.androidmika.pommesmann.GameParts.Powerups.LaserPowerup;
import de.androidmika.pommesmann.GameParts.Powerups.Powerup;

public class CollisionManager {

    public interface CollisionSoundManager {
        void hitSound();
        void boxSound();
        void powerupSound();
    }

    private CollisionSoundManager soundCallback;

    private GameHelper gameHelper;

    private ArrayList<Laser> removableLasers = new ArrayList<>();
    private ArrayList<Box> removableBoxes = new ArrayList<>();
    private ArrayList<Powerup> removablePowerups = new ArrayList<>();

    CollisionManager(Context context) {
        if (context instanceof CollisionSoundManager) {
            soundCallback = (CollisionSoundManager) context;
        }
        gameHelper = new GameHelper(context);
    }


    // does all the collisionDetection and returns ArrayList of Boxes, which change to animating
    ArrayList<Box> collisionDetection(Player player,
                            ArrayList<Box> boxes,
                            ArrayList<Laser> lasers,
                            ArrayList<Powerup> powerups) {

        ArrayList<Box> animationBoxes = new ArrayList<>();
        Box animBox;

        for (Box box : boxes) {
            // one laser hits box
            for (Laser laser : lasers) {
                animBox = laserHitsBox(player, laser, box);
                if (animBox != null) {
                    animationBoxes.add(animBox);
                }
            }
            // player hits box
            animBox = playerHitsBox(player, box);
            if (animBox != null) {
                animationBoxes.add(animBox);
            }
        }
        // laser hits player
        for (Laser laser : lasers) {
            laserHitsPlayer(player, laser);
        }
        // player hits powerup
        for (Powerup powerup : powerups) {
            playerHitsPowerup(player, powerup);
        }
        lasers.removeAll(removableLasers);
        boxes.removeAll(removableBoxes);
        powerups.removeAll(removablePowerups);
        removableLasers.clear();
        removableBoxes.clear();
        removablePowerups.clear();

       return animationBoxes;
    }


    private void laserHitsPlayer(Player player, Laser laser) {
        if (laser.getWallCount() != 0) {

            float dx = laser.getPos().x - player.getPos().x;
            float dy = laser.getPos().y - player.getPos().y;
            float dist = (float) Math.sqrt(dx * dx + dy * dy);

            float minDist = laser.getR() + player.getR();

            if (dist < minDist) {
                removableLasers.add(laser);
                player.changeHealth(-0.5f * player.attributes.hitDamage);
            }
        }
    }

    // checks if the laser of the player hits the box
    // returns the box, if it is hit, so it can change to animationBoxes
    private Box laserHitsBox(Player player, Laser laser, Box box) {

            if (circleInSquare(laser.getPos().x, laser.getPos().y, laser.getR(),
                    box.getPos().x, box.getPos().y, box.getLen())) {
                player.changeHealth(player.attributes.hitBonus);
                removableLasers.add(laser);

                box.setToAnimating();
                removableBoxes.add(box);

                player.attributes.points += 1;
                soundCallback.hitSound();

                return box;
            }

        return null;
    }


    // checks if the player hits the box
    // returns the box, if it is hit, so it can change to animationBoxes
    private Box playerHitsBox(Player player, Box box) {

        if (circleInSquare(player.getPos().x, player.getPos().y, player.getR(),
                box.getPos().x, box.getPos().y, box.getLen())) {
            player.changeHealth(-1f * player.attributes.hitDamage);

            box.setToAnimating();
            removableBoxes.add(box);

            soundCallback.boxSound();

            return box;
        }
        return null;
    }


    // check if player hits powerup, based on the type of the powerup
    private void playerHitsPowerup(Player player, Powerup powerup) {
        if (powerup instanceof HealthPowerup) {
            if (circleInSquare(player.getPos().x, player.getPos().y, player.getR(),
                    powerup.getPos().x, powerup.getPos().y, powerup.getLen())) {
                player.changeHealth(gameHelper.healthPowerupHealing);

                removablePowerups.add(powerup);
                soundCallback.powerupSound();
            }
        } else if (powerup instanceof LaserPowerup) {
            if (circleInCircle(player.getPos().x, player.getPos().y, player.getR(),
                    powerup.getPos().x, powerup.getPos().y, powerup.getLen())) {
                player.attributes.maxLasers++;
                player.attributes.laserDuration = gameHelper.laserPowerupDuration;

                removablePowerups.add(powerup);
                soundCallback.powerupSound();
            }
        }
    }

    boolean circleInSquare(float cx, float cy, float cr, float sx, float sy, float slen) {
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
