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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.oliverst.spaceinstamps.adapters.StampAdapter;
import com.oliverst.spaceinstamps.data.MainViewModel;
import com.oliverst.spaceinstamps.data.Stamp;
import com.oliverst.spaceinstamps.utils.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    private Spinner spinnerRangeSelect;

    public static final int RANGE_1857_1960 = 1;
    public static final int RANGE_1861_1991 = 2;
    public static final int RANGE_1992_2020 = 3;


    private static int methodOfSort = 1;
    public static final int SORT_BY_THEME = 1;
    public static final int SORT_BY_YEAR = 2;
    public static final int SORT_BY_KEYWORD = 3;

    private TextView textViewSortByTheme;
    private TextView textViewSortByYear;
    private TextView textViewSortByKeyword;

    private EditText editTextSearchKeyword;
    private ImageView imageViewSearchKeyword;
    private static String keyword;

    private static final int YEAR_START = 1961;
    private static final int YEAR_END = 1991;
    private Spinner spinnerYearSelect;
    private static boolean flagNoInitYear = false;
    private int year;

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
    private static int recordsNumberG;             //кол-во записей найдено

    private LiveData<List<Stamp>> stampsFromLiveData;
    private static int  exitCount = 0;

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
            case R.id.itemFavourite:
                exitCount = 0;
                Intent intentToFavourite = new Intent(this, FavouriteActivity.class);
                //startActivity(intentToFavourite);
                startActivityForResult(intentToFavourite, RESULT_FIRST_USER);
                break;
            case R.id.itemExit:
                System.exit (0);
                break;
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
    public void onBackPressed() {
        if(exitCount < 1) {
            Toast.makeText(this, "Для выхода нажмите еще раз", Toast.LENGTH_SHORT).show();
            exitCount++;
            return;
        }
        System.exit (0);
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewSortByTheme = findViewById(R.id.textViewSortByTheme);
        textViewSortByYear = findViewById(R.id.textViewSortByYear);
        textViewSortByKeyword = findViewById(R.id.textViewSortByKeyword);
        editTextSearchKeyword = findViewById(R.id.editTextSearchKeyword);
        imageViewSearchKeyword = findViewById(R.id.imageViewSearchKeyword);

        spinnerRangeSelect = findViewById(R.id.spinnerRangeSelect);

        spinnerThemeSelect = findViewById(R.id.spinnerThemeSelect);
        recyclerViewTitle = findViewById(R.id.recyclerViewTitle);
        progressBarLoading = findViewById(R.id.progressBarLoading);
        spinnerYearSelect = findViewById(R.id.spinnerYearSelect);

        onClickSortByTheme(textViewSortByTheme);    //сортировка по умолчанию - По теме


        adapter = new StampAdapter();
        recyclerViewTitle.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTitle.setAdapter(adapter);

        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(MainViewModel.class);
        loaderManager = LoaderManager.getInstance(this);

        //слушатель выбора на спинере - ранг
        AdapterView.OnItemSelectedListener onItemSelectedRangeListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
        spinnerRangeSelect.setOnItemSelectedListener(onItemSelectedRangeListener);

        //слушатель выбора на спинере - тема
        AdapterView.OnItemSelectedListener onItemSelectedThemeListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                methodOfSort = SORT_BY_THEME;
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
                if (flagNoInitYear) {
                    methodOfSort = SORT_BY_YEAR;
                    year = Integer.parseInt((String) parent.getSelectedItem());
                    pageG = 1;
                    recordsNumberG = 0;
                    // Log.i("!@#", "y:" + year);
                    if (!isLoading) {   //если процесс загрузки не идет
                        downLoadData(year);
                    }
                }
                flagNoInitYear = true;
                exitCount = 0;
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
                exitCount = 0;
                // Toast.makeText(MainActivity.this, "" + position, Toast.LENGTH_SHORT).show();
                int positionTheme = spinnerThemeSelect.getSelectedItemPosition();
                Stamp stamp = adapter.getStamps().get(position);
                if(stamp !=null){
                    Intent intent = new Intent(MainActivity.this, DetailStampActivity.class);
                    intent.putExtra("id", stamp.getId());
                    intent.putExtra("idStamp", stamp.getIdStamp());
                    intent.putExtra("recordsNum", recordsNumberG);
                    intent.putExtra("currentNum", position + 1);
                    intent.putExtra("page", pageG);
                    intent.putExtra("positionTheme", positionTheme);
                    intent.putExtra("favouriteTag", false);
                    intent.putExtra("methodOfSort", methodOfSort);
                    intent.putExtra("year", year);
                    intent.putExtra("keyword", keyword);
                    startActivityForResult(intent, RESULT_FIRST_USER);
                }
          }
        });
        //слушатель достижения конца списка
        adapter.setOnReachEndListener(new StampAdapter.OnReachEndListener() {
            @Override
            public void onReachEnd() {
                exitCount = 0;
                // Toast.makeText(MainActivity.this, "конец списка page =" + pageG, Toast.LENGTH_SHORT).show();
                if (!isLoading) {   //если процесс загрузки не идет
                    //  methodOfSort = SORT_BY_THEME;
                    switch (methodOfSort) {
                        case SORT_BY_THEME:
                            int position = spinnerThemeSelect.getSelectedItemPosition();
                            downLoadData(position);
                            break;
                        case SORT_BY_YEAR:
                            year = Integer.parseInt((String) spinnerYearSelect.getSelectedItem());
                            downLoadData(year);
                            break;
                        case SORT_BY_KEYWORD:
                            keyword = editTextSearchKeyword.getText().toString();
                            downLoadData(-1);
                            break;
                    }
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
        // methodOfSort = SORT_BY_THEME;

    }//end of onCreate

    private void downLoadData(int position) {
        Bundle bundle = new Bundle();
        URL url = null;
        switch (methodOfSort) {
            case SORT_BY_THEME:
                url = NetworkUtils.buildURL(position, pageG);
                break;
            case SORT_BY_YEAR:
                url = NetworkUtils.buildURLByYear(position, pageG);
                break;
            case SORT_BY_KEYWORD:
                url = NetworkUtils.buildURLByKeyword(keyword, pageG);
                // Toast.makeText(this, url.toString(), Toast.LENGTH_SHORT).show();
                break;
        }//end of case

        if (url != null) {
            bundle.putString("url", url.toString());
            loaderManager.restartLoader(LOADER_ID, bundle, this);   //запускаем загрузчик
        }
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
            if (recordsNumberG < 0) {   //единичный результат
                Toast.makeText(MainActivity.this, "Всего найдено: один", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Всего найдено: " + recordsNumberG, Toast.LENGTH_SHORT).show();
            }
        }
        ArrayList<Stamp> stamps = new ArrayList<>();
        if (recordsNumberG < 0) {   //единичный результат
            //код обработки единичной формы
            Stamp stamp;
            int idStamp = recordsNumberG * (-1);
            stamp = NetworkUtils.parserTitlesSingleStamp(data, idStamp);
                stamps.add(stamp);
            recordsNumberG = 1;
        } else {
            stamps = NetworkUtils.parserTitlesStamp(data);
        }

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

    public void initSpinnerYearSelect(int yearStart, int yearEnd) {
        exitCount = 0;
        ArrayList<String> years = new ArrayList<>();
        for (int i = yearStart; i <= yearEnd; i++) {
            years.add(Integer.toString(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, years);
        // adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYearSelect.setAdapter(adapter);
    }

    public void initSpinnerThemeSelect() {
        exitCount = 0;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.themes_array_string, android.R.layout.simple_spinner_dropdown_item);
        spinnerThemeSelect.setAdapter(adapter);
    }


    public void onClickSortByTheme(View view) {
        exitCount = 0;
        textViewSortByTheme.setTextColor(getResources().getColor(R.color.black));
        textViewSortByTheme.setBackgroundColor(getResources().getColor(R.color.main_blue_light2));
        textViewSortByYear.setTextColor(getResources().getColor(R.color.white));
        textViewSortByYear.setBackgroundColor(getResources().getColor(R.color.black));
        textViewSortByKeyword.setTextColor(getResources().getColor(R.color.white));
        textViewSortByKeyword.setBackgroundColor(getResources().getColor(R.color.black));

        spinnerThemeSelect.setVisibility(View.VISIBLE);
        spinnerYearSelect.setVisibility(View.INVISIBLE);
        editTextSearchKeyword.setVisibility(View.INVISIBLE);
        imageViewSearchKeyword.setVisibility(View.INVISIBLE);

        initSpinnerThemeSelect();
    }

    public void onClickSortByYear(View view) {
        exitCount = 0;
        textViewSortByYear.setTextColor(getResources().getColor(R.color.black));
        textViewSortByYear.setBackgroundColor(getResources().getColor(R.color.main_blue_light2));
        textViewSortByTheme.setTextColor(getResources().getColor(R.color.white));
        textViewSortByTheme.setBackgroundColor(getResources().getColor(R.color.black));
        textViewSortByKeyword.setTextColor(getResources().getColor(R.color.white));
        textViewSortByKeyword.setBackgroundColor(getResources().getColor(R.color.black));

        spinnerThemeSelect.setVisibility(View.INVISIBLE);
        spinnerYearSelect.setVisibility(View.VISIBLE);
        editTextSearchKeyword.setVisibility(View.INVISIBLE);
        imageViewSearchKeyword.setVisibility(View.INVISIBLE);

        initSpinnerYearSelect(YEAR_START, YEAR_END);
    }

    public void onClickSortByKeyword(View view) {
        exitCount = 0;
        editTextSearchKeyword.setText("");
        keyword = "";
        adapter.clearStamps();
        viewModel.deleteAllStamps();
        viewModel.deleteAllImageUrlTask();


        textViewSortByKeyword.setTextColor(getResources().getColor(R.color.black));
        textViewSortByKeyword.setBackgroundColor(getResources().getColor(R.color.main_blue_light2));
        textViewSortByYear.setTextColor(getResources().getColor(R.color.white));
        textViewSortByYear.setBackgroundColor(getResources().getColor(R.color.black));
        textViewSortByTheme.setTextColor(getResources().getColor(R.color.white));
        textViewSortByTheme.setBackgroundColor(getResources().getColor(R.color.black));

        spinnerThemeSelect.setVisibility(View.INVISIBLE);
        spinnerYearSelect.setVisibility(View.INVISIBLE);
        editTextSearchKeyword.setVisibility(View.VISIBLE);
        imageViewSearchKeyword.setVisibility(View.VISIBLE);

    }

    public void onClickSearchKeyword(View view) {
        exitCount = 0;
        keyword = editTextSearchKeyword.getText().toString();
        if (!keyword.isEmpty()) {
            methodOfSort = SORT_BY_KEYWORD;
            pageG = 1;
            recordsNumberG = 0;
            if (!isLoading) {   //если процесс загрузки не идет
                downLoadData(-1);
            }
        }
    }

}