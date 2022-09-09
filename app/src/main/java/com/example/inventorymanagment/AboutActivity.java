package com.example.inventorymanagment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class AboutActivity extends AppCompatActivity {

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_about);

        ((TextView) findViewById(R.id.site)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
//                Intent intent = new Intent("android.intent.action.VIEW");
//                intent.setData(Uri.parse("link"));
//                AboutActivity.this.startActivity();
            }
        });
    }

    public void onBackPressed() {
        super.onBackPressed();
    }
}
