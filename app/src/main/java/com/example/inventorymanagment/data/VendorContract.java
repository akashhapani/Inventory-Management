package com.example.inventorymanagment.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class VendorContract {
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://com.example.inventorymanagment");
    public static final String CONTENT_AUTHORITY = "com.example.inventorymanagment";
    public static final String PATH_VENDOR = "vendor";

    public static final class VendorEntry implements BaseColumns {
        public static final String COLUMN_VENDOR_ADDRESS = "address";
        public static final String COLUMN_VENDOR_COMPANY = "company";
        public static final String COLUMN_VENDOR_CONTACT = "contact";
        public static final String COLUMN_VENDOR_NAME = "name";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.example.inventorymanagment/vendor";
        public static final String CONTENT_LIST_TYPE = "vnd.android.cursor.dir/com.example.inventorymanagment/vendor";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(VendorContract.BASE_CONTENT_URI, "vendor");
        public static final String TABLE_NAME = "vendor";
        public static final String _ID = "_id";
    }

    private VendorContract() {
    }
}
