package com.oliverst.russianstamps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.mediarouter.app.MediaRouteButton;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaLoadRequestData;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.Session;
import com.google.android.gms.cast.framework.SessionManager;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.oliverst.russianstamps.utils.ScaledImageView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class ScaledImageActivity extends AppCompatActivity  {
    private String url;
    private ScaledImageView scaledImageView;
    private CastSession mCastSession;
    private SessionManager mSessionManager;
    private CastContext castContext;
     private com.google.android.gms.cast.MediaMetadata  imageMetadata;
   private MediaInfo mediaInfo;
     private RemoteMediaClient remoteMediaClient;
     private MediaRouteButton mMediaRouteButton;

    private final SessionManagerListener mSessionManagerListener =   new SessionManagerListenerImpl();


    //слушатель событий Хромкаста--------------------------------------------------------------------------------

    private class SessionManagerListenerImpl implements SessionManagerListener {
        @Override
        public void onSessionStarting(Session session) {

        }

        @Override
        public void onSessionStarted(Session session, String s) {

            invalidateOptionsMenu();

            mCastSession = mSessionManager.getCurrentCastSession();
            if(mCastSession!=null){
                imageMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_PHOTO);    //определяем метаданные мультимедиа материала
                mediaInfo = new MediaInfo.Builder(url)                      //определяем тип данных и создаем мультимедийный экземпляр
                        .setStreamType(MediaInfo.STREAM_TYPE_NONE)
                        .setContentType("image/*")
                        .setMetadata(imageMetadata)
                        .build();

                remoteMediaClient = mCastSession.getRemoteMediaClient();   // Инициализируем объект фреймворка для работы с хромкастом CastContext - коордириет все взаймодействия с фреймворком
                remoteMediaClient.load(new MediaLoadRequestData.Builder().setMediaInfo(mediaInfo).build());
            }

        }

        @Override
        public void onSessionStartFailed(Session session, int i) {
        }

        @Override
        public void onSessionEnding(Session session) {

        }

        @Override
        public void onSessionEnded(Session session, int i) {
            finish();
        }

        @Override
        public void onSessionResuming(Session session, String s) {

        }

        @Override
        public void onSessionResumed(Session session, boolean b) {
            invalidateOptionsMenu();
        }

        @Override
        public void onSessionResumeFailed(Session session, int i) {

        }

        @Override
        public void onSessionSuspended(Session session, int i) {

        }
    };

    //------------------------------------------------menu----------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.scaled_image_menu, menu);
        CastButtonFactory.setUpMediaRouteButton(getApplicationContext(), menu, R.id.media_route_menu_item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int idMenu = item.getItemId();

        switch (idMenu) {


            case R.id.media_route_menu_item:       //ChromeCast изображения на ТВ

                //логика в слушателе

            case R.id.itemToShare:         //поделиться изображением через соцсеть
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
                }
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                try {
                    if(fOut!=null){
                        fOut.flush();
                        fOut.close();
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("image/jpeg");
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        Uri uri = FileProvider.getUriForFile(this, "com.oliverst.russianstamps.fileprovider", sdImageMainDirectory);

                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        startActivity(Intent.createChooser(shareIntent, "Share with"));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }
//-----------------------------------------------------------------------------------------------------
    @Override
    protected void onResume() {
            mCastSession = mSessionManager.getCurrentCastSession();    // получаем доступ к текущему активному сеансу

        mSessionManager.addSessionManagerListener(mSessionManagerListener);
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSessionManager.removeSessionManagerListener(mSessionManagerListener);
        mCastSession = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        castContext  = CastContext.getSharedInstance(this);
        mSessionManager = CastContext.getSharedInstance(this).getSessionManager();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scaled_image);
        scaledImageView = findViewById(R.id.scaledImageView);

        //--------------------------------------------------------модуль запроса необходимых разрешений релтайм
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

//----------------------------------------------------------------------------------------------
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
        }

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("url")) {
            url = intent.getStringExtra("url");
        } else {
            finish();
        }

        Picasso.get().load(url).placeholder(R.drawable.placeholder).into(scaledImageView);

//----------------------------------------------------------------------------- вывод марки на хромкаст
        mCastSession = mSessionManager.getCurrentCastSession();
            if(mCastSession!=null){
                imageMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_PHOTO);    //определяем метаданные мультимедиа материала
                mediaInfo = new MediaInfo.Builder(url)                      //определяем тип данных и создаем мультимедийный экземпляр
                        .setStreamType(MediaInfo.STREAM_TYPE_NONE)
                        .setContentType("image/*")
                        .setMetadata(imageMetadata)
                        .build();

                remoteMediaClient = mCastSession.getRemoteMediaClient();   // Инициализируем объект фреймворка для работы с хромкастом CastContext - коордириет все взаймодействия с фреймворком
                remoteMediaClient.load(new MediaLoadRequestData.Builder().setMediaInfo(mediaInfo).build());
            }


    }

}
