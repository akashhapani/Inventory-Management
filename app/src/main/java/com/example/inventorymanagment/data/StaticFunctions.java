package com.example.inventorymanagment.data;

import android.app.ProgressDialog;
import android.content.Context;

public class StaticFunctions {

    /* renamed from: pd */
    static ProgressDialog f1188pd;

    public static void showProgress(Context context) {
        f1188pd = new ProgressDialog(context);
        f1188pd.setMessage("loading");
        f1188pd.show();
    }

    public static void dismiss() {
        f1188pd.dismiss();
    }
}
