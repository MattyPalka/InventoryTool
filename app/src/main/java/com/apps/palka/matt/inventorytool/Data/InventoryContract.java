package com.apps.palka.matt.inventorytool.Data;

import android.provider.BaseColumns;

/**
 * Created by matt on 26.03.2018.
 */

public class InventoryContract {

    public class InventoryEntry implements BaseColumns {

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
