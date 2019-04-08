package de.androidmika.pommesmann.GameParts.User;

import java.util.ArrayList;

import de.androidmika.pommesmann.GameParts.Powerups.Powerup;

import static de.androidmika.pommesmann.App.getContext;

class UserAttributes {

    private UserHelper userHelper;

    // general game settings
    int difficulty;
    int points = 0;

    // settings for collision
    float healthLoss = 0.1f;
    float hitBonus = 25;
    float hitDamage;

    //Powerups
    ArrayList<String> availablePowerups;
    double chanceOfPowerup;

    // setting for HealthPowerup
    int healthPowerupDuration;
    float healthPowerupHealing;

    // settings for LaserPowerup
    final int MAX_LASERS = 3;
    int maxLasers = 3;
    int laserPowerupDuration = 0;     // The current Duration in the actual game
    final int LASER_POWERUP_DURATION; //The max Duration set by the PowerupLevel




    UserAttributes() {
        userHelper = new UserHelper(getContext());
        difficulty = userHelper.difficulty;
        hitDamage = 40 + 4 * difficulty;

        availablePowerups = userHelper.availablePowerups;
        chanceOfPowerup = userHelper.chanceOfPowerup;

        healthPowerupDuration = userHelper.healthPowerupDuration;
        healthPowerupHealing = userHelper.healthPowerupHealing;
        LASER_POWERUP_DURATION = userHelper.laserPowerupDuration;
    }

    String getRandomPowerup() {
        return userHelper.getRandomPowerup();
    }

    void update() {
        hitDamage += 2 + 2 * difficulty;

        if (maxLasers > MAX_LASERS) {
            laserPowerupDuration--;
            if (laserPowerupDuration < 0) {
                maxLasers--;
            }
        }
    }
}
