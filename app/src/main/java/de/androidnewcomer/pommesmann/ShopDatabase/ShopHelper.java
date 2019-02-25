package de.androidnewcomer.pommesmann.ShopDatabase;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ShopHelper {
    private static class PowerupRestriction{
        String name;
        int restriction;

        PowerupRestriction(String name, int restriction) {
            this.name = name;
            this.restriction = restriction;
        }
    }

    public static final int MAX_LEVEL = 5;
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
                    new Item(HEALTH_POWERUP, 0, 20),
                    new Item(LASER_POWERUP, 0, 100)
            )
    );

    private static final List<PowerupRestriction> RESTRICTIONS = Collections.unmodifiableList(
            Arrays.asList(
                    new PowerupRestriction(POWERUP_CHANCE, 40),
                    new PowerupRestriction(LASER_POWERUP, 30)
            )
    );

    public static int getRestrictionByName(String name) {
        for (PowerupRestriction powerupRestriction : RESTRICTIONS) {
            if (powerupRestriction.name == name) {
                return powerupRestriction.restriction;
            }
        }
        return 0;
    }
}
