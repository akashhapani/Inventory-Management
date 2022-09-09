package com.example.inventorymanagment.listner;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.example.inventorymanagment.model.SaleProduct;

public class CustomAutoCompleteTextChangedListener implements TextWatcher {
    public static final String TAG = "CACTCL.java";
    Context context;

    public void afterTextChanged(Editable editable) {
    }

    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    public CustomAutoCompleteTextChangedListener(Context context2) {
        this.context = context2;
    }

    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        Log.e(TAG, "User input: " + charSequence);
        SaleProduct saleProduct = (SaleProduct) this.context;
        saleProduct.item = saleProduct.getItemsFromDb(charSequence.toString());
        saleProduct.myAdapter.notifyDataSetChanged();
        saleProduct.myAdapter = new ArrayAdapter<>(saleProduct, android.R.layout.simple_dropdown_item_1line, saleProduct.item);
        saleProduct.nameAutoComplete.setAdapter(saleProduct.myAdapter);
    }
}
