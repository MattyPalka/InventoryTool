package com.apps.palka.matt.inventorytool;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.apps.palka.matt.inventorytool.Data.InventoryContract.InventoryEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_INVENTORY_ITEM_LOADER = 1;
    // Edit text field to enter Product Name
    private EditText mProductNameEditText;

    // Edit text field to enter product price
    private EditText mProductQuantityEditText;

    // Edit text field to enter product quantity
    private EditText mProductPriceEditText;

    // Edit text field to enter product supplier name
    private EditText mProductSupplierNameEditText;

    // Edit text field to enter product supplier's phone number
    private EditText mProductSupplierPhoneNumberEditText;

    private Uri mCurrentItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        //get the URI of the current inventory item
        mCurrentItem = getIntent().getData();

        if (mCurrentItem == null) {
            setTitle(R.string.editor_activity_label_add_new);
        } else {
            setTitle(R.string.editor_activity_label_edit);
            getLoaderManager().initLoader(EXISTING_INVENTORY_ITEM_LOADER, null, this);
        }

        // find all the relevant views that will be used to recieve input from the user
        mProductNameEditText = findViewById(R.id.product_name_input);
        mProductPriceEditText = findViewById(R.id.product_price_input);
        mProductQuantityEditText = findViewById(R.id.product_quantity_input);
        mProductSupplierNameEditText = findViewById(R.id.supplier_name_input);
        mProductSupplierPhoneNumberEditText = findViewById(R.id.supplier_phone_number_input);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu options from res/menu/edit.xml file.
        // This adds menu items to the bar.
        getMenuInflater().inflate(R.menu.edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.accept:
                saveData();
                finish();
                return true;
            case R.id.delete:
                //TODO: ADD DELETE DATABASE ITEM
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveData() {

        // Grab data from given edit tex views
        // PRODUCT NAME
        String productNameString = mProductNameEditText.getText().toString().trim();
        // PRODUCT PRICE
        int productPriceString = Integer.parseInt(mProductPriceEditText.getText().toString().trim());
        // PRODUCT QUANTITY
        int productQuantityInt = Integer.parseInt(mProductQuantityEditText.getText().toString().trim());
        // PRODUCT SUPPLIER NAME
        String productSupplierString = mProductSupplierNameEditText.getText().toString().trim();
        // PRODUCT SUPPLIER PHONE NUMBER
        int productSupplierPhoneNumberInt = Integer.parseInt(mProductSupplierPhoneNumberEditText.getText().toString().trim());


        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT_NAME, productNameString);
        values.put(InventoryEntry.COLUMN_PRODUCT_PRICE, productPriceString);
        values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, productQuantityInt);
        values.put(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME, productSupplierString);
        values.put(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER, productSupplierPhoneNumberInt);

        // Check if it's the new product screen (null) or edit screen (not null)
        if (mCurrentItem == null) {
            //If new product insert new row in a database
            Uri newRowUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
            if (newRowUri != null) {
                Toast.makeText(this, "New product added", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.editor_failed, Toast.LENGTH_SHORT).show();
            }
        } else {
            // if existing product update it's columns
            int updatedItems = getContentResolver().update(mCurrentItem, values, null, null);
            if (updatedItems == 0) {
                Toast.makeText(this, R.string.editor_failed, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Product updated", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Columns of the tables that we want to get in one row
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRODUCT_PRICE,
                InventoryEntry.COLUMN_PRODUCT_QUANTITY,
                InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER
        };

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(this, mCurrentItem, projection, null,
                null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            //Extract properties from cursor
            String name = cursor.getString(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_PRODUCT_NAME));
            int price = cursor.getInt(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_PRODUCT_PRICE));
            int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_PRODUCT_QUANTITY));
            String supplierName = cursor.getString(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME));
            int supplierPhoneNumber = cursor.getInt(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER));

            // Update the views on the screen with the values from the database
            mProductNameEditText.setText(name);
            mProductPriceEditText.setText(String.valueOf(price));
            mProductQuantityEditText.setText(String.valueOf(quantity));
            mProductSupplierNameEditText.setText(supplierName);
            mProductSupplierPhoneNumberEditText.setText(String.valueOf(supplierPhoneNumber));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields only leaving hint text.
        mProductSupplierNameEditText.setText(R.string.product_name);
        mProductPriceEditText.setText("");
        mProductQuantityEditText.setText("");
        mProductSupplierNameEditText.setText(R.string.supplier_name);
        mProductSupplierPhoneNumberEditText.setText(R.string.supplier_phone_number);
    }
}
