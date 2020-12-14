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
import android.text.GetChars;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.oliverst.spaceinstamps.adapters.StampAdapter;
import com.oliverst.spaceinstamps.data.MainViewModel;
import com.oliverst.spaceinstamps.data.Stamp;
import com.oliverst.spaceinstamps.utils.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {
    public static final int SORT_BY_THEME = 0;
    public static final int SORT_BY_YEAR = 1;

    private static final int YEAR_START = 1961;
    private static final int YEAR_END = 1991;
    private Spinner spinnerYearSelect;

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
    private int recordsNumberG;             //кол-во записей найдено

    private LiveData<List<Stamp>> stampsFromLiveData;
//--------------------------------menu---------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int idMenu = item.getItemId();
        switch (idMenu) {
            case R.id.itemMain:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.itemFavourite:
                Intent intentToFavourite = new Intent(this  , FavouriteActivity.class);
                startActivity(intentToFavourite);
              //  startActivityForResult(intentToFavourite, RESULT_FIRST_USER);
        }
        return super.onOptionsItemSelected(item);
    }
//-----------------------------------------------------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && data.hasExtra("page")) {
            int page = data.getIntExtra("page", -1);
            Toast.makeText(this, "" + page, Toast.LENGTH_SHORT).show();
            pageG = page;
            List<Stamp> stamps = stampsFromLiveData.getValue();
            adapter.setStamps(stamps);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spinnerThemeSelect = findViewById(R.id.spinnerThemeSelect);
        recyclerViewTitle = findViewById(R.id.recyclerViewTitle);
        progressBarLoading = findViewById(R.id.progressBarLoading);
        spinnerYearSelect = findViewById(R.id.spinnerYearSelect);

        initSpinnerYearSelect(YEAR_START,YEAR_END);

        adapter = new StampAdapter();
        recyclerViewTitle.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTitle.setAdapter(adapter);

        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(MainViewModel.class);
        loaderManager = LoaderManager.getInstance(this);

        //слушатель выбора на спинере - тема
        AdapterView.OnItemSelectedListener onItemSelectedThemeListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                theme = (String) parent.getSelectedItem();
                pageG = 1;
                recordsNumberG = 0;
                if (!isLoading) {   //если процесс загрузки не идет
                    downLoadData(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        spinnerThemeSelect.setOnItemSelectedListener(onItemSelectedThemeListener);

        //слушатель выбора на спинере - год-------------------------------------------------
        AdapterView.OnItemSelectedListener onItemSelectedYearListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("!@#", "p:"+position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        spinnerYearSelect.setOnItemSelectedListener(onItemSelectedYearListener);

        //слушатель клика на элемент рецайклера
        adapter.setOnStampClickListener(new StampAdapter.OnStampClickListener() {
            @Override
            public void onStampClick(int position) {
                // Toast.makeText(MainActivity.this, "" + position, Toast.LENGTH_SHORT).show();
                int positionTheme = spinnerThemeSelect.getSelectedItemPosition();
                Stamp stamp = adapter.getStamps().get(position);
                Intent intent = new Intent(MainActivity.this, DetailStampActivity.class);
                intent.putExtra("id", stamp.getId());
                intent.putExtra("idStamp", stamp.getIdStamp());
                intent.putExtra("recordsNum", recordsNumberG);
                intent.putExtra("currentNum", position + 1);
                intent.putExtra("page", pageG);
                intent.putExtra("positionTheme", positionTheme);
                intent.putExtra("favouriteTag", false );
                startActivityForResult(intent, RESULT_FIRST_USER);

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
                    // Toast.makeText(MainActivity.this, "pageG: "+ pageG, Toast.LENGTH_SHORT).show();
                }
            }
        });
        stampsFromLiveData = viewModel.getStampsLiveData();
        stampsFromLiveData.observe(this, new Observer<List<Stamp>>() {
            @Override
            public void onChanged(List<Stamp> stamps) {
                adapter.setStamps(stamps);
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
        if (data == null) {
            Toast.makeText(this, "данные не загружены", Toast.LENGTH_SHORT).show();
        }
        if (pageG == 1) {
            adapter.clearStamps();
            viewModel.deleteAllStamps();
            viewModel.deleteAllImageUrlTask();
            recordsNumberG = NetworkUtils.parserRecordsNumber(data);
            Toast.makeText(MainActivity.this, "Всего найдено: " + recordsNumberG, Toast.LENGTH_SHORT).show();
        }
        ArrayList<Stamp> stamps = NetworkUtils.parserTitlesStamp(data);
        if (stamps != null && !stamps.isEmpty()) {

            for (Stamp stamp : stamps) {
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
//-------------------------------------------------------------------------

   public void initSpinnerYearSelect(int yearStart, int yearEnd){
        ArrayList<String> years = new ArrayList<>();
        for(int i = yearStart; i<=yearEnd; i++){
            years.add(Integer.toString(i));
        }
       ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, years);
      // adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYearSelect.setAdapter(adapter);
    }


}