package de.androidmika.pommesmann.GameParts;


import android.content.Context;

import java.util.ArrayList;
import java.util.Random;

import de.androidmika.pommesmann.ShopDatabase.Item;
import de.androidmika.pommesmann.ShopDatabase.ShopDatabaseHelper;
import de.androidmika.pommesmann.ShopDatabase.ShopHelper;

public class GameHelper {


    double chanceOfPowerup;
    ArrayList<String> availablePowerups;
    int difficulty;

    int healthPowerupDuration;
    float healthPowerupHealing;

    int laserPowerupDuration;


    GameHelper(Context context) {
        ShopDatabaseHelper dbHelper = ShopDatabaseHelper.getInstance(context);
        availablePowerups = new ArrayList<>();

        Item powerupChance = dbHelper.getItemByName(ShopHelper.POWERUP_CHANCE);
        chanceOfPowerup = 0.40 + 0.1f * powerupChance.getLevel();
        if (chanceOfPowerup > 1) chanceOfPowerup = 1;

        difficulty = dbHelper.getItemByName(ShopHelper.SECRET_OF_POMMESMANN).getLevel();

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
