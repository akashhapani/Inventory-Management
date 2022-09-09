package com.example.inventorymanagment;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;

public class CustomAutoCompleteView extends AppCompatAutoCompleteTextView {
    public CustomAutoCompleteView(Context context) {
        super(context);
    }

    public CustomAutoCompleteView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public CustomAutoCompleteView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    /* access modifiers changed from: protected */
    public void performFiltering(CharSequence charSequence, int i) {
        super.performFiltering("", i);
    }

    /* access modifiers changed from: protected */
    public void replaceText(CharSequence charSequence) {
        super.replaceText(charSequence);
    }
}
