package com.example.inventorymanagment.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.inventorymanagment.R;
import com.example.inventorymanagment.data.ItemizeContract;

public class ProductCursorAdapter extends CursorAdapter {
    public ProductCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    public void bindView(View view, Context context, Cursor cursor) {
        int columnIndex = cursor.getColumnIndex(ItemizeContract.ItemizeEntry.COLUMN_PRODUCT_PICTURE);
        int columnIndex2 = cursor.getColumnIndex("name");
        int columnIndex3 = cursor.getColumnIndex("price");
        int columnIndex4 = cursor.getColumnIndex("quantity");
        byte[] blob = cursor.getBlob(columnIndex);
        String string = cursor.getString(columnIndex2);
        String string2 = cursor.getString(columnIndex3);
        String string3 = cursor.getString(columnIndex4);
        ((ImageView) view.findViewById(R.id.product_picture)).setImageBitmap(getPhoto(blob));
        ((TextView) view.findViewById(R.id.Name)).setText(string);
        ((TextView) view.findViewById(R.id.price)).setText(string2);
        ((TextView) view.findViewById(R.id.quantity)).setText(string3);
    }

    public static Bitmap getPhoto(byte[] bArr) {
        return BitmapFactory.decodeByteArray(bArr, 0, bArr.length);
    }
}
