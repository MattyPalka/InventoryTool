package com.apps.palka.matt.inventorytool;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.apps.palka.matt.inventorytool.Data.InventoryContract.InventoryEntry;
import com.apps.palka.matt.inventorytool.Data.InventoryDbHelper;

public class EditorActivity extends AppCompatActivity {

    // Edit text field to enter Product Name
    private EditText mProductNameEditText;

    // Edit text field to enter product price
    private EditText mProductPriceEditText;

    // Edit text field to enter product quantity
    private EditText mProductQuantityEditText;

    // Edit text field to enter product supplier name
    private EditText mProductSupplierNameEditText;

    // Edit text field to enter product supplier's phone number
    private EditText mProductSupplierPhoneNumberEditText;

    @Override
    protected void onStart() {
        super.onStart();
        insertData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

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
        switch (item.getItemId()){
            case R.id.accept:
                insertData();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void insertData(){

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

        InventoryDbHelper dbHelper = new InventoryDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT_NAME, productNameString);
        values.put(InventoryEntry.COLUMN_PRODUCT_PRICE, productPriceString);
        values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, productQuantityInt);
        values.put(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME, productNameString);
        values.put(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER, productSupplierPhoneNumberInt);

        long newRowId = db.insert(InventoryEntry.TABLE_NAME, null, values);

        if (newRowId != -1){
            Toast.makeText(this, "Success! product added with ID: " + newRowId, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Something went wrong. \n Please try again", Toast.LENGTH_SHORT).show();
        }


    }
}
