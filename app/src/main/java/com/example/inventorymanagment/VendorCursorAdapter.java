package com.example.inventorymanagment;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.example.inventorymanagment.data.VendorContract;

public class VendorCursorAdapter extends CursorAdapter {
    public VendorCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.vendor_list_item, viewGroup, false);
    }

    public void bindView(View view, Context context, Cursor cursor) {
        int columnIndex = cursor.getColumnIndex("name");
        int columnIndex2 = cursor.getColumnIndex(VendorContract.VendorEntry.COLUMN_VENDOR_COMPANY);
        int columnIndex3 = cursor.getColumnIndex(VendorContract.VendorEntry.COLUMN_VENDOR_CONTACT);
        String string = cursor.getString(columnIndex);
        String string2 = cursor.getString(columnIndex2);
        String string3 = cursor.getString(columnIndex3);
        ((TextView) view.findViewById(R.id.vendor_list_name)).setText(string);
        ((TextView) view.findViewById(R.id.vendor_list_company)).setText(string2);
        ((TextView) view.findViewById(R.id.vendor_list_contact)).setText(string3);
    }
}
