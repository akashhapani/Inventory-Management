package com.example.inventorymanagment.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class ItemizeDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "itemize.db";
    private static final int DATABASE_VERSION = 5;

    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
    }

    public ItemizeDbHelper(Context context) {
        super(context, DATABASE_NAME, (SQLiteDatabase.CursorFactory) null, 5);
    }

    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("CREATE TABLE products(_id INTEGER PRIMARY KEY AUTOINCREMENT, picture BLOB, name TEXT NOT NULL, price INTEGER NOT NULL DEFAULT 0, quantity INTEGER NOT NULL DEFAULT 0, date TEXT NOT NULL, supplier TEXT, description TEXT);");
        sQLiteDatabase.execSQL("CREATE TABLE purchase(_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, price INTEGER NOT NULL DEFAULT 0, quantity INTEGER NOT NULL DEFAULT 0, date TEXT NOT NULL, supplier TEXT, description TEXT);");
        sQLiteDatabase.execSQL("CREATE TABLE sales(_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, price INTEGER NOT NULL DEFAULT 0, quantity INTEGER NOT NULL DEFAULT 0, date TEXT NOT NULL, customer_name TEXT, customer_contact TEXT);");
        sQLiteDatabase.execSQL("CREATE TABLE vendor(_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, company TEXT, address TEXT, contact INTEGER);");
    }

    public Cursor getVendorListByKeyword(String str) {
        Cursor rawQuery = getReadableDatabase().rawQuery("SELECT rowid as _id,name,company,address,contact FROM vendor WHERE name LIKE '%" + str + "%'", (String[]) null);
        if (rawQuery == null) {
            return null;
        }
        if (rawQuery.moveToFirst()) {
            return rawQuery;
        }
        rawQuery.close();
        return null;
    }

    @SuppressLint("Range")
    public List<MyObject> readSupplier(String str) {
        ArrayList arrayList = new ArrayList();
        SQLiteDatabase writableDatabase = getWritableDatabase();
        Cursor rawQuery = writableDatabase.rawQuery(((("" + "SELECT * FROM vendor") + " WHERE name LIKE '%" + str + "%'") + " ORDER BY _id DESC") + " LIMIT 0,5", (String[]) null);
        if (rawQuery.moveToFirst()) {
            do {
                arrayList.add(new MyObject(rawQuery.getString(rawQuery.getColumnIndex("name"))));
            } while (rawQuery.moveToNext());
        }
        rawQuery.close();
        writableDatabase.close();
        return arrayList;
    }

    @SuppressLint("Range")
    public List<MyObject> read(String str) {
        ArrayList arrayList = new ArrayList();
        SQLiteDatabase writableDatabase = getWritableDatabase();
        Cursor rawQuery = writableDatabase.rawQuery(((("" + "SELECT * FROM products") + " WHERE name LIKE '%" + str + "%'") + " ORDER BY _id DESC") + " LIMIT 0,5", (String[]) null);
        if (rawQuery.moveToFirst()) {
            do {
                arrayList.add(new MyObject(rawQuery.getString(rawQuery.getColumnIndex("name"))));
            } while (rawQuery.moveToNext());
        }
        rawQuery.close();
        writableDatabase.close();
        return arrayList;
    }

    @SuppressLint("Range")
    public List<String> getProductsList() {
        ArrayList arrayList = new ArrayList();
        SQLiteDatabase writableDatabase = getWritableDatabase();
        Cursor rawQuery = writableDatabase.rawQuery(" SELECT name FROM products", (String[]) null);
        if (rawQuery.moveToFirst()) {
            do {
                arrayList.add(new String(rawQuery.getString(rawQuery.getColumnIndex("name"))));
            } while (rawQuery.moveToNext());
        }
        rawQuery.close();
        writableDatabase.close();
        return arrayList;
    }

    @SuppressLint("Range")
    public List<String> getSuppliersList() {
        ArrayList arrayList = new ArrayList();
        SQLiteDatabase writableDatabase = getWritableDatabase();
        Cursor rawQuery = writableDatabase.rawQuery(" SELECT name FROM vendor", (String[]) null);
        if (rawQuery.moveToFirst()) {
            do {
                arrayList.add(new String(rawQuery.getString(rawQuery.getColumnIndex("name"))));
            } while (rawQuery.moveToNext());
        }
        rawQuery.close();
        writableDatabase.close();
        return arrayList;
    }

    public Cursor getProductListByKeyword(String str) {
        Cursor rawQuery = getReadableDatabase().rawQuery("SELECT  rowid as _id,picture,name,price,quantity,date,supplier,description FROM products WHERE name  LIKE  '%" + str + "%' ", (String[]) null);
        if (rawQuery == null) {
            return null;
        }
        if (rawQuery.moveToFirst()) {
            return rawQuery;
        }
        rawQuery.close();
        return null;
    }

    public void updateEntry(int i, String str) {
        getReadableDatabase().rawQuery(" UPDATE products SET quantity = quantity - " + i + " WHERE " + "name" + " = " + str, (String[]) null);
    }

    public Cursor selectColumnNames() {
        Cursor rawQuery = getReadableDatabase().rawQuery(" SELECT name FROM products", (String[]) null);
        if (rawQuery == null) {
            return null;
        }
        if (rawQuery.moveToFirst()) {
            return rawQuery;
        }
        rawQuery.close();
        return null;
    }

    public Cursor selectQuantity() {
        Cursor rawQuery = getReadableDatabase().rawQuery(" SELECT quantity FROM products", (String[]) null);
        if (rawQuery == null) {
            return null;
        }
        if (rawQuery.moveToFirst()) {
            return rawQuery;
        }
        rawQuery.close();
        return null;
    }

    public Cursor getPurchaseTable() {
        Cursor rawQuery = getReadableDatabase().rawQuery(" SELECT * FROM purchase", (String[]) null);
        if (rawQuery == null) {
            return null;
        }
        if (rawQuery.moveToFirst()) {
            return rawQuery;
        }
        rawQuery.close();
        return null;
    }

    public Cursor getSalesTable() {
        Cursor rawQuery = getReadableDatabase().rawQuery(" SELECT * FROM sales", (String[]) null);
        if (rawQuery == null) {
            return null;
        }
        if (rawQuery.moveToFirst()) {
            return rawQuery;
        }
        rawQuery.close();
        return null;
    }

    public Cursor productsTableExists() {
        Cursor rawQuery = getReadableDatabase().rawQuery(" SELECT * FROM products", (String[]) null);
        if (rawQuery == null) {
            return null;
        }
        if (rawQuery.moveToFirst()) {
            return rawQuery;
        }
        rawQuery.close();
        return null;
    }

    public void resetSerialNo() {
        getReadableDatabase().rawQuery(" DELETE FROM sqlite_sequence WHERE name =purchase", (String[]) null);
    }
}
