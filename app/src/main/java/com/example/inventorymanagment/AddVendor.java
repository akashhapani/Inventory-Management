package com.example.inventorymanagment;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.example.inventorymanagment.data.ItemizeDbHelper;
import com.example.inventorymanagment.data.VendorContract;

public class AddVendor extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int EXISTING_VENDOR_LOADER = 0;
    /* access modifiers changed from: private */
    public boolean mProductHasChanged = false;
    ItemizeDbHelper DbHelper;
    Button callVendor;
    private Uri mCurrentVendorUri;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            boolean unused = AddVendor.this.mProductHasChanged = true;
            return false;
        }
    };
    private EditText mVendorCompany;
    private EditText mVendorContact;
    private EditText mVendorLocation;
    private EditText mVendorName;

    public void onLoaderReset(Loader<Cursor> loader) {
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.add_vendor);
        this.DbHelper = new ItemizeDbHelper(this);

        this.callVendor = (Button) findViewById(R.id.call_vendor);
        this.callVendor.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AddVendor.this.call_vendor();
            }
        });
        this.mCurrentVendorUri = getIntent().getData();
        if (this.mCurrentVendorUri == null) {
            setTitle("Add Vendor");
            invalidateOptionsMenu();
        } else {
            setTitle("Edit Vendor");
            this.callVendor.setVisibility(View.VISIBLE);
            getSupportLoaderManager().initLoader(0, (Bundle) null, this);
        }
        this.mVendorName = (EditText) findViewById(R.id.vendor_name);
        this.mVendorCompany = (EditText) findViewById(R.id.vendor_company);
        this.mVendorLocation = (EditText) findViewById(R.id.vendor_address);
        this.mVendorContact = (EditText) findViewById(R.id.vendor_contact);
        this.mVendorName.setOnTouchListener(this.mTouchListener);
        this.mVendorCompany.setOnTouchListener(this.mTouchListener);
        this.mVendorLocation.setOnTouchListener(this.mTouchListener);
        this.mVendorContact.setOnTouchListener(this.mTouchListener);
    }

    private void saveVendor() {
        String trim = this.mVendorName.getText().toString().trim();
        String trim2 = this.mVendorCompany.getText().toString().trim();
        String trim3 = this.mVendorLocation.getText().toString().trim();
        String trim4 = this.mVendorContact.getText().toString().trim();
        if (TextUtils.isEmpty(trim)) {
            this.mVendorName.setError("Field Required");
        }
        if (TextUtils.isEmpty(trim2)) {
            this.mVendorCompany.setError("Field Required");
        }
        if (TextUtils.isEmpty(trim3)) {
            this.mVendorLocation.setError("Field Required");
        }
        if (TextUtils.isEmpty(trim4)) {
            this.mVendorContact.setError("Required Field");
        }
        if (this.mVendorContact.length() < 7) {
            this.mVendorContact.setError("At least 7 digits");
        }
        if (!TextUtils.isEmpty(trim) && !TextUtils.isEmpty(trim2) && !TextUtils.isEmpty(trim3) && !TextUtils.isEmpty(trim4) && this.mVendorContact.length() > 7) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("name", trim);
            contentValues.put(VendorContract.VendorEntry.COLUMN_VENDOR_COMPANY, trim2);
            contentValues.put(VendorContract.VendorEntry.COLUMN_VENDOR_ADDRESS, trim3);
            contentValues.put(VendorContract.VendorEntry.COLUMN_VENDOR_CONTACT, trim4);
            if (this.mCurrentVendorUri == null) {
                if (getContentResolver().insert(VendorContract.VendorEntry.CONTENT_URI, contentValues) == null) {
                    Toast.makeText(this, "Insert vendor failed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Insert vendor successful", Toast.LENGTH_SHORT).show();
                }
            } else if (getContentResolver().update(this.mCurrentVendorUri, contentValues, (String) null, (String[]) null) == Toast.LENGTH_SHORT) {
                Toast.makeText(this, "Vendor update unsuccessful", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Vendor update successful", Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }

    /* access modifiers changed from: private */
    public void call_vendor() {
        Intent intent = new Intent("android.intent.action.DIAL");
        intent.setData(Uri.fromParts("tel", "+" + this.mVendorContact.getText().toString(), (String) null));
        if (ActivityCompat.checkSelfPermission(this, "android.permission.CALL_PHONE") != 0) {
            startActivity(intent);
        } else {
            startActivity(intent);
        }
    }

    /* access modifiers changed from: private */
    public void deleteVendor() {
        if (this.mCurrentVendorUri != null) {
            if (getContentResolver().delete(this.mCurrentVendorUri, (String) null, (String[]) null) == 0) {
                Toast.makeText(this, "Error with deleting vendor", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Vendor deleted", Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }

    public void onBackPressed() {
        if (!this.mProductHasChanged) {
            super.onBackPressed();
        } else {
            showUnsavedChangesDialog(new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    AddVendor.this.finish();
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
        getMenuInflater().inflate(R.menu.menu_add_vendor, menu);
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (this.mCurrentVendorUri != null) {
            return true;
        }
        menu.findItem(R.id.vendor_delete).setVisible(false);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.vendor_delete) {
            showDeleteConfirmationDialog();
            return true;
        } else if (itemId != R.id.vendor_save) {
            return super.onOptionsItemSelected(menuItem);
        } else {
            saveVendor();
            return true;
        }
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage((CharSequence) "Delete this product");
        builder.setPositiveButton((CharSequence) "Delete", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                AddVendor.this.deleteVendor();
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

    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, this.mCurrentVendorUri, new String[]{"_id", "name", VendorContract.VendorEntry.COLUMN_VENDOR_COMPANY, VendorContract.VendorEntry.COLUMN_VENDOR_ADDRESS, VendorContract.VendorEntry.COLUMN_VENDOR_CONTACT}, (String) null, (String[]) null, (String) null);
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToNext()) {
            int columnIndex = cursor.getColumnIndex("name");
            int columnIndex2 = cursor.getColumnIndex(VendorContract.VendorEntry.COLUMN_VENDOR_COMPANY);
            int columnIndex3 = cursor.getColumnIndex(VendorContract.VendorEntry.COLUMN_VENDOR_ADDRESS);
            int columnIndex4 = cursor.getColumnIndex(VendorContract.VendorEntry.COLUMN_VENDOR_CONTACT);
            String string = cursor.getString(columnIndex);
            String string2 = cursor.getString(columnIndex2);
            String string3 = cursor.getString(columnIndex3);
            String string4 = cursor.getString(columnIndex4);
            this.mVendorName.setText(string);
            this.mVendorCompany.setText(string2);
            this.mVendorLocation.setText(string3);
            this.mVendorContact.setText(string4);
        }
    }
}
