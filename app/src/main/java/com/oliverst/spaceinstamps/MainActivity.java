package com.oliverst.spaceinstamps;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.oliverst.spaceinstamps.data.Stamp;
import com.oliverst.spaceinstamps.utils.NetworkUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<Stamp> stamps = new ArrayList<>();
    private Spinner spinnerThemeSelect;
    private String theme;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerThemeSelect = findViewById(R.id.spinnerThemeSelect);

        AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                theme = (String) parent.getSelectedItem();
                //Toast.makeText(MainActivity.this, theme + " " + position, Toast.LENGTH_SHORT).show();
//                Log.i("!@#", NetworkUtils.buildURL(position, 1).toString());
//                String data = NetworkUtils.getStampsFromNetwork(position, 1);
//                NetworkUtils.parserRecordsNumber(data);
//                Toast.makeText(MainActivity.this, "Всего выпусков: " + NetworkUtils.getRecordsNumber(), Toast.LENGTH_SHORT).show();
//                ArrayList<String> stringsBuf = NetworkUtils.parserTitlesStamp(data);
                    downLoadData(1);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };

        spinnerThemeSelect.setOnItemSelectedListener(onItemSelectedListener);
    }//end of onCreate

    private void downLoadData(int page){
        int position = spinnerThemeSelect.getSelectedItemPosition();
        //Toast.makeText(MainActivity.this, theme + " " + position, Toast.LENGTH_SHORT).show();
        Log.i("!@#", NetworkUtils.buildURL(position, 1).toString());
        String data = NetworkUtils.getStampsFromNetwork(position, 1);
        int recordsNumber = NetworkUtils.parserRecordsNumber(data);
        Toast.makeText(MainActivity.this, "Всего найдено выпусков: " + recordsNumber, Toast.LENGTH_SHORT).show();
        stamps = NetworkUtils.parserTitlesStamp(data);

        for(Stamp stamp: stamps){
            Log.i("!@#", "id:" +stamp.getIdStamp()+ " год:" + stamp.getReleaseYear() + " ITC:" + stamp.getCatalogNumberITC() + " SK:" + stamp.getCatalogNumberSK() + " Mich:" + stamp.getCatalogNumberMich()
                    + " Название выпуска:" + stamp.getName() + " Кол-во:" + stamp.getQuantity() + " Цена: " + stamp.getPrice() + " Url:" + stamp.getDetailUrl());
        }


    }

}