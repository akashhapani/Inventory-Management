package com.example.inventorymanagment;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.example.inventorymanagment.data.ItemizeContract;
import com.example.inventorymanagment.data.ItemizeDbHelper;
import com.example.inventorymanagment.data.MyObject;
import com.example.inventorymanagment.data.PurchaseContract;
import com.example.inventorymanagment.listner.addProductCustomAutoCompleteTextChangedListener;
import com.example.inventorymanagment.model.setDate;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class AddProduct extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int EXISTING_ITEMIZE_LOADER = 0;
    /* access modifiers changed from: private */
    public boolean mProductHasChanged = false;
    /* renamed from: bm */
    Bitmap f1176bm;
    public String[] item = {"Please search..."};
    ItemizeDbHelper mDbHelper;
    public ArrayAdapter<String> myAdapter;
    Bitmap pic;
    byte[] pictureArray;
    ImageView product_Img;
    public CustomAutoCompleteView supplierAutoComplete;
    Button supplierButton;
    String supplierString;
    Bitmap updatePhoto;
    private int REQUEST_CAMERA = 0;
    private int SELECT_FILE = 1;
    private EditText dateEditText;
    private Uri mCurrentProductUri;
    private EditText mDescEditText;
    private EditText mNameEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private EditText mSupplierEditText;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            boolean unused = AddProduct.this.mProductHasChanged = true;
            return false;
        }
    };
    private boolean supplierMatchFound;

    private static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public void onLoaderReset(Loader<Cursor> loader) {
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.add_product);
        this.mDbHelper = new ItemizeDbHelper(this);

        this.mCurrentProductUri = getIntent().getData();
        Button button = (Button) findViewById(R.id.button);
        if (this.mCurrentProductUri == null) {
            setTitle("Add Product");
            invalidateOptionsMenu();
        } else {
            setTitle("Edit Product");
            button.setVisibility(View.GONE);
            getSupportLoaderManager().initLoader(0, (Bundle) null, this);
        }
        this.mNameEditText = (EditText) findViewById(R.id.edit_name);
        this.mPriceEditText = (EditText) findViewById(R.id.edit_price);
        this.mQuantityEditText = (EditText) findViewById(R.id.edit_quantity);
        this.mDescEditText = (EditText) findViewById(R.id.edit_desc);
        this.supplierButton = (Button) findViewById(R.id.supplier_button);
        this.supplierButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AddProduct.this.startActivity(new Intent(AddProduct.this, AddVendor.class));
            }
        });
        this.mNameEditText.setOnTouchListener(this.mTouchListener);
        this.mPriceEditText.setOnTouchListener(this.mTouchListener);
        this.mQuantityEditText.setOnTouchListener(this.mTouchListener);
        this.mDescEditText.setOnTouchListener(this.mTouchListener);
        this.product_Img = (ImageView) findViewById(R.id.productImage);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AddProduct.this.addImage();
            }
        });
        this.dateEditText = (EditText) findViewById(R.id.dateEtext);
        new setDate(this, this.dateEditText);
        try {
            this.supplierAutoComplete = (CustomAutoCompleteView) findViewById(R.id.edit_supplier);
            this.supplierAutoComplete.addTextChangedListener(new addProductCustomAutoCompleteTextChangedListener(this));
            this.myAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, this.item);
            this.supplierAutoComplete.setAdapter(this.myAdapter);
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public String[] getSupplierFromProductDb(String str) {
        List<MyObject> readSupplier = this.mDbHelper.readSupplier(str);
        String[] strArr = new String[readSupplier.size()];
        int i = 0;
        for (MyObject myObject : readSupplier) {
            strArr[i] = myObject.objectName;
            i++;
        }
        return strArr;
    }

    /* access modifiers changed from: private */
    public void addImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage((CharSequence) "Upload image using");
        builder.setPositiveButton((CharSequence) "Camera", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                AddProduct.this.cameraIntent();
            }
        });
        builder.setNegativeButton((CharSequence) "Gallery", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                AddProduct.this.galleryIntent();
            }
        });
        builder.create().show();
    }

    /* access modifiers changed from: private */
    public void cameraIntent() {
        startActivityForResult(new Intent("android.media.action.IMAGE_CAPTURE"), this.REQUEST_CAMERA);
    }

    /* access modifiers changed from: private */
    public void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction("android.intent.action.GET_CONTENT");
        startActivityForResult(Intent.createChooser(intent, "Select File"), this.SELECT_FILE);
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i2 != -1) {
            return;
        }
        if (i == this.SELECT_FILE) {
            onSelectFromGalleryResult(intent);
        } else if (i == this.REQUEST_CAMERA) {
            onCaptureImageResult(intent);
        }
    }

    private void onCaptureImageResult(Intent intent) {
        Bitmap bitmap = (Bitmap) intent.getExtras().get("data");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        File file = new File(externalStorageDirectory, System.currentTimeMillis() + ".jpg");
        try {
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(byteArrayOutputStream.toByteArray());
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        this.product_Img.setImageBitmap(bitmap);
        this.pic = bitmap;
    }

    private void onSelectFromGalleryResult(Intent intent) {
        if (intent != null) {
            try {
                this.f1176bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), intent.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.product_Img.setImageBitmap(this.f1176bm);
        this.pic = this.f1176bm;
    }

    private void saveProduct() {
        Cursor query;
        this.mDbHelper.getWritableDatabase();
        String trim = this.mNameEditText.getText().toString().trim();
        String trim2 = this.mPriceEditText.getText().toString().trim();
        String trim3 = this.mQuantityEditText.getText().toString().trim();
        String trim4 = this.dateEditText.getText().toString().trim();
        this.supplierString = this.supplierAutoComplete.getText().toString().trim();
        String trim5 = this.mDescEditText.getText().toString().trim();
        if (this.mCurrentProductUri == null) {
            Bitmap bitmap = this.pic;
            if (bitmap == null) {
                this.pictureArray = getBytes(BitmapFactory.decodeResource(getResources(), R.drawable.no_image));
            } else {
                this.pictureArray = getBytes(bitmap);
            }
        }
        if (!(this.mCurrentProductUri == null || (query = getContentResolver().query(this.mCurrentProductUri, new String[]{ItemizeContract.ItemizeEntry.COLUMN_PRODUCT_PICTURE}, trim, (String[]) null, (String) null)) == null || !query.moveToFirst())) {
            this.pictureArray = query.getBlob(0);
        }
        if (TextUtils.isEmpty(trim)) {
            this.mNameEditText.setError(" Field Required ");
        }
        if (TextUtils.isEmpty(trim2)) {
            this.mPriceEditText.setError(" Field Required ");
        }
        if (TextUtils.isEmpty(trim3)) {
            this.mQuantityEditText.setError(" Field Required ");
        }
        if (TextUtils.isEmpty(trim4)) {
            this.dateEditText.setError(" Field Required ");
        }
        matchSupplierAC();
        if (!TextUtils.isEmpty(trim) && !TextUtils.isEmpty(trim2) && !TextUtils.isEmpty(trim3) && !TextUtils.isEmpty(trim4) && !TextUtils.isEmpty(this.supplierString) && this.supplierMatchFound) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("name", trim);
            contentValues.put("date", trim4);
            contentValues.put("supplier", this.supplierString);
            ContentValues contentValues2 = new ContentValues();
            contentValues2.put("name", trim);
            contentValues2.put("date", trim4);
            contentValues2.put("supplier", this.supplierString);
            if (TextUtils.isEmpty(trim5)) {
                contentValues.put("description", "Not available");
                contentValues2.put("description", "Not available");
            } else {
                contentValues.put("description", trim5);
                contentValues2.put("description", trim5);
            }
            contentValues.put(ItemizeContract.ItemizeEntry.COLUMN_PRODUCT_PICTURE, this.pictureArray);
            int parseInt = !TextUtils.isEmpty(trim2) ? Integer.parseInt(trim2) : 0;
            contentValues.put("price", Integer.valueOf(parseInt));
            contentValues2.put("price", Integer.valueOf(parseInt));
            int parseInt2 = !TextUtils.isEmpty(trim3) ? Integer.parseInt(trim3) : 0;
            contentValues.put("quantity", Integer.valueOf(parseInt2));
            contentValues2.put("quantity", Integer.valueOf(parseInt2));
            if (this.mCurrentProductUri == null) {
                if (getContentResolver().insert(ItemizeContract.ItemizeEntry.CONTENT_URI, contentValues) == null) {
                    Toast.makeText(this, "Insert product failed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Insert product successful", Toast.LENGTH_SHORT).show();
                }
                if (getContentResolver().insert(PurchaseContract.PurchaseEntry.CONTENT_URI, contentValues2) == null) {
                    Toast.makeText(this, "Insert product failed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Insert product successful", Toast.LENGTH_SHORT).show();
                }
                finish();
                return;
            }
            if (getContentResolver().update(this.mCurrentProductUri, contentValues, (String) null, (String[]) null) == 0) {
                Toast.makeText(this, "Product update unsuccessful", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Update product successful", Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }

    private void matchSupplierAC() {
        if (this.mDbHelper.getSuppliersList().contains(this.supplierString)) {
            this.supplierMatchFound = true;
            return;
        }
        this.supplierMatchFound = false;
        this.supplierAutoComplete.setError("Add supplier first");
        this.supplierButton.setVisibility(View.VISIBLE);
    }

    public void onBackPressed() {
        if (!this.mProductHasChanged) {
            super.onBackPressed();
        } else {
            showUnsavedChangesDialog(new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    AddProduct.this.finish();
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
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (this.mCurrentProductUri != null) {
            return true;
        }
        menu.findItem(R.id.action_delete).setVisible(false);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.action_delete) {
            showDeleteConfirmationDialog();
            return true;
        } else if (itemId != R.id.action_save) {
            return super.onOptionsItemSelected(menuItem);
        } else {
            saveProduct();
            return true;
        }
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage((CharSequence) "Delete this product");
        builder.setPositiveButton((CharSequence) "Delete", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                AddProduct.this.deleteProduct();
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
    public void deleteProduct() {
        if (this.mCurrentProductUri != null) {
            if (getContentResolver().delete(this.mCurrentProductUri, (String) null, (String[]) null) == 0) {
                Toast.makeText(this, "Error with deleting product", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Product deleted", Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }

    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        return new CursorLoader(this, this.mCurrentProductUri, new String[]{"_id", ItemizeContract.ItemizeEntry.COLUMN_PRODUCT_PICTURE, "name", "price", "quantity", "date", "supplier", "description"}, (String) null, (String[]) null, (String) null);
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToNext()) {
            int columnIndex = cursor.getColumnIndex(ItemizeContract.ItemizeEntry.COLUMN_PRODUCT_PICTURE);
            int columnIndex2 = cursor.getColumnIndex("name");
            int columnIndex3 = cursor.getColumnIndex("price");
            int columnIndex4 = cursor.getColumnIndex("quantity");
            int columnIndex5 = cursor.getColumnIndex("date");
            int columnIndex6 = cursor.getColumnIndex("supplier");
            int columnIndex7 = cursor.getColumnIndex("description");
            byte[] blob = cursor.getBlob(columnIndex);
            String string = cursor.getString(columnIndex2);
            int i = cursor.getInt(columnIndex3);
            int i2 = cursor.getInt(columnIndex4);
            String string2 = cursor.getString(columnIndex5);
            String string3 = cursor.getString(columnIndex6);
            String string4 = cursor.getString(columnIndex7);
            this.product_Img.setImageBitmap(getPhoto(blob));
            this.mNameEditText.setText(string);
            this.mPriceEditText.setText(Integer.toString(i));
            this.mQuantityEditText.setText(Integer.toString(i2));
            this.dateEditText.setText(string2);
            this.supplierAutoComplete.setText(string3);
            this.mDescEditText.setText(string4);
        }
    }

    public Bitmap getPhoto(byte[] bArr) {
        this.updatePhoto = BitmapFactory.decodeByteArray(bArr, 0, bArr.length);
        return this.updatePhoto;
    }
}
