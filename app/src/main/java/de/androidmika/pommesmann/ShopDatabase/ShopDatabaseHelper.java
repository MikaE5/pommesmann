package de.androidmika.pommesmann.ShopDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import de.androidmika.pommesmann.ShopDatabase.ShopContract.*;

public class ShopDatabaseHelper extends SQLiteOpenHelper {

    private static ShopDatabaseHelper sInstance;

    public static final String DATABASE_NAME = "shop.db";
    // if I update when app is already released, I have to change onUpgrade!!!!
    public static final int DATABASE_VERSION = 5;
        // VERSION 2: added COLUMN_PRICE
        // VERSION 3: added COLUMN_DESCRIPTION
        // VERSION 4: added COLUMN_ACTIVE
        // VERSION 5: added COLUMN_RESTRICTION and COLUMN_RESOURCE

    private ShopDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // put all available items in table
        initItems();
    }

    public static synchronized ShopDatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ShopDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public void initItems() {
        for (Item item : ShopHelper.ITEMS) {

            Item itemInTable = getItemByName(item.getName());
            // if item is not in table or if the level is not greater than zero
            // get the initial item in the table
            if (itemInTable.getName() == null || itemInTable.getLevel() <= 0) {
                addOrUpdateItem(item);
            }

        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_ITEMS_TABLE = "CREATE TABLE " +
                ItemEntry.TABLE_NAME + " (" +
                ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ItemEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                ItemEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                ItemEntry.COLUMN_LEVEL + " INTEGER NOT NULL, " +
                ItemEntry.COLUMN_ACTIVE + " INTEGER NOT NULL, " +
                ItemEntry.COLUMN_RESTRICTION + " INTEGER NOT NULL, " +
                ItemEntry.COLUMN_PRICE + " INTEGER NOT NULL, " +
                ItemEntry.COLUMN_RESOURCE + " INTEGER NOT NULL" +
                ");";
        db.execSQL(SQL_CREATE_ITEMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + ItemEntry.TABLE_NAME);
            onCreate(db);
        }
    }

    private ContentValues fillValues(Item item) {
        ContentValues values = new ContentValues();
        values.put(ItemEntry.COLUMN_NAME, item.getName());
        values.put(ItemEntry.COLUMN_DESCRIPTION, item.getDescription());
        values.put(ItemEntry.COLUMN_LEVEL, item.getLevel());
        values.put(ItemEntry.COLUMN_ACTIVE, item.getActive());
        values.put(ItemEntry.COLUMN_RESTRICTION, item.getRestriction());
        values.put(ItemEntry.COLUMN_PRICE, item.getPrice());
        values.put(ItemEntry.COLUMN_RESOURCE, item.getResource());
        return values;
    }

    public void addOrUpdateItem(Item item) {
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        try {
            // update row if "name" already exists
            int updatedRows = updateItem(item);

            if (updatedRows <= 0) {
                db.insertOrThrow(ItemEntry.TABLE_NAME, null, fillValues(item));
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public int updateItem(Item item) {
        SQLiteDatabase db = getWritableDatabase();

        // update based on the item.name
        String whereClause = ItemEntry.COLUMN_NAME + " = ?";
        String[] whereArgs = new String[]{String.valueOf(item.getName())};

        return db.update(ItemEntry.TABLE_NAME, fillValues(item), whereClause, whereArgs);
    }

    public Item getItemByName(String name) {
        Item item = new Item();

        // select all from table should also work ?!
        String selectQuery = "SELECT " +
                ItemEntry.COLUMN_NAME + ", " +
                ItemEntry.COLUMN_DESCRIPTION + ", " +
                ItemEntry.COLUMN_LEVEL + ", " +
                ItemEntry.COLUMN_ACTIVE + ", " +
                ItemEntry.COLUMN_RESTRICTION + ", " +
                ItemEntry.COLUMN_PRICE + ", " +
                ItemEntry.COLUMN_RESOURCE +
                " FROM " + ItemEntry.TABLE_NAME + " WHERE " + ItemEntry.COLUMN_NAME + "=?";

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(selectQuery, new String[]{name});

            try {
                if (cursor.moveToFirst()) {
                    item.setName(cursor.getString(cursor.getColumnIndex(ItemEntry.COLUMN_NAME)));
                    item.setDescription(cursor.getString(cursor.getColumnIndex(ItemEntry.COLUMN_DESCRIPTION)));
                    item.setLevel(cursor.getInt(cursor.getColumnIndex(ItemEntry.COLUMN_LEVEL)));
                    item.setActive(cursor.getInt(cursor.getColumnIndex(ItemEntry.COLUMN_ACTIVE)) == 1);
                    item.setRestriction(cursor.getInt(cursor.getColumnIndex(ItemEntry.COLUMN_RESTRICTION)));
                    item.setPrice(cursor.getInt(cursor.getColumnIndex(ItemEntry.COLUMN_PRICE)));
                    item.setResource(cursor.getInt(cursor.getColumnIndex(ItemEntry.COLUMN_RESOURCE)));
                }
            } catch (CursorIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        } finally {
            cursor.close();
        }
        return item;
    }

    public List<Item> getAllItems() {
        List<Item> items = new ArrayList<>();

        //select all query
        String selectQuery = "SELECT " +
                ItemEntry.COLUMN_NAME + ", " +
                ItemEntry.COLUMN_DESCRIPTION + ", " +
                ItemEntry.COLUMN_LEVEL + ", " +
                ItemEntry.COLUMN_ACTIVE + ", " +
                ItemEntry.COLUMN_RESTRICTION + ", " +
                ItemEntry.COLUMN_PRICE + ", " +
                ItemEntry.COLUMN_RESOURCE +
            " FROM " + ItemEntry.TABLE_NAME;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        db.beginTransaction();

        try {
            // looping through rows and adding to items
            if (cursor.moveToFirst()) {
                do {
                    Item item = new Item();
                    item.setName(cursor.getString(cursor.getColumnIndex(ItemEntry.COLUMN_NAME)));
                    item.setDescription(cursor.getString(cursor.getColumnIndex(ItemEntry.COLUMN_DESCRIPTION)));
                    item.setLevel(cursor.getInt(cursor.getColumnIndex(ItemEntry.COLUMN_LEVEL)));
                    item.setActive(cursor.getInt(cursor.getColumnIndex(ItemEntry.COLUMN_ACTIVE)) == 1);
                    item.setRestriction(cursor.getInt(cursor.getColumnIndex(ItemEntry.COLUMN_RESTRICTION)));
                    item.setPrice(cursor.getInt(cursor.getColumnIndex(ItemEntry.COLUMN_PRICE)));
                    item.setResource(cursor.getInt(cursor.getColumnIndex(ItemEntry.COLUMN_RESOURCE)));

                    items.add(item);
                } while (cursor.moveToNext());
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            cursor.close();
            db.close();
        }

        return items;
    }

    public static void deleteDatabase(Context context) {
        context.getApplicationContext().deleteDatabase(DATABASE_NAME);
        sInstance = null;
    }

}
