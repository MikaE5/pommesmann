package de.androidmika.pommesmann.ShopDatabase;

import android.provider.BaseColumns;

class ShopContract {

    private ShopContract() {};

    static final class ItemEntry implements BaseColumns {
        static final String TABLE_NAME = "items";
        static final String COLUMN_NAME = "name";
        static final String COLUMN_DESCRIPTION = "description";
        static final String COLUMN_LEVEL = "level";
        static final String COLUMN_PRICE = "price";
    }

}
