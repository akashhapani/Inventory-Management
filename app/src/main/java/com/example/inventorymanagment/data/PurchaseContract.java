package com.example.inventorymanagment.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class PurchaseContract {
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://com.example.inventorymanagment");
    public static final String CONTENT_AUTHORITY = "com.example.inventorymanagment";
    public static final String PATH_PURCHASE = "purchase";

    public static final class PurchaseEntry implements BaseColumns {
        public static final String COLUMN_PURCHASE_DATE = "date";
        public static final String COLUMN_PURCHASE_DESCRIPTION = "description";
        public static final String COLUMN_PURCHASE_NAME = "name";
        public static final String COLUMN_PURCHASE_PRICE = "price";
        public static final String COLUMN_PURCHASE_QUANTITY = "quantity";
        public static final String COLUMN_PURCHASE_SUPPLIER = "supplier";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.example.inventorymanagment/purchase";
        public static final String CONTENT_LIST_TYPE = "vnd.android.cursor.dir/com.example.inventorymanagment/purchase";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(PurchaseContract.BASE_CONTENT_URI, "purchase");
        public static final String RESET_TABLE = "sqlite_sequence";
        public static final String TABLE_NAME = "purchase";
        public static final String _ID = "_id";
    }

    private PurchaseContract() {
    }
}
