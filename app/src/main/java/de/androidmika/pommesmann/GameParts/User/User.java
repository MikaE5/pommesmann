package de.androidmika.pommesmann.GameParts.User;

import java.util.ArrayList;

import de.androidmika.pommesmann.GameParts.Laser;
import de.androidmika.pommesmann.GameParts.Player;
import de.androidmika.pommesmann.GameParts.Powerups.Powerup;

public class User {

    private UserAttributes attributes = new UserAttributes();

    Player player = new Player();
    ArrayList<Laser> lasers = new ArrayList<>();
    ArrayList<Powerup> powerups = new ArrayList<>();

    public void setPlayerParams(float scale, float width, float height) {
        player.setPos(width / 2, height / 2);
        player.setR(48 * scale);
        player.setMaxVel(0.09f * 48 * scale);


        // Attributes to User instead of Player
        //player.setHealthLoss();
    }

    public void updateAttributes() {
        attributes.update();
    }

    int getPoints() {
        return attributes.points;
    }

    int getMaxLaser() {
        return attributes.maxLasers;
    }

    float getHitDamage() {
        return attributes.hitDamage;
    }

    float getHitBonus() {
        return attributes.hitBonus;
    }

    void increasePoints(int number) {
        attributes.points += number;
    }

    void setLaserDuration(int duration) {
        attributes.laserDuration = duration;
    }

    void increaseMaxLaser() {
        attributes.maxLasers++;
    }

    private void decreaseMaxLaser() {
        if (attributes.maxLasers > attributes.MAX_LASERS) {
            attributes.maxLasers--;
        }
    }

    void updateLaserPowerup() {
        if (getMaxLaser() > attributes.MAX_LASERS) {
            attributes.laserDuration--;
            if (attributes.laserDuration < 0) {
                decreaseMaxLaser();
            }
        }
    }



}
