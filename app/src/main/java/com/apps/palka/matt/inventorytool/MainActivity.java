package com.apps.palka.matt.inventorytool;


import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.apps.palka.matt.inventorytool.Data.InventoryContract.InventoryEntry;


public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int INVENTORY_LOADER = 0;
    //This is the adapter used to display inventory list of items
    InventoryCursorAdapter inventoryCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Find list view to populate
        ListView lv = findViewById(R.id.list_view);
        //setup cursor adapter
        inventoryCursorAdapter = new InventoryCursorAdapter(this, null);
        //Attach cursor adapter to list view
        lv.setAdapter(inventoryCursorAdapter);

        //setup the view for an empty list
        View emptyView = findViewById(R.id.empty_view);
        lv.setEmptyView(emptyView);



        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {
                //When pressed on list item go to detailed/ edit screen of that item
                Intent intent = new Intent(getApplicationContext(), EditorActivity.class);
                // make the uri with the current item's id
                Uri currentItem = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);
                intent.setData(currentItem);
                startActivity(intent);
            }
        });

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(INVENTORY_LOADER, null, this);
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
            case R.id.delete_all:
                showDeleteConfirmationDialog();
                return true;
            case R.id.settings:
                //TODO: create settings where user can choose sortOrder via column (ASC + DESC)
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    // Called when a new Loader needs to be created
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        // Columns of the tables that we want to get in one row
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRODUCT_PRICE,
                InventoryEntry.COLUMN_PRODUCT_QUANTITY,
        };

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(this, InventoryEntry.CONTENT_URI, projection, null,
                null, null);
    }

    // Called when a previously created loader has finished loading
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Swap the new cursor in. (The framework will take care of closing the
        // old cursor once we return.)
        inventoryCursorAdapter.swapCursor(cursor);
    }

    // Called when a previously created loader is reset, making the data unavailable
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed. We need to make sure we are no
        // longer using it.
        inventoryCursorAdapter.swapCursor(null);
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteAllItems();
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
    private void deleteAllItems() {
        int deletedRow = getContentResolver().delete(InventoryEntry.CONTENT_URI, null, null);
        if (deletedRow == 0) {
            Toast.makeText(this, R.string.delete_all_items_failed, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.delete_all_item_successful, Toast.LENGTH_SHORT).show();
        }


    }
}
