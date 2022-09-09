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
import com.example.inventorymanagment.adapter.ProductCursorAdapter;
import com.example.inventorymanagment.data.ItemizeContract;
import com.example.inventorymanagment.data.ItemizeDbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;

public class ProductList extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int PRODUCT_LOADER = 0;
    Cursor cursor;
    ProgressDialog dialog;
    private int[] grantResults;
    ProductCursorAdapter mCursorAdapter;
    ItemizeDbHelper mDbHelper;
//    private InterstitialAd mInterstitial;
//    private RewardedVideoAd mRewardedVideoAd;
    private int requestCode;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.product_list);
        this.mDbHelper = new ItemizeDbHelper(this);
        ((FloatingActionButton) findViewById(R.id.fab)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ProductList.this.startActivity(new Intent(ProductList.this, AddProduct.class));
            }
        });
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setEmptyView(findViewById(R.id.empty_view));
        this.mCursorAdapter = new ProductCursorAdapter(this, (Cursor) null);
        listView.setAdapter(this.mCursorAdapter);
        this.mCursorAdapter.notifyDataSetChanged();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                Intent intent = new Intent(ProductList.this, AddProduct.class);
                intent.setData(ContentUris.withAppendedId(ItemizeContract.ItemizeEntry.CONTENT_URI, j));
                ProductList.this.startActivity(intent);
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
        getMenuInflater().inflate(R.menu.searchbar, menu);
        if (Build.VERSION.SDK_INT < 11) {
            return true;
        }
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setQueryHint("Search Product...");
//        searchView.setSearchableInfo(((SearchManager) getSystemService(FirebaseAnalytics.Event.SEARCH)).getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            public boolean onQueryTextSubmit(String str) {
                ProductList productList = ProductList.this;
                productList.cursor = productList.mDbHelper.getProductListByKeyword(str);
                if (ProductList.this.cursor == null) {
                    Toast.makeText(ProductList.this, "No records found!", Toast.LENGTH_LONG).show();
                } else {
                    ProductList productList2 = ProductList.this;
                    Toast.makeText(productList2, ProductList.this.cursor.getCount() + " records found!", Toast.LENGTH_LONG).show();
                }
                ProductList.this.mCursorAdapter.swapCursor(ProductList.this.cursor);
                return false;
            }

            public boolean onQueryTextChange(String str) {
                ProductList productList = ProductList.this;
                productList.cursor = productList.mDbHelper.getProductListByKeyword(str);
                if (ProductList.this.cursor == null) {
                    return false;
                }
                ProductList.this.mCursorAdapter.swapCursor(ProductList.this.cursor);
                return false;
            }
        });
        return true;
    }

//    public void loadInterstitial() {
//        this.mInterstitial = new InterstitialAd(this);
//        this.mInterstitial.setAdUnitId("ca-app-pub-9965955769930091/1042084438");
//        this.mInterstitial.setAdListener(new ToastAdListener(this) {
//            public void onAdLoaded() {
//                super.onAdLoaded();
//            }
//        });
//        this.mInterstitial.loadAd(new AdRequest.Builder().build());
//    }
//
//    public void showInterstitial() {
//        if (this.mInterstitial.isLoaded()) {
//            this.mInterstitial.show();
//        }
//    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.delete_products_list) {
            showProductsDeleteDialog();
            return true;
        } else if (itemId != R.id.export_products_table) {
            return super.onOptionsItemSelected(menuItem);
        } else {
//            showInterstitial();
//            loadInterstitial();
            askForPermission();
            showExportDialog();
            return true;
        }
    }

    private void showProductsDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage((CharSequence) "Delete all the products from Products List?");
        builder.setPositiveButton((CharSequence) "Delete", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                ProductList.this.deleteAllProducts();
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
    public void deleteAllProducts() {
        int delete = getContentResolver().delete(ItemizeContract.ItemizeEntry.CONTENT_URI, (String) null, (String[]) null);
        Toast.makeText(this, delete + " products were deleted ", Toast.LENGTH_SHORT).show();
    }

    private void showExportDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage((CharSequence) "Export Product List to excel file? File will be in Internal/External storage");
        builder.setPositiveButton((CharSequence) "Export", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                ProductList.this.export();
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
        new SQLiteToExcel(getApplicationContext(), "itemize.db", str).exportSingleTable("products", "ProductsTable.xls", new SQLiteToExcel.ExportListener() {
            public void onError(Exception exc) {
            }

            public void onStart() {
                ProductList.this.dialog.setMessage("Exporting database");
                ProductList.this.dialog.show();
            }

            public void onCompleted(String str) {
                ProductList.this.dialog.isShowing();
                ProductList.this.dialog.dismiss();
                Toast.makeText(ProductList.this, "Successfully Exported to Downloads....", Toast.LENGTH_SHORT).show();

//                Toast.makeText(ProductList.this, "Exported", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, ItemizeContract.ItemizeEntry.CONTENT_URI, new String[]{"_id", ItemizeContract.ItemizeEntry.COLUMN_PRODUCT_PICTURE, "name", "price", "quantity"}, (String) null, (String[]) null, (String) null);
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor2) {
        this.mCursorAdapter.swapCursor(cursor2);
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        this.mCursorAdapter.swapCursor((Cursor) null);
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
