package com.example.inventorymanagment;

import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ajts.androidmads.library.SQLiteToExcel;
import com.example.inventorymanagment.data.ItemizeDbHelper;
import com.example.inventorymanagment.data.VendorContract;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;

public class VendorList extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int VENDOR_LOADER = 0;
    Cursor cursor;
    ProgressDialog dialog;
    ItemizeDbHelper mDbHelper;
    VendorCursorAdapter mVendorAdapter;
    private int[] grantResults;
    private int requestCode;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.vendor_list);
        this.mDbHelper = new ItemizeDbHelper(this);
        ((FloatingActionButton) findViewById(R.id.vendor_fab)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                VendorList.this.startActivity(new Intent(VendorList.this, AddVendor.class));
            }
        });
        ListView listView = (ListView) findViewById(R.id.vendor_list);
        listView.setEmptyView(findViewById(R.id.empty_vendor_list));
        this.mVendorAdapter = new VendorCursorAdapter(this, (Cursor) null);
        listView.setAdapter(this.mVendorAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                Intent intent = new Intent(VendorList.this, AddVendor.class);
                intent.setData(ContentUris.withAppendedId(VendorContract.VendorEntry.CONTENT_URI, j));
                VendorList.this.startActivity(intent);
            }
        });
        getLoaderManager().initLoader(0, (Bundle) null, this);
    }

    public void onBackPressed() {
        super.onBackPressed();
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.searchbar_vendor, menu);
        if (Build.VERSION.SDK_INT < 11) {
            return true;
        }
        SearchView searchView = (SearchView) menu.findItem(R.id.search_vendor).getActionView();
        searchView.setQueryHint("Search Vendor");
//        searchView.setSearchableInfo(((SearchManager) getSystemService(FirebaseAnalytics.Event.SEARCH)).getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            public boolean onQueryTextSubmit(String str) {
                VendorList vendorList = VendorList.this;
                vendorList.cursor = vendorList.mDbHelper.getVendorListByKeyword(str);
                if (VendorList.this.cursor == null) {
                    Toast.makeText(VendorList.this, "No records found!", Toast.LENGTH_LONG).show();
                } else {
                    VendorList vendorList2 = VendorList.this;
                    Toast.makeText(vendorList2, VendorList.this.cursor.getCount() + " records found!", Toast.LENGTH_LONG).show();
                }
                VendorList.this.mVendorAdapter.swapCursor(VendorList.this.cursor);
                return false;
            }

            public boolean onQueryTextChange(String str) {
                VendorList vendorList = VendorList.this;
                vendorList.cursor = vendorList.mDbHelper.getVendorListByKeyword(str);
                if (VendorList.this.cursor == null) {
                    return false;
                }
                VendorList.this.mVendorAdapter.swapCursor(VendorList.this.cursor);
                return false;
            }
        });
        return true;
    }



    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.delete_vendors_list) {
//            showInterstitial();
//            loadInterstitial();
            showVendorsDeleteDialog();
            return true;
        } else if (itemId != R.id.export_vendors_table) {
            return super.onOptionsItemSelected(menuItem);
        } else {
//            showInterstitial();
//            loadInterstitial();
            askForPermission();
            showExportDialog();
            return true;
        }
    }

    private void showVendorsDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage((CharSequence) "Delete all the Vendors from Vendors List?");
        builder.setPositiveButton((CharSequence) "Delete", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                VendorList.this.deleteAllVendors();
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
    public void deleteAllVendors() {
        int delete = getContentResolver().delete(VendorContract.VendorEntry.CONTENT_URI, (String) null, (String[]) null);
        Toast.makeText(this, delete + " products were deleted ", Toast.LENGTH_SHORT).show();
    }

    private void showExportDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage((CharSequence) "Export Vendors List to excel file? File will be in Internal/External storage");
        builder.setPositiveButton((CharSequence) "Export", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                VendorList.this.export();
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

//        String str = Environment.getExternalStorageDirectory().getPath() + "";
        File file = new File(str);
        if (!file.exists()) {
            file.mkdirs();
        }
        new SQLiteToExcel(getApplicationContext(), "itemize.db", str).exportSingleTable("vendor", "VendorsTable.xls", new SQLiteToExcel.ExportListener() {
            public void onError(Exception exc) {
            }

            public void onStart() {
                VendorList.this.dialog.setMessage("Exporting database");
                VendorList.this.dialog.show();
            }

            public void onCompleted(String str) {
                VendorList.this.dialog.isShowing();
                VendorList.this.dialog.dismiss();
                Toast.makeText(VendorList.this, "Successfully Exported to Downloads....", Toast.LENGTH_SHORT).show();

//                Toast.makeText(VendorList.this, "Exported", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
    }

    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, VendorContract.VendorEntry.CONTENT_URI, new String[]{"_id", "name", VendorContract.VendorEntry.COLUMN_VENDOR_COMPANY, VendorContract.VendorEntry.COLUMN_VENDOR_ADDRESS, VendorContract.VendorEntry.COLUMN_VENDOR_CONTACT}, (String) null, (String[]) null, (String) null);
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor2) {
        this.mVendorAdapter.swapCursor(cursor2);
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        this.mVendorAdapter.swapCursor((Cursor) null);
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
}
