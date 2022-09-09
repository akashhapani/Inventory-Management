package com.example.inventorymanagment.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class ItemizeContract {
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://com.example.inventorymanagment");
    public static final String CONTENT_AUTHORITY = "com.example.inventorymanagment";
    public static final String PATH_ITEMIZE = "products";

    public static final class ItemizeEntry implements BaseColumns {
        public static final String COLUMN_PRODUCT_DATE = "date";
        public static final String COLUMN_PRODUCT_DESCRIPTION = "description";
        public static final String COLUMN_PRODUCT_NAME = "name";
        public static final String COLUMN_PRODUCT_PICTURE = "picture";
        public static final String COLUMN_PRODUCT_PRICE = "price";
        public static final String COLUMN_PRODUCT_QUANTITY = "quantity";
        public static final String COLUMN_PRODUCT_SUPPLIER = "supplier";
        public static final String CONTENT_IMAGE_TYPE = "vnd.android.cursor.item/*/com.example.inventorymanagment/products";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.example.inventorymanagment/products";
        public static final String CONTENT_LIST_TYPE = "vnd.android.cursor.dir/com.example.inventorymanagment/products";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(ItemizeContract.BASE_CONTENT_URI, "products");
        public static final String TABLE_NAME = "products";
        public static final String _ID = "_id";
    }

    private ItemizeContract() {
    }
}
