package de.androidmika.pommesmann.GameParts.User;


import android.content.Context;

import java.util.ArrayList;
import java.util.Random;

import de.androidmika.pommesmann.ShopDatabase.Item;
import de.androidmika.pommesmann.ShopDatabase.ShopDatabaseHelper;
import de.androidmika.pommesmann.ShopDatabase.ShopHelper;

class UserHelper {


    double chanceOfPowerup;
    ArrayList<String> availablePowerups;
    int difficulty;

    int healthPowerupDuration;
    float healthPowerupHealing;
    int laserPowerupDuration;
    float fastLaserSpeed;


    UserHelper(Context context) {
        ShopDatabaseHelper dbHelper = ShopDatabaseHelper.getInstance(context);
        availablePowerups = new ArrayList<>();

        difficulty = dbHelper.getItemByName(ShopHelper.SECRET_OF_POMMESMANN).getLevel();

        Item powerupChance = dbHelper.getItemByName(ShopHelper.POWERUP_CHANCE);
        chanceOfPowerup = 0.40;
        if (powerupChance.getActive()) {
            chanceOfPowerup += 0.1f * powerupChance.getLevel();
        }
        if (chanceOfPowerup > 1) chanceOfPowerup = 1;

        Item fastLaser = dbHelper.getItemByName(ShopHelper.FAST_LASER);
        // Tobi H. empfiehlt: Level 5 = 3-4mal so schnell
        if (fastLaser.getActive()) {
            fastLaserSpeed = 1 + 0.1f * fastLaser.getLevel(); // 1.5 times as fast at level 5
        } else {
            fastLaserSpeed = 1;
        }



        Item healthPowerup = dbHelper.getItemByName(ShopHelper.HEALTH_POWERUP);
        int healthPowerupLevel = healthPowerup.getLevel();
        healthPowerupDuration = 350 + 25 * healthPowerupLevel;
        healthPowerupHealing = 85 + 15 * healthPowerupLevel;
        if (healthPowerupLevel > 0 && healthPowerup.getActive()) {
            availablePowerups.add(ShopHelper.HEALTH_POWERUP);
        }

        Item laserPowerup = dbHelper.getItemByName(ShopHelper.LASER_POWERUP);
        int laserPowerupLevel = laserPowerup.getLevel();
        laserPowerupDuration = 300 + 25 * laserPowerupLevel;
        if (laserPowerupLevel > 0 && laserPowerup.getActive()) {
            availablePowerups.add(ShopHelper.LASER_POWERUP);
        }

    }

    String getRandomPowerup() {
        return availablePowerups.get(new Random().nextInt(availablePowerups.size()));
    }
}
