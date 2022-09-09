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
import com.example.inventorymanagment.data.SalesContract;

import java.io.File;

public class SalesDetail extends AppCompatActivity {
    ItemizeDbHelper DbHelper;

    /* renamed from: PD */
    ProgressDialog f1187PD;
    TableLayout SalesTable;
    ProgressDialog dialog;
    private int[] grantResults;
    private int requestCode;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.sales_detail);
        this.DbHelper = new ItemizeDbHelper(this);
        this.SalesTable = (TableLayout) findViewById(R.id.salesTableLayout);
        BuildTable();
        new MyAsync().execute(new Void[0]);
//        loadInterstitial();
    }

    /* access modifiers changed from: private */
    public void BuildTable() {
        TableRow tableRow = new TableRow(this);
        tableRow.setBackgroundColor(Color.parseColor("#c0c0c0"));
        tableRow.setLayoutParams(new TableLayout.LayoutParams(-1, -2));
        for (String text : new String[]{"NO", "PRODUCT", "PRICE", "QUANTITY", "DATE", "CUSTOMER", "CUSTOMER CONTACT"}) {
            TextView textView = new TextView(this);
            textView.setLayoutParams(new TableRow.LayoutParams(-2, -2));
            textView.setBackgroundResource(R.drawable.table_border);
            textView.setGravity(17);
            textView.setTextSize(18.0f);
            textView.setPadding(30, 30, 30, 30);
            textView.setText(text);
            tableRow.addView(textView);
        }
        this.SalesTable.addView(tableRow);
        this.DbHelper.getReadableDatabase();
        Cursor salesTable = this.DbHelper.getSalesTable();
        if (salesTable == null) {
            Toast.makeText(this, "Nothing has been sold yet", Toast.LENGTH_SHORT).show();
        } else if (salesTable.moveToFirst()) {
            int count = salesTable.getCount();
            int columnCount = salesTable.getColumnCount();
            salesTable.moveToFirst();
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
                    textView2.setText(salesTable.getString(i2));
                    tableRow2.addView(textView2);
                }
                salesTable.moveToNext();
                this.SalesTable.addView(tableRow2);
            }
        }
    }

/*
    public void loadInterstitial() {
        this.mInterstitial3 = new InterstitialAd(this);
        this.mInterstitial3.setAdUnitId("ca-app-pub-9965955769930091/9543800105");
        this.mInterstitial3.setAdListener(new ToastAdListener(this) {
            public void onAdLoaded() {
                super.onAdLoaded();
            }
        });
        this.mInterstitial3.loadAd(new AdRequest.Builder().build());
    }

    public void showInterstitial() {
        if (this.mInterstitial3.isLoaded()) {
            this.mInterstitial3.show();
        }
    }
*/

    public void onBackPressed() {
        super.onBackPressed();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sales_detail, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.delete_sales_table) {
            showSalesDetailDeleteDialog();
            return true;
        } else if (itemId != R.id.export_sales_table) {
            return super.onOptionsItemSelected(menuItem);
        } else {
            askForPermission();
            showExportDialog();
            return true;
        }
    }

    private void showSalesDetailDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage((CharSequence) "Delete all the entries from Sales Detail?");
        builder.setPositiveButton((CharSequence) "Delete", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                SalesDetail.this.deleteSalesTable();
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
    public void deleteSalesTable() {
        int delete = getContentResolver().delete(SalesContract.SalesEntry.CONTENT_URI, (String) null, (String[]) null);
        Toast.makeText(this, delete + " entries were deleted", Toast.LENGTH_LONG).show();
        finish();
    }

    private void showExportDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage((CharSequence) "Export Sales details to excel file?The file will be present in internal/external storage");
        builder.setPositiveButton((CharSequence) "Export", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                SalesDetail.this.export_sales_table();
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
    public void export_sales_table() {
        this.dialog = new ProgressDialog(this);

        String str = Environment.getExternalStorageDirectory().getPath() + "/" + Environment.DIRECTORY_DOWNLOADS + "/";

//        String str = Environment.getExternalStorageDirectory().getPath() + "";
        File file = new File(str);
        if (!file.exists()) {
            file.mkdirs();
        }
        new SQLiteToExcel(getApplicationContext(), "itemize.db", str).exportSingleTable("sales", "SalesTable.xls", new SQLiteToExcel.ExportListener() {
            public void onError(Exception exc) {
            }

            public void onStart() {
                SalesDetail.this.dialog.setMessage("Exporting database");
                SalesDetail.this.dialog.show();
            }

            public void onCompleted(String str) {
                SalesDetail.this.dialog.isShowing();
                SalesDetail.this.dialog.dismiss();
                Toast.makeText(SalesDetail.this, "Successfully Exported to Downloads....", Toast.LENGTH_SHORT).show();

//                Toast.makeText(SalesDetail.this, "Exported", Toast.LENGTH_SHORT).show();
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
            SalesDetail.this.SalesTable.removeAllViews();
            SalesDetail salesDetail = SalesDetail.this;
            salesDetail.f1187PD = new ProgressDialog(salesDetail);
            SalesDetail.this.f1187PD.setTitle("Please Wait..");
            SalesDetail.this.f1187PD.setMessage("Loading…");
            SalesDetail.this.f1187PD.setCancelable(false);
            SalesDetail.this.f1187PD.show();
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Void voidR) {
            super.onPostExecute(voidR);
            SalesDetail.this.BuildTable();
            SalesDetail.this.f1187PD.dismiss();
        }
    }
}
