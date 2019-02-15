package de.androidnewcomer.pommesmann.ShopDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import de.androidnewcomer.pommesmann.ShopDatabase.ShopContract.*;

public class ShopDatabaseHelper extends SQLiteOpenHelper {

    private static ShopDatabaseHelper sInstance;

    public static final String DATABASE_NAME = "shop.db";
    public static final int DATABASE_VERSION = 1;

    private ShopDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized ShopDatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ShopDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_ITEMS_TABLE = "CREATE TABLE " +
                ItemEntry.TABLE_NAME + " (" +
                ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ItemEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                ItemEntry.COLUMN_LEVEL + " INTEGER NOT NULL" +
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

    public void addOrUpdateItem(String name, int level) {
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        try {
            // update row if "name" already exists
            int updatedRows = updateItem(name, level);

            if (updatedRows <= 0) {
                ContentValues values = new ContentValues();
                values.put(ItemEntry.COLUMN_NAME, name);
                values.put(ItemEntry.COLUMN_LEVEL, level);

                db.insertOrThrow(ItemEntry.TABLE_NAME, null, values);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public int updateItem(String name, int level) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ItemEntry.COLUMN_NAME, name);
        values.put(ItemEntry.COLUMN_LEVEL, level);

        String whereClause = ItemEntry.COLUMN_NAME + " = ?";
        String[] whereArgs = new String[]{String.valueOf(name)};

        return db.update(ItemEntry.TABLE_NAME, values, whereClause, whereArgs);
    }

    public Item getItem(String name) {
        Item item = new Item();

        // select all from table should also work ?!
        String selectQuery = "SELECT " + ItemEntry.COLUMN_NAME + ", " + ItemEntry.COLUMN_LEVEL +
                " FROM " + ItemEntry.TABLE_NAME + " WHERE " + ItemEntry.COLUMN_NAME + "=?";

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(selectQuery, new String[]{name});

            cursor.moveToFirst();
            item.setName(cursor.getString(cursor.getColumnIndex(ItemEntry.COLUMN_NAME)));
            item.setLevel(cursor.getInt(cursor.getColumnIndex(ItemEntry.COLUMN_LEVEL)));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return item;
    }

    public List<Item> getAllItems() {
        List<Item> items = new ArrayList<>();

        //select all query
        String selectQuery = "SELECT " + ItemEntry.COLUMN_NAME + ", " + ItemEntry.COLUMN_LEVEL +
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
                    item.setLevel(cursor.getInt(cursor.getColumnIndex(ItemEntry.COLUMN_LEVEL)));

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
    }
}
