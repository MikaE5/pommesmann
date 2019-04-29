package de.androidmika.pommesmann.GameParts.User;


import android.graphics.Canvas;

import java.util.ArrayList;


import de.androidmika.pommesmann.GameParts.Laser;
import de.androidmika.pommesmann.GameParts.Player;
import de.androidmika.pommesmann.GameParts.Powerups.HealthPowerup;
import de.androidmika.pommesmann.GameParts.Powerups.LaserPowerup;
import de.androidmika.pommesmann.GameParts.Powerups.Powerup;
import de.androidmika.pommesmann.ShopDatabase.ShopHelper;
import de.androidmika.pommesmann.Vec;

public class User {

    private UserAttributes attributes = new UserAttributes();

    public Player player = new Player();
    public ArrayList<Laser> lasers = new ArrayList<>();
    public ArrayList<Powerup> powerups = new ArrayList<>();

    private float powerupR;
    private float powerupWidth;


    // update HitDamage at beginning of each round
    public void updateHitDamage() {
        attributes.updateHitDamage();
    }

    public void updateAttributes() {
        attributes.update();
    }

    public void setParams(float scale, float width, float height) {
        setPlayerParams(scale, width, height);
        setPowerupParams(scale);
    }

    private void setPlayerParams(float scale, float width, float height) {
        player.setPos(width / 2, height / 2);
        player.setR(48 * scale);
        player.setMaxVel(0.09f * 48 * scale);

        player.setHealthLoss(attributes.healthLoss);
    }

    private void setPowerupParams(float scale) {
        powerupR = 40 * scale;
        powerupWidth = 80 * scale;
    }


    // gets the difficulty set by Secret of Pommesmann
    public int getDifficulty() {
        return attributes.difficulty;
    }

    public void newPowerup(float width, float height) {
        if (Math.random() < attributes.chanceOfPowerup) {
            addNewPowerup(width, height);
        }
    }

    private void addNewPowerup(float width, float height) {
        if (attributes.availablePowerupsCount > 0) {
            String type = attributes.getRandomPowerup();

            if (type.equals(ShopHelper.HEALTH_POWERUP)) {
                powerups.add(new HealthPowerup(width, height, powerupWidth, attributes.HEALTH_POWERUP_DURATION));
            } else if (type.equals(ShopHelper.LASER_POWERUP)) {
                powerups.add(new LaserPowerup(width, height, powerupR, attributes.LASER_POWERUP_DURATION));
            }
        }
    }

    public int getPoints() {
        return attributes.points;
    }


    public void increasePoints() {
        /* idea for secret of pommesmann
        // get randomly 2 points for a box; chance is based on level of Secret of Pommesmann ( = difficulty)
        if (attributes.difficulty > 0) {
            double map = (double) attributes.difficulty / (double) (20 + attributes.difficulty);
            if (Math.random() < map) {
                attributes.points += 2;
                return true;
            }
            else {
                attributes.points += 1;
                return false;
            }
        } else {
            attributes.points += 1;
            return false;
        }
        */
        attributes.points += 1;
    }




    private int getMaxLaser() {
        return attributes.currentMaxLaser;
    }

    public float getHitDamage() {
        return attributes.hitDamage;
    }

    public float getHitBonus() {
        return attributes.hitBonus;
    }

    public float getHealthPowerupHealing() { return attributes.healthPowerupHealing; }


    public void setLaserPowerupDuration() {
        attributes.laserPowerupDuration = attributes.LASER_POWERUP_DURATION;
    }

    public void increaseMaxLaser() {
        attributes.currentMaxLaser++;
    }


    // tries to fire a new laser and returns true if that could happen
    public boolean fire() {
        boolean success = false;
        if (lasers.size() < getMaxLaser()) {
            Laser newLaser = new Laser(player, attributes.laserSpeedFactor);
            lasers.add(0, newLaser);
            success = true;
        }
        return success;
    }

    public float getHealth() {
        return player.getHealth();
    }


    public float getPlayerR() {
        return player.getR();
    }

    public Vec getPlayerPos() {
        return player.getPos();
    }

    public void movePlayer(float xPercent, float yPercent) {
        player.setAcc(xPercent, yPercent);
    }

}
