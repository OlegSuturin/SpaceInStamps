package com.oliverst.spaceinstamps;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.oliverst.spaceinstamps.utils.ScaledImageView;
import com.squareup.picasso.Picasso;

public class ScaledImageActivity extends AppCompatActivity {
   private String url;
   ScaledImageView scaledImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scaled_image);
        scaledImageView = findViewById(R.id.scaledImageView);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.hide();
        }

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("url")) {
            url = intent.getStringExtra("url");
            //Toast.makeText(this, url, Toast.LENGTH_SHORT).show();
        }else {
            finish();
        }

        Picasso.get().load(url).placeholder(R.drawable.placeholder).into(scaledImageView);

    }
}