package com.oliverst.spaceinstamps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.oliverst.spaceinstamps.adapters.StampAdapter;
import com.oliverst.spaceinstamps.data.MainViewModel;
import com.oliverst.spaceinstamps.data.Stamp;
import com.oliverst.spaceinstamps.utils.NetworkUtils;

import java.util.ArrayList;

public class DetailStamp extends AppCompatActivity {
    private int idStamp;
    private TextView textViewCountryInfo;
    private TextView textViewYearInfo;
    private TextView textViewNameInfo;
    private TextView textViewQuantityInfo;
    private TextView textViewDateReleaseInfo;
    private TextView textViewCatalogNumbersInfo;
    private TextView textViewPriceInfo;
    private TextView textViewSpecificationsInfo;
    private TextView textViewOverviewInfo;

    private MainViewModel viewModel;

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


        Intent intent = getIntent();                                    //! проверяем Интент и наличие параметров
        if (intent != null && intent.hasExtra("idStamp")) {
            idStamp = intent.getIntExtra("idStamp", -1);
            Toast.makeText(this, "" + idStamp, Toast.LENGTH_SHORT).show();
        } else {
            finish();               //  закрываем активность, если что то не так
        }
        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(MainViewModel.class);
        Stamp stamp = viewModel.getStampById(idStamp);

        //загрузка детальной информации
        String urlAsStringDetail = stamp.getDetailUrl();
                                //"http://www.philately.ru/cgi-bin/sql/search1.cgi?action=view_details&id=3863";
                                //stamp.getDetailUrl();
        String data = NetworkUtils.getDetailFromNetwork(urlAsStringDetail);
        // Log.i("!@#", line);
        if (data == null) {
            Toast.makeText(this, "данные не загружены", Toast.LENGTH_SHORT).show();
            finish();               //  закрываем активность, если что то не так
        }
        Stamp stampDetail = NetworkUtils.parserDetailStamp(data, Integer.toString(stamp.getYear()));
        stamp.setCountry(stampDetail.getCountry());
        stamp.setDateRelease(stampDetail.getDateRelease());
        stamp.setOverview(stampDetail.getOverview());
        stamp.setSpecifications(stampDetail.getSpecifications());
        //путь к картинке здесь
        stamp.setFlag(true);

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
    }
}