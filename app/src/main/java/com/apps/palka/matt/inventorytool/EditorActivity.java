package com.apps.palka.matt.inventorytool;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.apps.palka.matt.inventorytool.Data.InventoryContract.InventoryEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    //Indicator for the listener if the any fields within the item has changed (true if any field is updated)
    private boolean mItemHasChanged = false;

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
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a pet that hasn't been created yet.)
            invalidateOptionsMenu();
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

        mProductNameEditText.setOnTouchListener(mTouchListener);
        mProductPriceEditText.setOnTouchListener(mTouchListener);
        mProductQuantityEditText.setOnTouchListener(mTouchListener);
        mProductSupplierNameEditText.setOnTouchListener(mTouchListener);
        mProductSupplierPhoneNumberEditText.setOnTouchListener(mTouchListener);
    }

    // OnTouchListener that listens for any user touches on a View, implying that they are modifying
    // the view, and we change the mPetHasChanged boolean to true.
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
        }
    };

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        // If the item hasn't changed, continue with handling back button press
        if (!mItemHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        //if this is new item hide the "delete" menu item
        if (mCurrentItem == null){
            MenuItem menuItem = menu.findItem(R.id.delete);
            menuItem.setVisible(false);
        }
        return true;
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
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
            // If the Item hasn't changed, continue with navigating up to parent activity
            // which is the {@link MainActivity}.
            if (!mItemHasChanged) {
                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                return true;
            }

            // Otherwise if there are unsaved changes, setup a dialog to warn the user.
            // Create a click listener to handle the user confirming that
            // changes should be discarded.
            DialogInterface.OnClickListener discardButtonClickListener =
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // User clicked "Discard" button, navigate to parent activity.
                            NavUtils.navigateUpFromSameTask(EditorActivity.this);
                        }
                    };

            // Show a dialog that notifies the user they have unsaved changes
            showUnsavedChangesDialog(discardButtonClickListener);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteItem();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of item in the database.
     */
    private void deleteItem() {
        if (mCurrentItem != null) {
            int deletedRow = getContentResolver().delete(mCurrentItem, null, null);
            if (deletedRow == 0) {
                Toast.makeText(this, R.string.editor_delete_item_failed, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.editor_delete_item_successful, Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    public void saveData() {

        // Grab data from given edit tex views
        // PRODUCT NAME
        String productNameString = mProductNameEditText.getText().toString().trim();

        // PRODUCT PRICE
        String productPriceString = mProductPriceEditText.getText().toString().trim();
        int productPrice;

        // PRODUCT QUANTITY
        String productQuantityString = mProductQuantityEditText.getText().toString().trim();
        int productQuantity = 0;

        // PRODUCT SUPPLIER NAME
        String productSupplierString = mProductSupplierNameEditText.getText().toString().trim();
        // PRODUCT SUPPLIER PHONE NUMBER
        String productSupplierPhoneNumberString = mProductSupplierPhoneNumberEditText.getText().toString().trim();
        int productSupplierPhoneNumber;

        // Check if all the edit fields are empty and if so return without any info
        if (mCurrentItem == null && productNameString.isEmpty() && productPriceString.isEmpty() &&
                productQuantityString.isEmpty() && productSupplierString.isEmpty() && productSupplierPhoneNumberString.isEmpty()){
            return;
        }

        // check if there is input in product price. If there isn't return and display toast to set a price
        // if there is set it's value to productPrice
        if (productPriceString.isEmpty()) {
            Toast.makeText(this, R.string.set_price, Toast.LENGTH_SHORT).show();
            return;
        } else {
            productPrice = Integer.parseInt(productPriceString);
        }

        // check if there is input in quantity and if so set it to productQuantity value
        if (!(productQuantityString.isEmpty())) {
            productQuantity = Integer.parseInt(productQuantityString);
        }

        // if supplier phone number is empty set it to 0 otherwise set it to the proper number
        if (productSupplierPhoneNumberString.isEmpty()) {
            productSupplierPhoneNumber = 0;
        } else {
            productSupplierPhoneNumber = Integer.parseInt(productSupplierPhoneNumberString);
        }

        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT_NAME, productNameString);
        values.put(InventoryEntry.COLUMN_PRODUCT_PRICE, productPrice);
        values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, productQuantity);
        values.put(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME, productSupplierString);
        values.put(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER, productSupplierPhoneNumber);

        // Check if it's the new product screen (null) or edit screen (not null)
        if (mCurrentItem == null) {
            //If new product insert new row in a database
            Uri newRowUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
            if (newRowUri != null) {
                Toast.makeText(this, R.string.product_added, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.editor_failed, Toast.LENGTH_SHORT).show();
            }
        } else {
            // if existing product update it's columns
            int updatedItems = getContentResolver().update(mCurrentItem, values, null, null);
            if (updatedItems == 0) {
                Toast.makeText(this, R.string.editor_failed, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.editor_success, Toast.LENGTH_SHORT).show();
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
