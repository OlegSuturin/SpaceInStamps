package com.oliverst.spaceinstamps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.oliverst.spaceinstamps.utils.ScaledImageView;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import static android.os.Environment.DIRECTORY_PICTURES;

public class ScaledImageActivity extends AppCompatActivity {
    private String url;
    ScaledImageView scaledImageView;

    //------------------------------------------------menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.scaled_image_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int idMenu = item.getItemId();
        switch (idMenu) {
            case R.id.itemToShare:
                boolean b = false;
                scaledImageView.buildDrawingCache();
                Bitmap bitmap = scaledImageView.getDrawingCache();

                OutputStream fOut = null;
                Uri outputFileUri;

                File sdImageMainDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "stamp.jpg");
                try {

                    fOut = new FileOutputStream(sdImageMainDirectory);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Log.i("!@#", e.toString());
                }

                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                try {
                    fOut.flush();
                    fOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/jpeg");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                Uri uri = FileProvider.getUriForFile(this, "com.example.fileprovider", sdImageMainDirectory);

                // Log.i("!@#", uri.toString());
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(shareIntent, "Share with"));

                break;

            case R.id.itemCast:

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scaled_image);
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {/* ... */}

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
        }).check();


        scaledImageView = findViewById(R.id.scaledImageView);
        scaledImageView.buildDrawingCache();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
        }

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("url")) {
            url = intent.getStringExtra("url");
            //Toast.makeText(this, url, Toast.LENGTH_SHORT).show();
        } else {
            finish();
        }

        Picasso.get().load(url).placeholder(R.drawable.placeholder).into(scaledImageView);

    }
}