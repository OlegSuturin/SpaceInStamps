package com.oliverst.spaceinstamps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.oliverst.spaceinstamps.adapters.ImagesAdapter;
import com.oliverst.spaceinstamps.adapters.StampAdapter;
import com.oliverst.spaceinstamps.data.ImageUrl;
import com.oliverst.spaceinstamps.data.MainViewModel;
import com.oliverst.spaceinstamps.data.Stamp;
import com.oliverst.spaceinstamps.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DetailStamp extends AppCompatActivity {
    private long id;
    private TextView textViewCountryInfo;
    private TextView textViewYearInfo;
    private TextView textViewNameInfo;
    private TextView textViewQuantityInfo;
    private TextView textViewDateReleaseInfo;
    private TextView textViewCatalogNumbersInfo;
    private TextView textViewPriceInfo;
    private TextView textViewSpecificationsInfo;
    private TextView textViewOverviewInfo;
    // private ImageView imageViewBigStamp;
    private RecyclerView recyclerViewImagesInfo;
    private ImagesAdapter adapter;
    private MainViewModel viewModel;
    private Stamp stamp;
    private String detailUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_stamp);

        textViewCountryInfo = findViewById(R.id.textViewCountryInfo);
        textViewYearInfo = findViewById(R.id.textViewYearInfo);
        textViewNameInfo = findViewById(R.id.textViewNameInfo);
        textViewQuantityInfo = findViewById(R.id.textViewQuantityInfo);
        textViewDateReleaseInfo = findViewById(R.id.textViewDateReleaseInfo);
        textViewCatalogNumbersInfo = findViewById(R.id.textViewCatalogNumbersInfo);
        textViewPriceInfo = findViewById(R.id.textViewPriceInfo);
        textViewSpecificationsInfo = findViewById(R.id.textViewSpecificationsInfo);
        textViewOverviewInfo = findViewById(R.id.textViewOverviewInfo);
        //imageViewBigStamp = findViewById(R.id.imageViewBigStamp);
        adapter = new ImagesAdapter();
        recyclerViewImagesInfo = findViewById(R.id.recyclerViewImagesInfo);


        Intent intent = getIntent();                                    //! проверяем Интент и наличие параметров
        if (intent != null && intent.hasExtra("id")) {
            id = intent.getLongExtra("id", -1);
        } else {
            finish();               //  закрываем активность, если что то не так
        }
        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(MainViewModel.class);
        stamp = viewModel.getStampById(id);
        if (stamp==null){
            Toast.makeText(this, "Информация в БД не найдена!", Toast.LENGTH_SHORT).show();
            finish();               //  закрываем активность, если что то не так
        }

        if (!stamp.isFlag()) {   //загрузка детальной информации из интернета
            downloadDetail();

        }

        applyDetail();  //применить детальную информацию на экран

        adapter.setOnImageClickListener(new ImagesAdapter.OnImageClickListener() {
            @Override
            public void onImageClick(int position) {
                Toast.makeText(DetailStamp.this, "" + position, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void downloadDetail(){
        //загрузка детальной информации
        String urlAsStringDetail = stamp.getDetailUrl();
        //"http://www.philately.ru/cgi-bin/sql/search1.cgi?action=view_details&id=3863";
        //stamp.getDetailUrl();
        String data = NetworkUtils.getDetailFromNetwork(urlAsStringDetail);
        if (data == null) {
            Toast.makeText(this, "данные не загружены", Toast.LENGTH_SHORT).show();
            finish();               //  закрываем активность, если что то не так
        }
        Stamp stampDetail = NetworkUtils.parserDetailStamp(data, Integer.toString(stamp.getYear()));
        stamp.setCountry(stampDetail.getCountry());
        stamp.setDateRelease(stampDetail.getDateRelease());
        stamp.setOverview(stampDetail.getOverview());
        stamp.setSpecifications(stampDetail.getSpecifications());
        //stamp.setBigPhotoPath(stampDetail.getBigPhotoPath());
        //пути к картинкам здесь

        ArrayList<String> imagesUrlString = NetworkUtils.parseImagesUrl(data);

        for (int i = 0; i < imagesUrlString.size(); i++) {
            ImageUrl imageUrl = new ImageUrl(stamp.getIdStamp(), imagesUrlString.get(i));
            // Log.i("!@#", imageUrl.getUrl());
            viewModel.insertImageUrl(imageUrl);
        }

        stamp.setFlag(true);  // установили flag - информация загружена вся
        viewModel.updateStamp(stamp);
    }

    public void applyDetail(){

        textViewCountryInfo.setText(stamp.getCountry());
        textViewYearInfo.setText(Integer.toString(stamp.getYear()));
        textViewNameInfo.setText(stamp.getName());
        textViewQuantityInfo.setText(stamp.getQuantity());
        textViewDateReleaseInfo.setText(stamp.getDateRelease());
        textViewPriceInfo.setText(stamp.getPrice());
        textViewSpecificationsInfo.setText(stamp.getSpecifications());
        textViewOverviewInfo.setText(stamp.getOverview());
        String catalogNumbers = String.format("ИТС: %s СК: %s Михель: %s", stamp.getCatalogNumberITC(), stamp.getCatalogNumberSK(), stamp.getCatalogNumberMich());
        textViewCatalogNumbersInfo.setText(catalogNumbers);

        List<ImageUrl> imagesUrl = viewModel.getImagesUrlById(stamp.getIdStamp());

        recyclerViewImagesInfo.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewImagesInfo.setAdapter(adapter);
        adapter.setImagesUrl(imagesUrl);
    }

    public void onClickLeft(View view) {
        id--;
        stamp = viewModel.getStampById(id);
        if (stamp==null){
            Toast.makeText(this, "Информация в БД не найдена!", Toast.LENGTH_SHORT).show();
        }else{
            if (!stamp.isFlag()) {   //загрузка детальной информации из интернета
                downloadDetail();
            }
            applyDetail();  //применить детальную информацию на экран
        }
    }


    public void onClickRight(View view) {
        id++;
        stamp = viewModel.getStampById(id);
        if (stamp==null){
            Toast.makeText(this, "Информация в БД не найдена!", Toast.LENGTH_SHORT).show();
        }else{
                        if (!stamp.isFlag()) {   //загрузка детальной информации из интернета
                             downloadDetail();

                        }
           applyDetail();  //применить детальную информацию на экран
        }
    }
}