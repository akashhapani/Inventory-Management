package com.example.inventorymanagment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.internal.view.SupportMenu;
import androidx.core.view.GravityCompat;
import androidx.core.view.InputDeviceCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.inventorymanagment.data.ItemizeDbHelper;
import com.example.inventorymanagment.data.StaticFunctions;
import com.example.inventorymanagment.model.SaleProduct;
import com.google.android.material.navigation.NavigationView;

import org.achartengine.ChartFactory;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import java.util.Random;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String SHOWCASE_ID = "sequence example";

    ItemizeDbHelper DbHelper;
    Button SalesDetail;
    Button addProduct;
    Button addVendor;
    String consent = "1";
    Button pieChart;
    Button productList;
    Button purchaseDetail;
    Button saleProduct;
    Button vendorList;
    private int[] grantResults;
    private final String rewardedPlacementId = "rewardedVideo";

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_main);

        this.DbHelper = new ItemizeDbHelper(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        ((NavigationView) findViewById(R.id.nav_view)).setNavigationItemSelectedListener(this);
        this.addProduct = (Button) findViewById(R.id.add_product);
        this.addProduct.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MainActivity.this.startActivity(new Intent(MainActivity.this, AddProduct.class));
            }
        });
        this.saleProduct = (Button) findViewById(R.id.sale_product);
        this.saleProduct.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MainActivity.this.startActivity(new Intent(MainActivity.this, SaleProduct.class));
            }
        });
        this.productList = (Button) findViewById(R.id.product_list);
        this.productList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MainActivity.this.startActivity(new Intent(MainActivity.this, ProductList.class));
            }
        });
        this.addVendor = (Button) findViewById(R.id.add_vendor);
        this.addVendor.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MainActivity.this.startActivity(new Intent(MainActivity.this, AddVendor.class));
            }
        });
        this.vendorList = (Button) findViewById(R.id.vendor_list);
        this.vendorList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MainActivity.this.startActivity(new Intent(MainActivity.this, VendorList.class));
            }
        });
        this.pieChart = (Button) findViewById(R.id.pie_chart);
        this.pieChart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Cursor productsTableExists = MainActivity.this.DbHelper.productsTableExists();
                if (productsTableExists == null) {
                    Toast.makeText(MainActivity.this, "Add product first", Toast.LENGTH_SHORT).show();
                } else if (productsTableExists.moveToFirst()) {
                    MainActivity.this.openChart();
                }
            }
        });
        this.purchaseDetail = (Button) findViewById(R.id.purchaseDetail);
        this.purchaseDetail.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MainActivity.this.DbHelper.getReadableDatabase();
                Cursor purchaseTable = MainActivity.this.DbHelper.getPurchaseTable();
                if (purchaseTable == null) {
                    Toast.makeText(MainActivity.this, "There is no Purchase transaction", Toast.LENGTH_SHORT).show();
                } else if (purchaseTable.moveToFirst()) {
                    MainActivity.this.startActivity(new Intent(MainActivity.this, PurchaseDetail.class));
                }
            }
        });
        this.SalesDetail = (Button) findViewById(R.id.sales_detail);
        this.SalesDetail.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MainActivity.this.DbHelper.getReadableDatabase();
                Cursor salesTable = MainActivity.this.DbHelper.getSalesTable();
                if (salesTable == null) {
                    Toast.makeText(MainActivity.this, "There is no Sales transaction", Toast.LENGTH_SHORT).show();
                } else if (salesTable.moveToFirst()) {
                    MainActivity.this.startActivity(new Intent(MainActivity.this, SalesDetail.class));
                }
            }
        });
        askForPermission();

    }

    private void askForPermission() {
        if (ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") == -1) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 1);
            onRequestPermissionsResult(1, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, grantResults);
        }
    }
