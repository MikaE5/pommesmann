package de.androidnewcomer.pommesmann.ShopDatabase;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ShopHelper {

    //public static final String POWERUP_CHANCE = "Powerup Percentage";
    public static final String HEALTH_POWERUP = "Health Powerup";

    public static final List<Item> ITEMS = Collections.unmodifiableList(
            Arrays.asList(
                    //new Item(POWERUP_CHANCE, 0, 150),
                    new Item(HEALTH_POWERUP, 0, 100)
            )
    );
}
