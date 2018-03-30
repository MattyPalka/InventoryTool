package com.apps.palka.matt.inventorytool;


import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
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

        //Find list view to populate
        ListView lv = findViewById(R.id.list_view);
        //setup cursor adapter
        InventoryCursorAdapter inventoryCursorAdapter = new InventoryCursorAdapter(this, cursor);
        //Attach cursor adapter to list view
        lv.setAdapter(inventoryCursorAdapter);

        View emptyView = findViewById(R.id.empty_view);
        lv.setEmptyView(emptyView);

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
