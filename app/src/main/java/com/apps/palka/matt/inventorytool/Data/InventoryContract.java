package com.apps.palka.matt.inventorytool.Data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by matt on 26.03.2018.
 */

public class InventoryContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private InventoryContract(){}

    public static final String CONTENT_AUTHORITY = "com.apps.palka.matt.inventorytool";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //path to specific table
    public static final String PATH_INVENTORY = "inventory";

    public static class InventoryEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_INVENTORY);

        //MIME TYPE of the CONTENT_URI for inventory list
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;
        //MIME TYPE of the CONTENT_URI for inventory item
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        // Name of the table for the inventory items
        public static final String TABLE_NAME = "inventory";

        //Column names in the inventory items table
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME = "ProductName";
        public static final String COLUMN_PRODUCT_PRICE = "Price";
        public static final String COLUMN_PRODUCT_QUANTITY = "Quantity";
        public static final String COLUMN_PRODUCT_SUPPLIER_NAME = "SupplierName";
        public static final String COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER = "SupplierPhoneNumber";



    }
}
