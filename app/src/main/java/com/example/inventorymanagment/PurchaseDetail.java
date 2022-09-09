package com.example.inventorymanagment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ajts.androidmads.library.SQLiteToExcel;
import com.example.inventorymanagment.data.ItemizeDbHelper;
import com.example.inventorymanagment.data.PurchaseContract;

import java.io.File;

public class PurchaseDetail extends AppCompatActivity {
    ItemizeDbHelper DbHelper;

    /* renamed from: PD */
    ProgressDialog f1177PD;
    ProgressDialog dialog;
    //    private AdView mAdView;
//    private InterstitialAd mInterstitial2;
    TableLayout purchaseTable;
    private int[] grantResults;
    private int requestCode;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.purchase_detail);

        this.DbHelper = new ItemizeDbHelper(this);
        this.purchaseTable = (TableLayout) findViewById(R.id.purchaseTableLayout);
        BuildTable();
        new MyAsync().execute(new Void[0]);
//        loadInterstitial();
    }

    /* access modifiers changed from: private */
    public void BuildTable() {
        TableRow tableRow = new TableRow(this);
        tableRow.setBackgroundColor(Color.parseColor("#c0c0c0"));
        tableRow.setLayoutParams(new TableLayout.LayoutParams(-1, -2));
        for (String text : new String[]{"NO", "PRODUCT", "PRICE", "QUANTITY", "DATE", "SUPPLIER", "DESCRIPTION"}) {
            TextView textView = new TextView(this);
            textView.setLayoutParams(new TableRow.LayoutParams(-2, -2));
            textView.setBackgroundResource(R.drawable.table_border);
            textView.setGravity(17);
            textView.setTextSize(18.0f);
            textView.setPadding(30, 30, 30, 30);
            textView.setText(text);
            tableRow.addView(textView);
        }
        this.purchaseTable.addView(tableRow);
        this.DbHelper.getReadableDatabase();
        Cursor purchaseTable2 = this.DbHelper.getPurchaseTable();
        if (purchaseTable2.moveToFirst()) {
            int count = purchaseTable2.getCount();
            int columnCount = purchaseTable2.getColumnCount();
            purchaseTable2.moveToFirst();
            for (int i = 0; i < count; i++) {
                TableRow tableRow2 = new TableRow(this);
                tableRow2.setLayoutParams(new TableRow.LayoutParams(-1, -2));
                for (int i2 = 0; i2 < columnCount; i2++) {
                    TextView textView2 = new TextView(this);
                    textView2.setLayoutParams(new TableRow.LayoutParams(-2, -2));
                    textView2.setBackgroundResource(R.drawable.table_border);
                    textView2.setGravity(17);
                    textView2.setTextSize(18.0f);
                    textView2.setPadding(30, 30, 30, 30);
                    textView2.setText(purchaseTable2.getString(i2));
                    tableRow2.addView(textView2);
                }
                purchaseTable2.moveToNext();
                this.purchaseTable.addView(tableRow2);
            }
        }
    }

/*
    public void loadInterstitial() {
        this.mInterstitial2 = new InterstitialAd(this);
        this.mInterstitial2.setAdUnitId("ca-app-pub-9965955769930091/9926943486");
        this.mInterstitial2.setAdListener(new ToastAdListener(this) {
            public void onAdLoaded() {
                super.onAdLoaded();
            }
        });
        this.mInterstitial2.loadAd(new AdRequest.Builder().build());
    }

    public void showInterstitial() {
        if (this.mInterstitial2.isLoaded()) {
            this.mInterstitial2.show();
        }
    }
*/

    public void onBackPressed() {
        super.onBackPressed();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_purchase_detail, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.delete_purchase_table) {
            showPurchaseDetailDeleteDialog();
            return true;
        } else if (itemId != R.id.export) {
            return super.onOptionsItemSelected(menuItem);
        } else {
            askForPermission();
            showExportDialog();
            return true;
        }
    }

    private void showPurchaseDetailDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage((CharSequence) "Delete all the entries from Purchase Detail?");
        builder.setPositiveButton((CharSequence) "Delete", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                PurchaseDetail.this.deletePurchaseTable();
            }
        });
        builder.setNegativeButton((CharSequence) "Cancel", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.create().show();
    }

    /* access modifiers changed from: private */
    public void deletePurchaseTable() {
        int delete = getContentResolver().delete(PurchaseContract.PurchaseEntry.CONTENT_URI, (String) null, (String[]) null);
        Toast.makeText(this, delete + " entries were deleted", Toast.LENGTH_LONG).show();
        finish();
    }

    private void showExportDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage((CharSequence) "Export Purchase details to excel file?The file will be present in internal/external storage");
        builder.setPositiveButton((CharSequence) "Export", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                PurchaseDetail.this.export();
            }
        });
        builder.setNegativeButton((CharSequence) "Cancel", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.create().show();
    }

    /* access modifiers changed from: private */
    public void export() {
        this.dialog = new ProgressDialog(this);
        String str = Environment.getExternalStorageDirectory().getPath() + "/" + Environment.DIRECTORY_DOWNLOADS + "/";
        File file = new File(str);
        if (!file.exists()) {
            file.mkdirs();
        }
        new SQLiteToExcel(getApplicationContext(), "itemize.db", str)
                .exportSingleTable("purchase", "PurchaseTable.xls", new SQLiteToExcel.ExportListener() {
                    public void onError(Exception exc) {

                        Log.d("ERROR_XL", "------------ERRor-----------" + exc);
                    }

                    public void onStart() {
                        PurchaseDetail.this.dialog.setMessage("Exporting database");
                        PurchaseDetail.this.dialog.show();
                    }

                    public void onCompleted(String str) {
                        PurchaseDetail.this.dialog.isShowing();
                        PurchaseDetail.this.dialog.dismiss();
                        Log.d("ERROR_XL", "------------ complete -----------" + str);

                        Toast.makeText(PurchaseDetail.this, "Successfully Exported to Downloads....", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void askForPermission() {
        if (ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") == -1) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, this.requestCode);
            onRequestPermissionsResult(this.requestCode, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, this.grantResults);
        }
    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (i == 1) {
            if (iArr.length <= 0 || iArr[0] != 0) {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                onDestroy();
                return;
            }
            Log.d("permission", "granted");
            Toast.makeText(this, " Permission Granted", Toast.LENGTH_SHORT).show();
        }
    }

    private class MyAsync extends AsyncTask<Void, Void, Void> {
        private MyAsync() {
        }

        /* access modifiers changed from: protected */
        public Void doInBackground(Void... voidArr) {
            return null;
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            super.onPreExecute();
            PurchaseDetail.this.purchaseTable.removeAllViews();
            PurchaseDetail purchaseDetail = PurchaseDetail.this;
            purchaseDetail.f1177PD = new ProgressDialog(purchaseDetail);
            PurchaseDetail.this.f1177PD.setTitle("Please Wait..");
            PurchaseDetail.this.f1177PD.setMessage("Loading…");
            PurchaseDetail.this.f1177PD.setCancelable(false);
            PurchaseDetail.this.f1177PD.show();
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Void voidR) {
            super.onPostExecute(voidR);
            PurchaseDetail.this.BuildTable();
            PurchaseDetail.this.f1177PD.dismiss();
        }
    }
}
