package com.apps.palka.matt.inventorytool;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.apps.palka.matt.inventorytool.Data.InventoryContract;

/**
 * Created by matt on 30.03.2018.
 * InventoryCursorAdapter is an adapter for list view, that uses Cursor of inventory data as it's data
 * source. This adapter knows how to crate a list of items for each row of pet data in the cursor
 */

public class InventoryCursorAdapter extends CursorAdapter {

    /**
     * Constructs new InventoryCursorAdapter
     *
     * @param context the context
     * @param c       The cursor from which to get data
     */
    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /*flags */);
    }

    /**
     * makes a new blank list item view. No data is set or bound in this view yet
     *
     * @param context   app context
     * @param cursor    The cursor from which get data. The cursor is already moved to the correct position
     * @param viewGroup the parent to which the new group is attached to
     * @return the newly created list item view
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.item_inventory_item, viewGroup, false);
    }

    /**
     * This method binds the inventory data (in the current row pointed to by cursor) to the given item_inventory_item
     * layout. For example, the name for the current inventory item can be set on the productName TextView
     * in the item_inventory_item layout.
     *
     * @param view    Existing view, returned earlier by newView()
     * @param context app context
     * @param cursor  The cursor from which get data. The cursor is already moved to the correct position
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //Find views to populate in inflate template
        TextView productName = view.findViewById(R.id.product_name_display);
        TextView productPrice = view.findViewById(R.id.product_price_display);
        TextView productQuantity = view.findViewById(R.id.product_quantity_display);

        //Extract properties from cursor
        String name = cursor.getString(cursor.getColumnIndexOrThrow(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME));
        int price = cursor.getInt(cursor.getColumnIndexOrThrow(InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE));
        int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY));

        // populate fields with extracted properties
        productName.setText(name);
        productPrice.setText(String.valueOf(price));
        productQuantity.setText(String.valueOf(quantity));
    }
}
