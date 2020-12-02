package com.oliverst.spaceinstamps;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.oliverst.spaceinstamps.adapters.StampAdapter;
import com.oliverst.spaceinstamps.data.MainViewModel;
import com.oliverst.spaceinstamps.data.Stamp;
import com.oliverst.spaceinstamps.utils.NetworkUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {
    // private ArrayList<Stamp> stamps;
    private StampAdapter adapter;
    private RecyclerView recyclerViewTitle;

    private Spinner spinnerThemeSelect;
    private String theme;
    private static int pageG = 1;    // переменная, которая хранит номер страницы

    private String data;
    private static boolean isLoading = false;
    private static final int LOADER_ID = 1233; // - уникальный идентификатор загрузчика, определяем сами
    private LoaderManager loaderManager;      //  - менеджер загрузок
    private ProgressBar progressBarLoading;

    private MainViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spinnerThemeSelect = findViewById(R.id.spinnerThemeSelect);
        recyclerViewTitle = findViewById(R.id.recyclerViewTitle);
        progressBarLoading = findViewById(R.id.progressBarLoading);

        //stamps = new ArrayList<>();
        adapter = new StampAdapter();
        recyclerViewTitle.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTitle.setAdapter(adapter);

        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(MainViewModel.class);
        loaderManager = LoaderManager.getInstance(this);
        //слушатель выбора на спинере
        AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                theme = (String) parent.getSelectedItem();
                pageG = 1;
                if (!isLoading) {   //если процесс загрузки не идет
                    downLoadData(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        spinnerThemeSelect.setOnItemSelectedListener(onItemSelectedListener);

        //слушатель клика на элемент рецайклера
        adapter.setOnStampClickListener(new StampAdapter.OnStampClickListener() {
            @Override
            public void onStampClick(int position) {
                Toast.makeText(MainActivity.this, "" + position, Toast.LENGTH_SHORT).show();
                Stamp stamp = adapter.getStamps().get(position);
                Intent intent = new Intent(MainActivity.this, DetailStamp.class);
                intent.putExtra("idStamp", stamp.getIdStamp());
                startActivity(intent);
            }
        });
        //слушатель достижения конца списка
        adapter.setOnReachEndListener(new StampAdapter.OnReachEndListener() {
            @Override
            public void onReachEnd() {

               // Toast.makeText(MainActivity.this, "конец списка page =" + pageG, Toast.LENGTH_SHORT).show();
                if (!isLoading) {   //если процесс загрузки не идет
                    int position = spinnerThemeSelect.getSelectedItemPosition();
                    downLoadData(position);
                }
            }
        });
        LiveData<List<Stamp>> stampsFromLiveData = viewModel.getStampsLiveData();
        stampsFromLiveData.observe(this, new Observer<List<Stamp>>() {
            @Override
            public void onChanged(List<Stamp> stamps) {
//                if (pageG == 1) {
                   adapter.setStamps(stamps);    // если отсутствует интернет устанавливаем на адаптер данные из БД
//                    //ЛОГИКА ПРИЛОЖЕНИЯ
//                }
            }
        });

    }//end of onCreate

    private void downLoadData(int position) {
        URL url = NetworkUtils.buildURL(position, pageG);
        Bundle bundle = new Bundle();
        bundle.putString("url", url.toString());
        loaderManager.restartLoader(LOADER_ID, bundle, this);   //запускаем загрузчик
    }

    //------------------------------------------loader------------------------------
    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        NetworkUtils.DataLoader dataLoader = new NetworkUtils.DataLoader(this, args);
        //слушатель на начало загрузки
            dataLoader.setOnStartLoadingListener(new NetworkUtils.DataLoader.OnStartLoadingListener() {
                @Override
                public void onStartLoading() {
                    progressBarLoading.setVisibility(View.VISIBLE);
                    isLoading = true;         //загрузка началась
                }
            });


        return dataLoader;
    }

    @Override   //в этом методе получаем данные по окончании работы загрузчика
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        if (data == null){
            Toast.makeText(this, "данные не загружены", Toast.LENGTH_SHORT).show();
        }
        if (pageG == 1) {
            adapter.clearStamps();
            viewModel.deleteAllStamps();
            int recordsNumber = NetworkUtils.parserRecordsNumber(data);
            Toast.makeText(MainActivity.this, "Всего найдено: " + recordsNumber, Toast.LENGTH_SHORT).show();
        }
        ArrayList<Stamp> stamps = NetworkUtils.parserTitlesStamp(data);
        if (stamps != null && !stamps.isEmpty()) {

            for(Stamp stamp: stamps){
                                                    //загрузка детальной информации
//                String urlAsStringDetail = stamp.getDetailUrl();
//                String line =  NetworkUtils.getDetailFromNetwork(urlAsStringDetail);
//                Log.i("!@#", line);

                viewModel.insertStamp(stamp);
            }
            //adapter.addStamps(stamps);
            pageG++;
        }
        isLoading = false;  //загрузка закончилась
        progressBarLoading.setVisibility(View.INVISIBLE);
        loaderManager.destroyLoader(LOADER_ID);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }
}