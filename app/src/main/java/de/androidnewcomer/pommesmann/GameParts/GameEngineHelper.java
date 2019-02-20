package de.androidnewcomer.pommesmann.GameParts;


import android.content.Context;

import de.androidnewcomer.pommesmann.ShopDatabase.Item;
import de.androidnewcomer.pommesmann.ShopDatabase.ShopDatabaseHelper;
import de.androidnewcomer.pommesmann.ShopDatabase.ShopHelper;

public class GameEngineHelper {

    private ShopDatabaseHelper dbHelper;

    double chanceOfPowerup;

    int healthPowerupLevel;
    int healthPowerupDuration;
    float healthPowerupHealing;

    GameEngineHelper(Context context) {
        dbHelper = ShopDatabaseHelper.getInstance(context);

        Item powerupChance = dbHelper.getItemByName(ShopHelper.POWERUP_CHANCE);
        chanceOfPowerup = 0.40 + 0.1f * powerupChance.getLevel();
        if (chanceOfPowerup > 1) chanceOfPowerup = 1;

        Item healthPowerup = dbHelper.getItemByName(ShopHelper.HEALTH_POWERUP);
        healthPowerupLevel = healthPowerup.getLevel();
        healthPowerupDuration = 350 + 50 * healthPowerupLevel;
        healthPowerupHealing = 85 + 15 * healthPowerupLevel;
    }

}
