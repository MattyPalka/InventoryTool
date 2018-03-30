package com.apps.palka.matt.inventorytool;


import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.apps.palka.matt.inventorytool.Data.InventoryContract.InventoryEntry;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onStart() {
        super.onStart();
        queryData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        queryData();

    }


    private void queryData() {
        TextView displayDataTextView = findViewById(R.id.main_text_view);


        // Columns of the tables that we want to get
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRODUCT_PRICE,
                InventoryEntry.COLUMN_PRODUCT_QUANTITY,
                InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER
        };

        Cursor cursor = getContentResolver().query(InventoryEntry.CONTENT_URI, projection,
                null, null, null);

        try {
            displayDataTextView.setText("Inventory contains: " + cursor.getCount() + " items\n\n");
            displayDataTextView.append(InventoryEntry._ID + "\t|\t" +
                    InventoryEntry.COLUMN_PRODUCT_NAME + "\t|\t" +
                    InventoryEntry.COLUMN_PRODUCT_PRICE + "\t|\t" +
                    InventoryEntry.COLUMN_PRODUCT_QUANTITY + "\t|\t" +
                    InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME + "\t|\t" +
                    InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);

            //figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(InventoryEntry._ID);
            int productNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
            int productPriceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
            int productQuantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
            int productSupplierPhoneNumberColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);
            int productSupplierNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);

            //Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on
                int currentID = cursor.getInt(idColumnIndex);
                String currentProductName = cursor.getString(productNameColumnIndex);
                int currentProductPrice = cursor.getInt(productPriceColumnIndex);
                int currentProductQuantity = cursor.getInt(productQuantityColumnIndex);
                String currentProductSupplier = cursor.getString(productSupplierNameColumnIndex);
                int currentProductSupplierPhoneNumber = cursor.getInt(productSupplierPhoneNumberColumnIndex);

                // display the values from each column of the current row in the Text View
                displayDataTextView.append("\n" + currentID + "\t|\t" +
                        currentProductName + "\t|\t" +
                        currentProductPrice + "\t|\t" +
                        currentProductQuantity + "\t|\t" +
                        currentProductSupplier + "\t|\t" +
                        currentProductSupplierPhoneNumber);
            }


        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu options from res/menu/main.xml file.
        // This adds menu items to the bar.
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // user clicked on the menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // start the add item / edit item activity (EditorActivity)
            case R.id.add_item:
                Intent intent = new Intent(this, EditorActivity.class);
                startActivity(intent);
                return true;
                //TODO: add menu option to delete all data (with extra security screen)
        }
        return super.onOptionsItemSelected(item);
    }
}
