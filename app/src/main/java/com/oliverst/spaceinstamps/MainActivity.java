package com.oliverst.spaceinstamps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.oliverst.spaceinstamps.adapters.StampAdapter;
import com.oliverst.spaceinstamps.data.Stamp;
import com.oliverst.spaceinstamps.utils.NetworkUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<Stamp> stamps;
    private StampAdapter adapter;
    private RecyclerView recyclerViewTitle;

    private Spinner spinnerThemeSelect;
    private String theme;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spinnerThemeSelect = findViewById(R.id.spinnerThemeSelect);
        recyclerViewTitle = findViewById(R.id.recyclerViewTitle);

        stamps = new ArrayList<>();
        adapter = new StampAdapter();
        recyclerViewTitle.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTitle.setAdapter(adapter);

        AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                theme = (String) parent.getSelectedItem();
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
            Log.i("!@#", "id:" +stamp.getIdStamp()+ " год:" + stamp.getYear() + " ITC:" + stamp.getCatalogNumberITC() + " SK:" + stamp.getCatalogNumberSK() + " Mich:" + stamp.getCatalogNumberMich()
                    + " Название выпуска:" + stamp.getName() + " Кол-во:" + stamp.getQuantity() + " Цена: " + stamp.getPrice() + " Url:" + stamp.getDetailUrl());
        }
        adapter.setStamps(stamps);

    }
}