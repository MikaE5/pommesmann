package de.androidnewcomer.pommesmann.ShopDatabase;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ShopHelper {

    public static final int PRICE_INCREASE = 25;

    public static final String POWERUP_CHANCE = "Powerup Chance";
    public static final String POWERUP_CHANCE_DESCRIPTION = "Increase the percentage of a new " +
            "powerup at the beginning of a round";

    public static final String HEALTH_POWERUP = "Health Powerup";
    public static final String HEALTH_POWERUP_DESCRIPTION = "Gives you a Healthboost! Upgrade" +
            " for a greater boost and a longer visibility of the powerup";

    public static final String LASER_POWERUP = "Laser Powerup";
    public static final String LASER_POWERUP_DESCRIPTION = "Shoot one more Laser! Upgrade" +
            " for a longer visibility of the powerup";
    public static final int LASER_POWERUP_RESTRICTION = 40;

    public static final List<Item> ITEMS = Collections.unmodifiableList(
            Arrays.asList(
                    new Item(POWERUP_CHANCE, 0, 150),
                    new Item(HEALTH_POWERUP, 0, 10),
                    new Item(LASER_POWERUP, 0, 100)
            )
    );
}
