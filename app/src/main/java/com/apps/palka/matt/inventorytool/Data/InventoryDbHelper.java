package com.apps.palka.matt.inventorytool.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.apps.palka.matt.inventorytool.Data.InventoryContract.InventoryEntry;

/**
 * Created by matt on 26.03.2018.
 */

public class InventoryDbHelper extends SQLiteOpenHelper{

    // name of the database file
    private static final String DATABASE_NAME = "Inventory.db";

    // If change database schema need to update (increment by 1) DATABASE_VERSION
    private static final int DATABASE_VERSION = 1;

    // SQLite command to create new database schema with entries
    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + InventoryEntry.TABLE_NAME + "(" +
            InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            InventoryEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, " +
            InventoryEntry.COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL, " +
            InventoryEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0, " +
            InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME + " TEXT, " +
            InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER + " TEXT);";

    // SQLite command to delete existing database if it exists
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS" + InventoryEntry.TABLE_NAME;

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
    }
}
