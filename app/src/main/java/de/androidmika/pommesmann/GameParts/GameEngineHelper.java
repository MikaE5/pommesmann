package de.androidmika.pommesmann.GameParts;


import android.content.Context;

import java.util.ArrayList;
import java.util.Random;

import de.androidmika.pommesmann.ShopDatabase.Item;
import de.androidmika.pommesmann.ShopDatabase.ShopDatabaseHelper;
import de.androidmika.pommesmann.ShopDatabase.ShopHelper;

public class GameEngineHelper {

    private ShopDatabaseHelper dbHelper;

    double chanceOfPowerup;
    public ArrayList<String> availablePowerups;

    int healthPowerupLevel;
    int healthPowerupDuration;
    float healthPowerupHealing;

    int laserPowerupLevel;
    int laserPowerupDuration;


    GameEngineHelper(Context context) {
        dbHelper = ShopDatabaseHelper.getInstance(context);
        availablePowerups = new ArrayList<>();

        Item powerupChance = dbHelper.getItemByName(ShopHelper.POWERUP_CHANCE);
        chanceOfPowerup = 0.40 + 0.1f * powerupChance.getLevel();
        if (chanceOfPowerup > 1) chanceOfPowerup = 1;

        Item healthPowerup = dbHelper.getItemByName(ShopHelper.HEALTH_POWERUP);
        healthPowerupLevel = healthPowerup.getLevel();
        healthPowerupDuration = 350 + 50 * healthPowerupLevel;
        healthPowerupHealing = 85 + 15 * healthPowerupLevel;
        if (healthPowerupLevel > 0) availablePowerups.add(ShopHelper.HEALTH_POWERUP);

        Item laserPowerup = dbHelper.getItemByName(ShopHelper.LASER_POWERUP);
        laserPowerupLevel = laserPowerup.getLevel();
        laserPowerupDuration = 300 + 50 * laserPowerupLevel;
        if (laserPowerupLevel > 0) availablePowerups.add(ShopHelper.LASER_POWERUP);
    }

    public String getRandomPowerup() {
        return availablePowerups.get(new Random().nextInt(availablePowerups.size()));
    }
}
