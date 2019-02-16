package de.androidnewcomer.pommesmann.ShopDatabase;

import android.provider.BaseColumns;

public class ShopContract {

    private ShopContract() {};

    public static final class ItemEntry implements BaseColumns {
        public static final String TABLE_NAME = "items";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_LEVEL = "level";
        public static final String COLUMN_PRICE = "price";
    }

}
