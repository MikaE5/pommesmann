package de.androidmika.pommesmann.ShopDatabase;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.androidmika.pommesmann.App;
import de.androidmika.pommesmann.R;

public class ShopHelper {
    private static class PowerupProperty {
        String name;
        int restriction;
        Bitmap image;

        PowerupProperty(String name, int restriction, int id) {
            this.name = name;
            this.restriction = restriction;
            image = BitmapFactory.decodeResource(App.getContext().getResources(), id);
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


    public static final String SECRET_OF_POMMESMANN = "Secret of POMMESMANN";
    public static final String SECRET_OF_POMMESMANN_DESCRIPTION ="Discover the truth about POMMESMANN";


    public static final List<Item> ITEMS = Collections.unmodifiableList(
            Arrays.asList(
                    new Item(HEALTH_POWERUP, 0, 20),
                    new Item(LASER_POWERUP, 0, 100),
                    new Item(POWERUP_CHANCE, 0, 150),
                    new Item(SECRET_OF_POMMESMANN, 0, 1)
            )
    );

    private static final List<PowerupProperty> PROPERTIES = Collections.unmodifiableList(
            Arrays.asList(
                    new PowerupProperty(HEALTH_POWERUP, 10, R.drawable.healthpowerupimage),
                    new PowerupProperty(LASER_POWERUP, 30, R.drawable.laserpowerupimage),
                    new PowerupProperty(POWERUP_CHANCE, 40, R.drawable.powerupchanceimage),
                    new PowerupProperty(SECRET_OF_POMMESMANN, 0, R.drawable.pommesmann)
            )
    );

    public static int getRestrictionByName(String name) {
        for (PowerupProperty powerupRestriction : PROPERTIES) {
            if (powerupRestriction.name.equals(name)) {
                return powerupRestriction.restriction;
            }
        }
        return 0;
    }

    public static Bitmap getImageByName(String name) {
        for (PowerupProperty property : PROPERTIES) {
            if (property.name.equals(name)) {
                return property.image;
            }
         }
         return null;
    }
}
