package com.example.inventorymanagment.model;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.inventorymanagment.AddProduct;
import com.example.inventorymanagment.CustomAutoCompleteView;
import com.example.inventorymanagment.R;
import com.example.inventorymanagment.data.ItemizeContract;
import com.example.inventorymanagment.data.ItemizeDbHelper;
import com.example.inventorymanagment.data.MyObject;
import com.example.inventorymanagment.data.SalesContract;
import com.example.inventorymanagment.listner.CustomAutoCompleteTextChangedListener;

import java.util.List;

public class SaleProduct extends AppCompatActivity {
    public String[] item = {"Please search..."};
    /* access modifiers changed from: private */
    public boolean mProductHasChanged = false;
    ImageButton addproductbutton;
    EditText dCustomer_Contact;
    EditText dCustomer_Name;
    EditText dPriceEditText;
    EditText dQuantityEditText;
    EditText dateEditTextSale;
    ItemizeDbHelper dbHelper;
    String delNameString;
    int finalQuantity;
    public ArrayAdapter<String> myAdapter;
    public CustomAutoCompleteView nameAutoComplete;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            boolean unused = SaleProduct.this.mProductHasChanged = true;
            return false;
        }
    };
    private boolean productsMatchfound;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.sale_product);
        this.dateEditTextSale = (EditText) findViewById(R.id.dateEtext);
        this.dbHelper = new ItemizeDbHelper(this);
        this.dPriceEditText = (EditText) findViewById(R.id.del_price);
        this.dQuantityEditText = (EditText) findViewById(R.id.del_quantity);
        this.dCustomer_Name = (EditText) findViewById(R.id.del_cust_name);
        this.dCustomer_Contact = (EditText) findViewById(R.id.del_cust_cont);
        this.addproductbutton = (ImageButton) findViewById(R.id.addProduct_button);
        this.addproductbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SaleProduct.this.startActivity(new Intent(SaleProduct.this, AddProduct.class));
            }
        });
        this.dPriceEditText.setOnTouchListener(this.mTouchListener);
        this.dQuantityEditText.setOnTouchListener(this.mTouchListener);
        this.dateEditTextSale.setOnTouchListener(this.mTouchListener);
        this.dCustomer_Name.setOnTouchListener(this.mTouchListener);
        this.dCustomer_Contact.setOnTouchListener(this.mTouchListener);
        new setDate(this, this.dateEditTextSale);
        try {
            this.nameAutoComplete = (CustomAutoCompleteView) findViewById(R.id.del_name);
            this.nameAutoComplete.addTextChangedListener(new CustomAutoCompleteTextChangedListener(this));
            this.myAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, this.item);
            this.nameAutoComplete.setAdapter(this.myAdapter);
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public String[] getItemsFromDb(String str) {
        List<MyObject> read = this.dbHelper.read(str);
        String[] strArr = new String[read.size()];
        int i = 0;
        for (MyObject myObject : read) {
            strArr[i] = myObject.objectName;
            i++;
        }
        return strArr;
    }

    private void saveSalesEntry() {
        this.dbHelper.getWritableDatabase();
        this.delNameString = this.nameAutoComplete.getText().toString().trim();
        String trim = this.dPriceEditText.getText().toString().trim();
        String trim2 = this.dQuantityEditText.getText().toString().trim();
        String trim3 = this.dateEditTextSale.getText().toString().trim();
        String trim4 = this.dCustomer_Name.getText().toString().trim();
        String obj = this.dCustomer_Contact.getText().toString();
        if (TextUtils.isEmpty(this.delNameString)) {
            this.nameAutoComplete.setError(" Field Required ");
        }
        if (TextUtils.isEmpty(trim)) {
            this.dPriceEditText.setError(" Field Required ");
        }
        if (TextUtils.isEmpty(trim2)) {
            this.dQuantityEditText.setError(" Field Required ");
        }
        if (TextUtils.isEmpty(trim3)) {
            this.dateEditTextSale.setError(" Field Required ");
        }
        if (TextUtils.isEmpty(trim4)) {
            this.dCustomer_Name.setError(" Field Required ");
        }
        matchProductsAC();
        if (!TextUtils.isEmpty(this.delNameString) && !TextUtils.isEmpty(trim) && !TextUtils.isEmpty(trim2) && !TextUtils.isEmpty(trim3) && !TextUtils.isEmpty(trim4) && this.productsMatchfound) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("name", this.delNameString);
            contentValues.put("date", trim3);
            contentValues.put(SalesContract.SalesEntry.COLUMN_CUSTOMER_NAME, trim4);
            contentValues.put("price", Integer.valueOf(!TextUtils.isEmpty(trim) ? Integer.parseInt(trim) : 0));
            contentValues.put("quantity", Integer.valueOf(!TextUtils.isEmpty(trim2) ? Integer.parseInt(trim2) : 0));
            if (TextUtils.isEmpty(obj)) {
                contentValues.put(SalesContract.SalesEntry.COLUMN_CUSTOMER_CONT, " Not available ");
            } else {
                contentValues.put(SalesContract.SalesEntry.COLUMN_CUSTOMER_CONT, obj);
            }
            if (getContentResolver().insert(SalesContract.SalesEntry.CONTENT_URI, contentValues) == null) {
                Toast.makeText(this, "Insert sales entry failed", Toast.LENGTH_SHORT).show();;
            } else {
                Toast.makeText(this, "Insert sales entry successful", Toast.LENGTH_SHORT).show();;
            }
            finish();
        }
    }

    private void matchProductsAC() {
        if (this.dbHelper.getProductsList().contains(this.delNameString)) {
            this.productsMatchfound = true;
            return;
        }
        this.productsMatchfound = false;
        this.nameAutoComplete.setError("Add new product first");
        this.addproductbutton.setVisibility(View.VISIBLE);
    }

    private int quantityUpdate() {
        String trim = this.dateEditTextSale.getText().toString().trim();
        String trim2 = this.dCustomer_Name.getText().toString().trim();
        String trim3 = this.dPriceEditText.getText().toString().trim();
        String trim4 = this.dQuantityEditText.getText().toString().trim();
        String trim5 = this.nameAutoComplete.getText().toString().trim();
        if (TextUtils.isEmpty(trim5)) {
            this.nameAutoComplete.setError(" Field Required ");
        }
        if (TextUtils.isEmpty(trim3)) {
            this.dPriceEditText.setError(" Field Required ");
        }
        if (TextUtils.isEmpty(trim4)) {
            this.dQuantityEditText.setError(" Field Required ");
        }
        if (TextUtils.isEmpty(trim)) {
            this.dateEditTextSale.setError(" Field Required ");
        }
        if (TextUtils.isEmpty(trim2)) {
            this.dCustomer_Name.setError(" Field Required ");
        }
        if (!TextUtils.isEmpty(trim5) && !TextUtils.isEmpty(trim3) && !TextUtils.isEmpty(trim4) && !TextUtils.isEmpty(trim) && !TextUtils.isEmpty(trim2)) {
            int parseInt = Integer.parseInt(trim4);
            ContentResolver contentResolver = getContentResolver();
            Uri uri = ItemizeContract.ItemizeEntry.CONTENT_URI;
            Cursor query = contentResolver.query(uri, new String[]{"quantity"}, "name=?", new String[]{trim5}, (String) null);
            if (query != null && query.moveToFirst()) {
                this.finalQuantity = query.getInt(0) - parseInt;
            }
            int i = this.finalQuantity;
            if (i > 0 || i == 0) {
                saveSalesEntry();
                ContentValues contentValues = new ContentValues();
                contentValues.put("quantity", Integer.valueOf(this.finalQuantity));
                if (contentResolver.update(ItemizeContract.ItemizeEntry.CONTENT_URI, contentValues, "name=?", new String[]{trim5}) == 0) {
                    Toast.makeText(this, "Quantity update unsuccessful", Toast.LENGTH_SHORT).show();;
                } else {
                    Toast.makeText(this, "Quantity updated", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Stock is less than the required amount", Toast.LENGTH_LONG).show();
            }
        }
        return this.finalQuantity;
    }

    public void onBackPressed() {
        if (!this.mProductHasChanged) {
            super.onBackPressed();
        } else {
            showUnsavedChangesDialog(new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    SaleProduct.this.finish();
                }
            });
        }
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage((CharSequence) "Discard your changes and quit editing");
        builder.setPositiveButton((CharSequence) "Discard", onClickListener);
        builder.setNegativeButton((CharSequence) "Keep editing", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.create().show();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sale, menu);
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != R.id.action_delete_okay) {
            return super.onOptionsItemSelected(menuItem);
        }
        quantityUpdate();
        return true;
    }
}
