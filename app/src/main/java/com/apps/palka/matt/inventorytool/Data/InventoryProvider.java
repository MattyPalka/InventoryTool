package com.apps.palka.matt.inventorytool.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by matt on 29.03.2018.
 */

public class InventoryProvider extends ContentProvider {
    //TAG for log messages
    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();

    //code numbers to URI matcher
    private static final int INVENTORY = 100;
    private static final int INVENTORY_ID = 101;

    //initialize URI matcher with the value of no match
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Database helper object
    private InventoryDbHelper mDbHelper;

    static {
        // match the whole table
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY, INVENTORY);
        // match the specific row
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY + "/#", INVENTORY_ID);
    }

    // Initialize the provider and the database helper object
    @Override
    public boolean onCreate() {
        mDbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    /**
     * Perform a query for a given URI
     *
     * @param uri           query URI
     * @param projection    columns that we want to get from one row
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri,
                        @Nullable String[] projection,
                        @Nullable String selection,
                        @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        //Figure out if the URI matcher can match the URI to specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                cursor = database.query(InventoryContract.InventoryEntry.TABLE_NAME,
                        projection, null, null, null, null, sortOrder);
                break;
            case INVENTORY_ID:
                /**
                 * For the INVENTORY_ID code, extract the ID from the URI.
                 * For example URI such as "content://com.apps.palka.matt.inventorytool/inventory/3"
                 * the selection will be "_ID=?" and selection argument will be a string array containing
                 * the actual ID of 3 in this case
                 */
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(InventoryContract.InventoryEntry.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query illegal URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return InventoryContract.InventoryEntry.CONTENT_LIST_TYPE;
            case INVENTORY_ID:
                return InventoryContract.InventoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match " + match);
        }
    }

    /**
     * adds new entry to database
     *
     * @param uri
     * @param contentValues values to enter with a new object
     * @return
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri,
                      @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return insertInventoryItem(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insetion is not supported for " + uri);
        }
    }

    /**
     * Deletes database entry or the entire database
     *
     * @param uri
     * @param selection     selection of items to delete
     * @param selectionArgs arguments to selection for deletion
     * @return
     */
    @Override
    public int delete(@NonNull Uri uri,
                      @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        // get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        //number of rows deleted
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                rowsDeleted = database.delete(InventoryContract.InventoryEntry.TABLE_NAME,
                        null, null);
                break;
            case INVENTORY_ID:
                rowsDeleted = database.delete(InventoryContract.InventoryEntry.TABLE_NAME,
                        "_ID=?", new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    /**
     * updates database entry
     *
     * @param uri
     * @param contentValues values to update
     * @param selection
     * @param selectionArgs
     * @return
     */
    @Override
    public int update(@NonNull Uri uri,
                      @Nullable ContentValues contentValues,
                      @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case INVENTORY:
                return updateInventoryItem(uri, contentValues, selection, selectionArgs);
            case INVENTORY_ID:
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateInventoryItem(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateInventoryItem(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // Check if values contain the key for product name and if it does check if it's not empty
        if (values.containsKey(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Item requires a name");
            }
        }

        // check if values contain the key for product price and if it does check if it's not empty or negative
        if (values.containsKey(InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE)) {
            Integer price = values.getAsInteger(InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE);
            if (price == null && price < 0) {
                throw new IllegalArgumentException("Item requires price");
            }
        }

        // check if values contain the key for product quantity and if so check if it's not empty or negative
        if (values.containsKey(InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY)) {
            Integer quantity = values.getAsInteger(InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY);
            if (quantity == null || quantity < 0) {
                throw new IllegalArgumentException("Item requires quantity");
            }
        }

        //if there are no values to update don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise get writable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(InventoryContract.InventoryEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            // notify listener that there's a change in the database
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    /**
     * Insert an item to a inventory database with given content values.
     *
     * @param uri
     * @param values values to put into the item
     * @return new content URI for the specific row in the database
     */
    private Uri insertInventoryItem(Uri uri, ContentValues values) {
        //check if the name is not null
        String name = values.getAsString(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Item requires a name");
        }
        //check if the price is not null and not negative
        Integer price = values.getAsInteger(InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE);
        if (price == null && price < 0) {
            throw new IllegalArgumentException("item requires a price");
        }
        //check if the quantity is not null and not negative
        Integer quantity = values.getAsInteger(InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY);
        if (quantity == null && quantity < 0) {
            throw new IllegalArgumentException("Item requires quantity");
        }

        //get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        //insert new path with given value
        long id = database.insert(InventoryContract.InventoryEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert the row for ID: " + id);
            return null;
        }
        // notify listeners that there's change in the database
        getContext().getContentResolver().notifyChange(uri, null);
        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }
}
