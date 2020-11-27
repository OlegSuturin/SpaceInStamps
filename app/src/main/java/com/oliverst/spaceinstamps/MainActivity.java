package com.oliverst.spaceinstamps;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.oliverst.spaceinstamps.utils.NetworkUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
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
                Log.i("!@#", NetworkUtils.buildURL(position, 2).toString());
                String data = NetworkUtils.getStampsFromNetwork(position, 2);
                NetworkUtils.parserRecordsNumber(data);
                Toast.makeText(MainActivity.this, "Всего выпусков: " + NetworkUtils.getRecordsNumber(), Toast.LENGTH_SHORT).show();
                ArrayList<String> stringsBuf = NetworkUtils.parserTitlesStamp(data);




            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        spinnerThemeSelect.setOnItemSelectedListener(onItemSelectedListener);
    }//end of onCreate

}