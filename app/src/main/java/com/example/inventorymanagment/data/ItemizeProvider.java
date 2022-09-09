package com.example.inventorymanagment.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class ItemizeProvider extends ContentProvider {
    private static final int ITEMIZE = 100;
    private static final int ITEMIZE_ID = 101;
    public static final String LOG_TAG = ItemizeProvider.class.getSimpleName();
    private static final int PURCHASE = 400;
    private static final int PURCHASE_ID = 401;
    private static final int SALES = 200;
    private static final int SALES_ID = 201;
    private static final int VENDOR = 300;
    private static final int VENDOR_ID = 301;
    private static final UriMatcher sUriMatcher = new UriMatcher(-1);
    private ItemizeDbHelper mDbHelper;

    static {
        sUriMatcher.addURI("com.example.inventorymanagment", "products", 100);
        sUriMatcher.addURI("com.example.inventorymanagment", "products/#", 101);
        sUriMatcher.addURI("com.example.inventorymanagment", "sales", 200);
        sUriMatcher.addURI("com.example.inventorymanagment", "sales/#", 201);
        sUriMatcher.addURI("com.example.inventorymanagment", "vendor", VENDOR);
        sUriMatcher.addURI("com.example.inventorymanagment", "vendor/#", VENDOR_ID);
        sUriMatcher.addURI("com.example.inventorymanagment", "purchase", PURCHASE);
        sUriMatcher.addURI("com.example.inventorymanagment", "purchase/#", PURCHASE_ID);
    }

    public boolean onCreate() {
        this.mDbHelper = new ItemizeDbHelper(getContext());
        return true;
    }

    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        Cursor cursor;
        SQLiteDatabase readableDatabase = this.mDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        if (match == 100) {
            cursor = readableDatabase.query("products", strArr, str, strArr2, (String) null, (String) null, str2);
        } else if (match == 101) {
            cursor = readableDatabase.query("products", strArr, "_id=?", new String[]{String.valueOf(ContentUris.parseId(uri))}, (String) null, (String) null, str2);
        } else if (match == 200) {
            cursor = readableDatabase.query("sales", strArr, str, strArr2, (String) null, (String) null, str2);
        } else if (match == 201) {
            cursor = readableDatabase.query("sales", strArr, "_id=?", new String[]{String.valueOf(ContentUris.parseId(uri))}, (String) null, (String) null, str2);
        } else if (match == VENDOR) {
            cursor = readableDatabase.query("vendor", strArr, str, strArr2, (String) null, (String) null, str2);
        } else if (match == VENDOR_ID) {
            cursor = readableDatabase.query("vendor", strArr, "_id=?", new String[]{String.valueOf(ContentUris.parseId(uri))}, (String) null, (String) null, str2);
        } else if (match == PURCHASE) {
            cursor = readableDatabase.query("purchase", strArr, str, strArr2, (String) null, (String) null, str2);
        } else if (match == PURCHASE_ID) {
            cursor = readableDatabase.query("purchase", strArr, "_id=?", new String[]{String.valueOf(ContentUris.parseId(uri))}, (String) null, (String) null, str2);
        } else {
            throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    public String getType(Uri uri) {
        int match = sUriMatcher.match(uri);
        if (match == 100) {
            return ItemizeContract.ItemizeEntry.CONTENT_LIST_TYPE;
        }
        if (match == 101) {
            return ItemizeContract.ItemizeEntry.CONTENT_ITEM_TYPE;
        }
        if (match == 200) {
            return SalesContract.SalesEntry.CONTENT_LIST_TYPE;
        }
        if (match == 201) {
            return SalesContract.SalesEntry.CONTENT_ITEM_TYPE;
        }
        if (match == VENDOR) {
            return VendorContract.VendorEntry.CONTENT_LIST_TYPE;
        }
        if (match == VENDOR_ID) {
            return VendorContract.VendorEntry.CONTENT_ITEM_TYPE;
        }
        if (match == PURCHASE) {
            return PurchaseContract.PurchaseEntry.CONTENT_LIST_TYPE;
        }
        if (match == PURCHASE_ID) {
            return PurchaseContract.PurchaseEntry.CONTENT_ITEM_TYPE;
        }
        throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
    }

    public Uri insert(Uri uri, ContentValues contentValues) {
        int match = sUriMatcher.match(uri);
        if (match == 100) {
            return insertProduct(uri, contentValues);
        }
        if (match == 200) {
            return insertSales(uri, contentValues);
        }
        if (match == VENDOR) {
            return insertVendor(uri, contentValues);
        }
        if (match == PURCHASE) {
            return insertPurchase(uri, contentValues);
        }
        throw new IllegalArgumentException("Insertion is not supported for " + uri);
    }

    private Uri insertVendor(Uri uri, ContentValues contentValues) {
        if (contentValues.getAsString("name") == null) {
            throw new IllegalArgumentException("Vendor requires a name");
        } else if (contentValues.getAsString(VendorContract.VendorEntry.COLUMN_VENDOR_COMPANY) == null) {
            throw new IllegalArgumentException("Vendor requires a valid company");
        } else if (contentValues.getAsString(VendorContract.VendorEntry.COLUMN_VENDOR_ADDRESS) == null) {
            throw new IllegalArgumentException("Vendor requires a valid address");
        } else if (contentValues.getAsString(VendorContract.VendorEntry.COLUMN_VENDOR_CONTACT) != null) {
            long insert = this.mDbHelper.getWritableDatabase().insert("vendor", (String) null, contentValues);
            if (insert == -1) {
                String str = LOG_TAG;
                Log.e(str, "Failed to insert row for " + uri);
                return null;
            }
            getContext().getContentResolver().notifyChange(uri, (ContentObserver) null);
            return ContentUris.withAppendedId(uri, insert);
        } else {
            throw new IllegalArgumentException("Vendor requires contact");
        }
    }

    private Uri insertSales(Uri uri, ContentValues contentValues) {
        if (contentValues.getAsString(SalesContract.SalesEntry.COLUMN_CUSTOMER_NAME) != null) {
            Integer asInteger = contentValues.getAsInteger("price");
            if (asInteger == null || asInteger.intValue() < 0) {
                throw new IllegalArgumentException("Product requires a valid price");
            }
            Integer asInteger2 = contentValues.getAsInteger("quantity");
            if (asInteger2 == null && asInteger2.intValue() < 0) {
                throw new IllegalArgumentException("Product requires valid quantity");
            } else if (contentValues.getAsString("date") == null) {
                throw new IllegalArgumentException("Product requires a valid date");
            } else if (contentValues.getAsString(SalesContract.SalesEntry.COLUMN_CUSTOMER_NAME) == null) {
                throw new IllegalArgumentException("Product requires customer name");
            } else if (contentValues.getAsString(SalesContract.SalesEntry.COLUMN_CUSTOMER_CONT) != null) {
                long insert = this.mDbHelper.getWritableDatabase().insert("sales", (String) null, contentValues);
                if (insert == -1) {
                    String str = LOG_TAG;
                    Log.e(str, "Failed to insert row for " + uri);
                    return null;
                }
                getContext().getContentResolver().notifyChange(uri, (ContentObserver) null);
                return ContentUris.withAppendedId(uri, insert);
            } else {
                throw new IllegalArgumentException("Product requires customer contact");
            }
        } else {
            throw new IllegalArgumentException("Product requires a name");
        }
    }

    private Uri insertProduct(Uri uri, ContentValues contentValues) {
        if (contentValues.getAsString("name") != null) {
            Integer asInteger = contentValues.getAsInteger("price");
            if (asInteger == null || asInteger.intValue() < 0) {
                throw new IllegalArgumentException("Product requires a valid price");
            }
            Integer asInteger2 = contentValues.getAsInteger("quantity");
            if (asInteger2 == null && asInteger2.intValue() < 0) {
                throw new IllegalArgumentException("Product requires valid quanity");
            } else if (contentValues.getAsString("description") == null) {
                throw new IllegalArgumentException("Product requires description");
            } else if (contentValues.getAsString("supplier") == null) {
                throw new IllegalArgumentException("Product requires a supplier");
            } else if (contentValues.getAsString("date") != null) {
                long insert = this.mDbHelper.getWritableDatabase().insert("products", (String) null, contentValues);
                if (insert == -1) {
                    String str = LOG_TAG;
                    Log.e(str, "Failed to insert row for " + uri);
                    return null;
                }
                getContext().getContentResolver().notifyChange(uri, (ContentObserver) null);
                return ContentUris.withAppendedId(uri, insert);
            } else {
                throw new IllegalArgumentException("Product requires date");
            }
        } else {
            throw new IllegalArgumentException("Product requires a name");
        }
    }

    private Uri insertPurchase(Uri uri, ContentValues contentValues) {
        if (contentValues.getAsString("name") != null) {
            Integer asInteger = contentValues.getAsInteger("price");
            if (asInteger == null || asInteger.intValue() < 0) {
                throw new IllegalArgumentException("Product requires a valid price");
            }
            Integer asInteger2 = contentValues.getAsInteger("quantity");
            if (asInteger2 == null && asInteger2.intValue() < 0) {
                throw new IllegalArgumentException("Product requires valid quanity");
            } else if (contentValues.getAsString("description") == null) {
                throw new IllegalArgumentException("Product requires description");
            } else if (contentValues.getAsString("supplier") == null) {
                throw new IllegalArgumentException("Product requires a supplier");
            } else if (contentValues.getAsString("date") != null) {
                long insert = this.mDbHelper.getWritableDatabase().insert("purchase", (String) null, contentValues);
                if (insert == -1) {
                    String str = LOG_TAG;
                    Log.e(str, "Failed to insert row for " + uri);
                    return null;
                }
                getContext().getContentResolver().notifyChange(uri, (ContentObserver) null);
                return ContentUris.withAppendedId(uri, insert);
            } else {
                throw new IllegalArgumentException("Product requires date");
            }
        } else {
            throw new IllegalArgumentException("Product requires a name");
        }
    }

    public int delete(Uri uri, String str, String[] strArr) {
        int i;
        SQLiteDatabase writableDatabase = this.mDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        if (match == 100) {
            i = writableDatabase.delete("products", str, strArr);
        } else if (match == 101) {
            i = writableDatabase.delete("products", "_id=?", new String[]{String.valueOf(ContentUris.parseId(uri))});
        } else if (match == 200) {
            i = writableDatabase.delete("sales", str, strArr);
        } else if (match == 201) {
            i = writableDatabase.delete("sales", "_id=?", new String[]{String.valueOf(ContentUris.parseId(uri))});
        } else if (match == VENDOR) {
            i = writableDatabase.delete("vendor", str, strArr);
        } else if (match == VENDOR_ID) {
            i = writableDatabase.delete("vendor", "_id=?", new String[]{String.valueOf(ContentUris.parseId(uri))});
        } else if (match == PURCHASE) {
            i = writableDatabase.delete("purchase", str, strArr);
        } else if (match == PURCHASE_ID) {
            i = writableDatabase.delete("purchase", "_id=?", new String[]{String.valueOf(ContentUris.parseId(uri))});
        } else {
            throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (i != 0) {
            getContext().getContentResolver().notifyChange(uri, (ContentObserver) null);
        }
        return i;
    }

    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        int match = sUriMatcher.match(uri);
        if (match == 100) {
            return updateproduct(uri, contentValues, str, strArr);
        }
        if (match == 101) {
            return updateproduct(uri, contentValues, "_id=?", new String[]{String.valueOf(ContentUris.parseId(uri))});
        } else if (match == 200) {
            return updateSales(uri, contentValues, str, strArr);
        } else {
            if (match == 201) {
                return updateSales(uri, contentValues, "_id=?", new String[]{String.valueOf(ContentUris.parseId(uri))});
            } else if (match == VENDOR) {
                return updateVendor(uri, contentValues, str, strArr);
            } else {
                if (match == VENDOR_ID) {
                    return updateVendor(uri, contentValues, "_id=?", new String[]{String.valueOf(ContentUris.parseId(uri))});
                }
                throw new IllegalArgumentException("Update is not supported for " + uri);
            }
        }
    }

    private int updateVendor(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        if (contentValues.containsKey("name") && contentValues.getAsString("name") == null) {
            throw new IllegalArgumentException("Vendor requires a name");
        } else if (contentValues.containsKey(VendorContract.VendorEntry.COLUMN_VENDOR_COMPANY) && contentValues.getAsString(VendorContract.VendorEntry.COLUMN_VENDOR_COMPANY) == null) {
            throw new IllegalArgumentException("Vendor requires a valid company");
        } else if (contentValues.containsKey(VendorContract.VendorEntry.COLUMN_VENDOR_ADDRESS) && contentValues.getAsString(VendorContract.VendorEntry.COLUMN_VENDOR_ADDRESS) == null) {
            throw new IllegalArgumentException("Address is required");
        } else if (contentValues.containsKey(VendorContract.VendorEntry.COLUMN_VENDOR_CONTACT) && contentValues.getAsString(VendorContract.VendorEntry.COLUMN_VENDOR_CONTACT) == null) {
            throw new IllegalArgumentException("Vendor contact is required");
        } else if (contentValues.size() == 0) {
            return 0;
        } else {
            int update = this.mDbHelper.getWritableDatabase().update("vendor", contentValues, str, strArr);
            if (update != 0) {
                getContext().getContentResolver().notifyChange(uri, (ContentObserver) null);
            }
            return update;
        }
    }

    private int updateSales(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        if (contentValues.containsKey(SalesContract.SalesEntry.COLUMN_CUSTOMER_NAME) && contentValues.getAsString(SalesContract.SalesEntry.COLUMN_CUSTOMER_NAME) == null) {
            throw new IllegalArgumentException("Product requires a name");
        } else if (contentValues.containsKey("price") && contentValues.getAsInteger("price") == null) {
            throw new IllegalArgumentException("Product requires price");
        } else if (contentValues.containsKey("quantity") && contentValues.getAsInteger("quantity") == null) {
            throw new IllegalArgumentException("Product requires quantity");
        } else if (contentValues.containsKey("date") && contentValues.getAsString("date") == null) {
            throw new IllegalArgumentException("Product requires a valid date");
        } else if (contentValues.containsKey(SalesContract.SalesEntry.COLUMN_CUSTOMER_NAME) && contentValues.getAsString(SalesContract.SalesEntry.COLUMN_CUSTOMER_NAME) == null) {
            throw new IllegalArgumentException("Customer name is required");
        } else if (contentValues.containsKey(SalesContract.SalesEntry.COLUMN_CUSTOMER_CONT) && contentValues.getAsString(SalesContract.SalesEntry.COLUMN_CUSTOMER_CONT) == null) {
            throw new IllegalArgumentException("Customer contact is required");
        } else if (contentValues.size() == 0) {
            return 0;
        } else {
            int update = this.mDbHelper.getWritableDatabase().update("sales", contentValues, str, strArr);
            if (update != 0) {
                getContext().getContentResolver().notifyChange(uri, (ContentObserver) null);
            }
            return update;
        }
    }

    private int updateproduct(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        if (contentValues.containsKey("name") && contentValues.getAsString("name") == null) {
            throw new IllegalArgumentException("Product requires a name");
        } else if (contentValues.containsKey("price") && contentValues.getAsInteger("price") == null) {
            throw new IllegalArgumentException("Product requires price");
        } else if (contentValues.containsKey("quantity") && contentValues.getAsInteger("quantity") == null) {
            throw new IllegalArgumentException("Product requires quantity");
        } else if (contentValues.containsKey("description") && contentValues.getAsString("description") == null) {
            throw new IllegalArgumentException("Product requires description");
        } else if (contentValues.containsKey("supplier") && contentValues.getAsString("supplier") == null) {
            throw new IllegalArgumentException("Supplier name is required");
        } else if (contentValues.containsKey("date") && contentValues.getAsString("date") == null) {
            throw new IllegalArgumentException("Date is required");
        } else if (contentValues.size() == 0) {
            return 0;
        } else {
            int update = this.mDbHelper.getWritableDatabase().update("products", contentValues, str, strArr);
            if (update != 0) {
                getContext().getContentResolver().notifyChange(uri, (ContentObserver) null);
            }
            return update;
        }
    }
}