//    private void loadRewardedVideoAd() {
//        this.mRewardedVideoAd.loadAd("ca-app-pub-9965955769930091/4151339715", new AdRequest.Builder().build());
//    }
//
//    public void loadInterstitial() {
//        this.mInterstitial = new InterstitialAd(this);
//        this.mInterstitial.setAdUnitId("ca-app-pub-9965955769930091/1336564068");
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

    public void openChart() {
        String[] arrayFromCursor = arrayFromCursor(this.DbHelper.selectColumnNames());
        int[] qArrayFromCursor = qArrayFromCursor(this.DbHelper.selectQuantity());
        @SuppressLint("RestrictedApi") int[] iArr = {-16776961, -65281, -16711936, -16711681, SupportMenu.CATEGORY_MASK, InputDeviceCompat.SOURCE_ANY, -7829368, -16711936};
        CategorySeries categorySeries = new CategorySeries(" Percentage of products in the inventory ");
        for (int i = 0; i < qArrayFromCursor.length; i++) {
            categorySeries.add(arrayFromCursor[i], (double) qArrayFromCursor[i]);
        }
        DefaultRenderer defaultRenderer = new DefaultRenderer();
        for (int i2 = 0; i2 < qArrayFromCursor.length; i2++) {
            SimpleSeriesRenderer simpleSeriesRenderer = new SimpleSeriesRenderer();
            if (i2 < iArr.length) {
                simpleSeriesRenderer.setColor(iArr[i2]);
            } else {
                simpleSeriesRenderer.setColor(getRandomColor());
            }
            simpleSeriesRenderer.setDisplayBoundingPoints(true);
            defaultRenderer.addSeriesRenderer(simpleSeriesRenderer);
        }
        defaultRenderer.setChartTitle("Percentage of products in the inventory ");
        defaultRenderer.setChartTitleTextSize(50.0f);
        defaultRenderer.setLabelsColor(-7829368);
        defaultRenderer.setLabelsTextSize(30.0f);
        defaultRenderer.setZoomButtonsVisible(false);
        startActivity(ChartFactory.getPieChartIntent(getBaseContext(), categorySeries, defaultRenderer, "AChartEnginePieChartDemo"));
    }

    private int getRandomColor() {
        Random random = new Random();
        return Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
    }

    @SuppressLint("Range")
    public String[] arrayFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        String[] strArr = new String[count];
        if (cursor.moveToFirst()) {
            for (int i = 0; i < count; i++) {
                strArr[i] = cursor.getString(cursor.getColumnIndex("name"));
                cursor.moveToNext();
            }
        }
        return strArr;
    }

    @SuppressLint("Range")
    public int[] qArrayFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        int[] iArr = new int[count];
        if (cursor.moveToFirst()) {
            for (int i = 0; i < count; i++) {
                iArr[i] = cursor.getInt(cursor.getColumnIndex("quantity"));
                cursor.moveToNext();
            }
        }
        return iArr;
    }

    public void onBackPressed() {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawerLayout.isDrawerOpen((int) GravityCompat.START)) {
            drawerLayout.closeDrawer((int) GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != R.id.help) {
            return super.onOptionsItemSelected(menuItem);
        }
        MaterialShowcaseView.resetSingleUse(this, SHOWCASE_ID);
        presentShowcaseSequence();
        return true;
    }

    public boolean onNavigationItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId != R.id.home) {
            if (itemId == R.id.share) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.SEND");
                intent.putExtra("android.intent.extra.TEXT", "Hey check out my app at: Cooming soon.......");
                intent.setType("text/plain");
                startActivity(intent);
//            } else if (itemId == R.id.rate) {
//                Intent intent2 = new Intent("android.intent.action.VIEW");
//                intent2.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
//                startActivity(intent2);
            } else if (itemId == R.id.user_manual) {
                startActivity(new Intent(this, HowTo.class));
            } else if (itemId == R.id.about) {
                startActivity(new Intent(this, AboutActivity.class));
            } else if (itemId == R.id.facebook) {
                Intent intent3 = new Intent("android.intent.action.VIEW");
                intent3.setData(Uri.parse("https://www.facebook.com/profile.php?id=100010650944333"));
                startActivity(intent3);
            } else if (itemId == R.id.contact_us) {
                Intent intent4 = new Intent("android.intent.action.VIEW");
                intent4.setData(Uri.parse("mailto: akashhapani2201@gmail.com"));
                startActivity(intent4);
//            } else if (itemId == R.id.support_us) {
////                loadRewardedVideoAd();
//                StaticFunctions.showProgress(this);
            }
        }
        ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer((int) GravityCompat.START);
        return true;
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
    }

    private void presentShowcaseSequence() {
        ShowcaseConfig showcaseConfig = new ShowcaseConfig();
        showcaseConfig.setDelay(500);
        MaterialShowcaseSequence materialShowcaseSequence = new MaterialShowcaseSequence(this, SHOWCASE_ID);
        materialShowcaseSequence.setOnItemShownListener(new MaterialShowcaseSequence.OnSequenceItemShownListener() {
            public void onShow(MaterialShowcaseView materialShowcaseView, int i) {

            }
        });
        materialShowcaseSequence.setConfig(showcaseConfig);
        materialShowcaseSequence.addSequenceItem(new MaterialShowcaseView.Builder(this).setTarget(this.addVendor).setContentText((CharSequence) "Add vendor first of all, because you will be allowed to enter the products corresponding to some vendor").setTitleText((CharSequence) "ADD VENDOR").setDismissText((CharSequence) "NEXT").withRectangleShape().build());
        materialShowcaseSequence.addSequenceItem(new MaterialShowcaseView.Builder(this).setTarget(this.addProduct).setDismissText((CharSequence) "NEXT").setTitleText((CharSequence) "ADD PRODUCT").setContentText((CharSequence) "Now add the product that is supplied by the vendor already present in vendor list").withRectangleShape().build());
        materialShowcaseSequence.addSequenceItem(new MaterialShowcaseView.Builder(this).setTarget(this.saleProduct).setTitleText((CharSequence) "SALE PRODUCT").setDismissText((CharSequence) "NEXT").setContentText((CharSequence) "Now you can sale any produ ct that is stored in the product list").withRectangleShape().build());
        materialShowcaseSequence.start();
    }


}
