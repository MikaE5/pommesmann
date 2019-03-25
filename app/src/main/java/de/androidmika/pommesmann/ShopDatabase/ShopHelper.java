package de.androidmika.pommesmann.ShopDatabase;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.androidmika.pommesmann.App;
import de.androidmika.pommesmann.R;

public class ShopHelper {

    public static final int MAX_LEVEL = 5;
    public static final int PRICE_INCREASE = 25;

    public static final String POWERUP_CHANCE = "Powerup Chance";
    static final String POWERUP_CHANCE_DESCRIPTION = "Increase the percentage of a new " +
            "powerup at the beginning of a round";

    public static final String HEALTH_POWERUP = "Health Powerup";
    static final String HEALTH_POWERUP_DESCRIPTION = "Gives you a Healthboost! Upgrade" +
            " for a greater boost and a longer visibility of the powerup";

    public static final String LASER_POWERUP = "Laser Powerup";
    static final String LASER_POWERUP_DESCRIPTION = "Shoot one more Laser! Upgrade" +
            " for a longer visibility of the powerup";


    public static final String SECRET_OF_POMMESMANN = "Secret of POMMESMANN";
    static final String SECRET_OF_POMMESMANN_DESCRIPTION ="Discover the truth about POMMESMANN";


    public static final List<Item> ITEMS = Collections.unmodifiableList(
            Arrays.asList(
                    new Item(
                            HEALTH_POWERUP, HEALTH_POWERUP_DESCRIPTION,
                            0, 10,
                            20,
                            R.drawable.healthpowerupimage),
                    new Item(LASER_POWERUP, LASER_POWERUP_DESCRIPTION,
                            0, 20,
                            75,
                            R.drawable.laserpowerupimage),
                    new Item(POWERUP_CHANCE, POWERUP_CHANCE_DESCRIPTION,
                            0, 30,
                            100,
                            R.drawable.powerupchanceimage),
                    new Item(SECRET_OF_POMMESMANN, SECRET_OF_POMMESMANN_DESCRIPTION,
                            0, 60,
                            3141,
                            R.drawable.pommesmann)
            )
    );

}
