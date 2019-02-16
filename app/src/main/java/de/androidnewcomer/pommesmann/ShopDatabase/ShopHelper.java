package de.androidnewcomer.pommesmann.ShopDatabase;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ShopHelper {

    public static final List<Item> ITEMS = Collections.unmodifiableList(
            Arrays.asList((
                    new Item("HEALTH_POWERUP", 0, 100)
            ))
    );

}
