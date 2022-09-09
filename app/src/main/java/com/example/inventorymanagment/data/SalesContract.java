package com.example.inventorymanagment.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class SalesContract {
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://com.example.inventorymanagment");
    public static final String CONTENT_AUTHORITY = "com.example.inventorymanagment";
    public static final String PATH_SALES = "sales";

    public static final class SalesEntry implements BaseColumns {
        public static final String COLUMN_CUSTOMER_CONT = "customer_contact";
        public static final String COLUMN_CUSTOMER_NAME = "customer_name";
        public static final String COLUMN_PRODUCT_DATE = "date";
        public static final String COLUMN_PRODUCT_NAME = "name";
        public static final String COLUMN_PRODUCT_PRICE = "price";
        public static final String COLUMN_PRODUCT_QUANTITY = "quantity";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.example.inventorymanagment/sales";
        public static final String CONTENT_LIST_TYPE = "vnd.android.cursor.dir/com.example.inventorymanagment/sales";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(SalesContract.BASE_CONTENT_URI, "sales");
        public static final String TABLE_NAME = "sales";
        public static final String _ID = "_id";
    }

    private SalesContract() {
    }
}
