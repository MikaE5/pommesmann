package de.androidnewcomer.pommesmann.GameParts;


import android.content.Context;

import de.androidnewcomer.pommesmann.ShopDatabase.Item;
import de.androidnewcomer.pommesmann.ShopDatabase.ShopDatabaseHelper;
import de.androidnewcomer.pommesmann.ShopDatabase.ShopHelper;

public class GameEngineHelper {

    private ShopDatabaseHelper dbHelper;

    double powerupChance;

    int healthPowerupLevel;
    int healthPowerupDuration;
    float healthPowerupHealing;

    GameEngineHelper(Context context) {
        dbHelper = ShopDatabaseHelper.getInstance(context);

        powerupChance = 0.5;

        Item healthPowerup = dbHelper.getItemByName(ShopHelper.HEALTH_POWERUP);
        healthPowerupLevel = healthPowerup.getLevel();
        healthPowerupDuration = 350 + 50 * healthPowerupLevel;
        healthPowerupHealing = 85 + 15 * healthPowerupLevel;
    }

}
