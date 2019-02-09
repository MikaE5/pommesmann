package de.androidnewcomer.pommesmann.ShopDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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

    public void addItem(String name, int level) {
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(ItemEntry.COLUMN_NAME, name);
            values.put(ItemEntry.COLUMN_LEVEL, level);

            db.insertOrThrow(ItemEntry.TABLE_NAME, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }
}
