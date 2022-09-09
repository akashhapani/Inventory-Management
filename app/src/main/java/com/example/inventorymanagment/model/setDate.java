package com.example.inventorymanagment.model;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.inventorymanagment.R;

import java.util.Calendar;
import java.util.TimeZone;

public class setDate implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
    private int _birthYear;
    private Context _context;
    private int _day;
    EditText _editText;
    private int _month;

    public setDate(Context context, EditText editText) {
        this._editText = (EditText) ((Activity) context).findViewById(R.id.dateEtext);
        this._editText.setOnClickListener(this);
        this._context = context;
    }

    public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {
        this._birthYear = i;
        this._month = i2;
        this._day = i3;
        updateDisplay();
    }

    public void onClick(View view) {
        Calendar instance = Calendar.getInstance(TimeZone.getDefault());
        new DatePickerDialog(this._context, this, instance.get(1), instance.get(2), instance.get(5)).show();
    }

    private void updateDisplay() {
        EditText editText = this._editText;
        StringBuilder sb = new StringBuilder();
        sb.append(this._day);
        sb.append("/");
        sb.append(this._month + 1);
        sb.append("/");
        sb.append(this._birthYear);
        sb.append(" ");
        editText.setText(sb);
    }
}
