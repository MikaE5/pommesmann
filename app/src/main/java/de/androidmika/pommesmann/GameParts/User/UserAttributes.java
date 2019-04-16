package de.androidmika.pommesmann.GameParts.User;

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
    double chanceOfPowerup;
    int availablePowerupsCount;

    //LaserSpeed
    float laserSpeedFactor;

    // setting for HealthPowerup
    int HEALTH_POWERUP_DURATION;
    float healthPowerupHealing;

    // settings for LaserPowerup
    int LASER_POWERUP_DURATION;       //The max Duration set by the PowerupLevel
    int laserPowerupDuration = 0;     // The current Duration in the actual game
    private final int MAX_LASERS = 3;
    int currentMaxLaser = 3;



    UserAttributes() {
        userHelper = new UserHelper(getContext());
        difficulty = userHelper.difficulty;
        hitDamage = 40 + 4 * difficulty;

        chanceOfPowerup = userHelper.chanceOfPowerup;
        availablePowerupsCount = userHelper.availablePowerups.size();

        laserSpeedFactor = userHelper.fastLaserSpeed;

        HEALTH_POWERUP_DURATION = userHelper.healthPowerupDuration;
        healthPowerupHealing = userHelper.healthPowerupHealing;

        LASER_POWERUP_DURATION = userHelper.laserPowerupDuration;
    }

    String getRandomPowerup() {
        return userHelper.getRandomPowerup();
    }

    void update() {
        if (currentMaxLaser > MAX_LASERS) {
            laserPowerupDuration--;
            if (laserPowerupDuration < 0) {
                currentMaxLaser--;
            }
        }
    }

    void updateHitDamage() {
        hitDamage += 2 + 2 * difficulty;
    }
}
