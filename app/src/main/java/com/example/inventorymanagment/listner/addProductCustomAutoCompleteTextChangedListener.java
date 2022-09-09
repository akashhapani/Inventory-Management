package com.example.inventorymanagment.listner;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.example.inventorymanagment.AddProduct;

public class addProductCustomAutoCompleteTextChangedListener implements TextWatcher {
    public static final String TAG = "addProductAutoComp.java";
    Context context;

    public void afterTextChanged(Editable editable) {
    }

    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    public addProductCustomAutoCompleteTextChangedListener(Context context2) {
        this.context = context2;
    }

    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        Log.e(TAG, "User input: " + charSequence);
        AddProduct addProduct = (AddProduct) this.context;
        addProduct.item = addProduct.getSupplierFromProductDb(charSequence.toString());
        addProduct.myAdapter.notifyDataSetChanged();
        addProduct.myAdapter = new ArrayAdapter<>(addProduct, android.R.layout.simple_dropdown_item_1line, addProduct.item);
        addProduct.supplierAutoComplete.setAdapter(addProduct.myAdapter);
    }
}
