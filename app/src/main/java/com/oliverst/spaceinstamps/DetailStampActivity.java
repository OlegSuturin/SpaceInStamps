package com.oliverst.spaceinstamps;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.oliverst.spaceinstamps.adapters.ImagesAdapter;
import com.oliverst.spaceinstamps.data.FavouriteImageURL;
import com.oliverst.spaceinstamps.data.FavouriteStamp;
import com.oliverst.spaceinstamps.data.ImageUrl;
import com.oliverst.spaceinstamps.data.MainViewModel;
import com.oliverst.spaceinstamps.data.Stamp;
import com.oliverst.spaceinstamps.utils.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DetailStampActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {
    private long id;
    private int idStamp;
    private int recordsNum;
    private int currentNum;
    private int page;
    private int positionTheme;
    private boolean favouriteTag;
    private int methodOfSort;
    private int year;
    private String keyword;

    private TextView textViewCountryInfo;
    private TextView textViewYearInfo;
    private TextView textViewNameInfo;
    private TextView textViewQuantityInfo;
    private TextView textViewDateReleaseInfo;
    private TextView textViewCatalogNumbersInfo;
    private TextView textViewPriceInfo;
    private TextView textViewSpecificationsInfo;
    private TextView textViewOverviewInfo;
    private TextView textViewNumberOfPics;

    private RecyclerView recyclerViewImagesInfo;
    private TextView textViewNumRecord;
    private ImageView imageViewLeft;
    private ImageView imageViewRight;
    private ImageView imageViewHeart;

    private ImagesAdapter adapter;
    private MainViewModel viewModel;
    private Stamp stamp;
    private FavouriteStamp favouriteStamp;
    private String detailUrl;
    private OnReachEndListener onReachEndListener;
    private static boolean isLoading = false;
    private static final int LOADER_ID = 1133; // - уникальный идентификатор загрузчика, определяем сами
    private LoaderManager loaderManager;      //  - менеджер загрузок
    private ProgressBar progressBarLoadingOnDetail;

    // private LiveData<List<Stamp>> stampsLD;

    //  private List<Stamp> stamps;

    private LiveData<List<FavouriteStamp>> favouriteStampsLD;
    //  private List<FavouriteStamp> favouriteStamps;

    @Override
    public void onBackPressed() {
        if (favouriteTag) {
            Intent intent = new Intent(DetailStampActivity.this, FavouriteActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(DetailStampActivity.this, MainActivity.class);
            intent.putExtra("page", page);
            setResult(RESULT_OK, intent);
        }


        super.onBackPressed();
    }

    //------------------------------------------------menu
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
                Intent intentToFavourite = new Intent(this, FavouriteActivity.class);
                startActivity(intentToFavourite);
        }
        return super.onOptionsItemSelected(item);
    }
//-------------------------------------------------------------

    //----------------------------------loader-------------------------------------
    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        NetworkUtils.DataLoader dataLoader = new NetworkUtils.DataLoader(this, args);
        //слушатель на начало загрузки
        dataLoader.setOnStartLoadingListener(new NetworkUtils.DataLoader.OnStartLoadingListener() {
            @Override
            public void onStartLoading() {
                progressBarLoadingOnDetail.setVisibility(View.VISIBLE);
                isLoading = true;         //загрузка началась
            }
        });
        return dataLoader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        if (data == null) {
            Toast.makeText(this, "данные не загружены", Toast.LENGTH_SHORT).show();
        }
        ArrayList<Stamp> stamps = NetworkUtils.parserTitlesStamp(data);
        if (stamps != null && !stamps.isEmpty()) {
            for (Stamp stamp : stamps) {
                viewModel.insertStamp(stamp);
            }
            page++;
        }
        isLoading = false;  //загрузка закончилась
        progressBarLoadingOnDetail.setVisibility(View.INVISIBLE);
        loaderManager.destroyLoader(LOADER_ID);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {
    }

    //-------------------------------------------------------------------------------------------
    private void downLoadData() {
        URL url = null;
        switch (methodOfSort) {
            case MainActivity.SORT_BY_THEME:
                url = NetworkUtils.buildURL(positionTheme, page);
                break;
            case MainActivity.SORT_BY_YEAR:
                url = NetworkUtils.buildURLByYear(year, page);
                break;
            case MainActivity.SORT_BY_KEYWORD:
                url = NetworkUtils.buildURLByKeyword(keyword, page);
                break;
        }

        Bundle bundle = new Bundle();
        if (url != null) {
            bundle.putString("url", url.toString());
            loaderManager.restartLoader(LOADER_ID, bundle, this);   //запускаем загрузчик
        }
    }


    //-----Слушатель на достижение конца списка
    public interface OnReachEndListener {
        void onReachEnd();
    }

    public void setOnReachEndListener(OnReachEndListener onReachEndListener) {
        this.onReachEndListener = onReachEndListener;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {    //сохранаяем значения перед поворотом экрана

        outState.putLong("id", stamp.getId());
        outState.putInt("idStamp", stamp.getIdStamp());
        outState.putInt("recordsNum", recordsNum);
        outState.putInt("currentNum", currentNum);
        outState.putInt("page", page);
        outState.putInt("positionTheme", positionTheme);
        outState.putBoolean("favouriteTag", favouriteTag);
        outState.putInt("methodOfSort", methodOfSort);
        outState.putInt("year", year);
        outState.putString("keyword", keyword);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_stamp);

        List<Stamp> stamps = new ArrayList<>();
        List<FavouriteStamp> favouriteStamps = new ArrayList<>();

        textViewCountryInfo = findViewById(R.id.textViewCountryInfo);
        textViewYearInfo = findViewById(R.id.textViewYearInfo);
        textViewNameInfo = findViewById(R.id.textViewNameInfo);
        textViewQuantityInfo = findViewById(R.id.textViewQuantityInfo);
        textViewDateReleaseInfo = findViewById(R.id.textViewDateReleaseInfo);
        textViewCatalogNumbersInfo = findViewById(R.id.textViewCatalogNumbersInfo);
        textViewPriceInfo = findViewById(R.id.textViewPriceInfo);
        textViewSpecificationsInfo = findViewById(R.id.textViewSpecificationsInfo);
        textViewOverviewInfo = findViewById(R.id.textViewOverviewInfo);
        imageViewLeft = findViewById(R.id.imageViewLeft);
        imageViewRight = findViewById(R.id.imageViewRight);
        imageViewHeart = findViewById((R.id.imageViewHeart));
        textViewNumberOfPics = findViewById(R.id.textViewNumberOfPics);

        adapter = new ImagesAdapter();
        recyclerViewImagesInfo = findViewById(R.id.recyclerViewImagesInfo);
        textViewNumRecord = findViewById(R.id.textViewNumRecord);
        progressBarLoadingOnDetail = findViewById(R.id.progressBarLoadingOnDetail);

        Intent intent = getIntent();                                    //! проверяем Интент и наличие параметров
        if (intent != null && intent.hasExtra("id") && intent.hasExtra("recordsNum") && intent.hasExtra("currentNum") && intent.hasExtra("page") && intent.hasExtra("positionTheme")) {
            id = intent.getLongExtra("id", -1);
            idStamp = intent.getIntExtra("idStamp", -1);
            recordsNum = intent.getIntExtra("recordsNum", -1);
            currentNum = intent.getIntExtra("currentNum", -1);
            page = intent.getIntExtra("page", -1);
            positionTheme = intent.getIntExtra("positionTheme", -1);
            favouriteTag = intent.getBooleanExtra("favouriteTag", false);
            methodOfSort = intent.getIntExtra("methodOfSort", -1);
            year = intent.getIntExtra("year", -1);
            keyword = intent.getStringExtra("keyword");

            Toast.makeText(this, "FT: " + favouriteTag, Toast.LENGTH_SHORT).show();
        } else {
            finish();               //  закрываем активность, если что то не так
        }
        //восстанавливаем значения после поворота экрана
        if (savedInstanceState != null) {
            id = savedInstanceState.getLong("id");
            idStamp = savedInstanceState.getInt("idStamp");
            recordsNum = savedInstanceState.getInt("recordsNum");
            currentNum = savedInstanceState.getInt("currentNum");
            page = savedInstanceState.getInt("page");
            positionTheme = savedInstanceState.getInt("positionTheme");
            favouriteTag = savedInstanceState.getBoolean("favouriteTag");
            methodOfSort = savedInstanceState.getInt("methodOfSort");
            year = savedInstanceState.getInt("year");
            keyword = savedInstanceState.getString("keyword");
        }

        loaderManager = LoaderManager.getInstance(this);
        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(MainViewModel.class);

        if (favouriteTag) {
            stamp = viewModel.getFavouriteStampByIdStamp(idStamp);
            id = stamp.getId();

            favouriteStampsLD = viewModel.getFavouriteStampsLiveData();

            favouriteStampsLD.observe(this, new Observer<List<FavouriteStamp>>() {
                @Override
                public void onChanged(List<FavouriteStamp> fStamps) {
                    if (fStamps != null) {
                        // stamps.clear();
                        // stamps.addAll(fStamps);
                        recordsNum = fStamps.size();
                    }
                }
            });
            //  List<FavouriteStamp> favouriteStampList = favouriteStampsLD.getValue();
            //  stamps.addAll(favouriteStampList);

        } else {
            stamp = viewModel.getStampByIdStamp(idStamp);
        }

        if (stamp == null) {
            Toast.makeText(this, "Информация в БД не найдена!", Toast.LENGTH_SHORT).show();
            finish();               //  закрываем активность, если что то не так
        }

        if (!stamp.isFlag()) {   //загрузка детальной информации из интернета
            downloadDetail();
        }

        applyDetail();  //применить детальную информацию на экран

        textViewNumRecord.setText("№ " + currentNum + " из " + recordsNum);

        adapter.setOnImageClickListener(new ImagesAdapter.OnImageClickListener() {
            @Override
            public void onImageClick(int position) {
                //Toast.makeText(DetailStampActivity.this, "" + position, Toast.LENGTH_SHORT).show();
                Intent intentScaled = new Intent(DetailStampActivity.this, ScaledImageActivity.class);
                String url = adapter.getImagesUrl().get(position).getUrl();
                intentScaled.putExtra("url", url);
                startActivity(intentScaled);
            }
        });

        setOnReachEndListener(new OnReachEndListener() {   //догрузка данных
            @Override
            public void onReachEnd() {
                Toast.makeText(DetailStampActivity.this, "Достижение конца списка " + page, Toast.LENGTH_SHORT).show();
                if (!isLoading) {   //если процесс загрузки не идет
                    downLoadData();
                }
            }
        });
        //свайп
        ItemTouchHelper itemTouch = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                switch (direction) {
                    case ItemTouchHelper.RIGHT:
                        onClickLeft(imageViewRight);
                        break;
                    case ItemTouchHelper.LEFT:
                        onClickRight(imageViewRight);
                        break;
                }
            }
        });
        itemTouch.attachToRecyclerView(recyclerViewImagesInfo);
    } //end of onCreate

    public void downloadDetail() {
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

        ArrayList<String> imagesUrlString = NetworkUtils.parseImagesUrl(data);

        for (int i = 0; i < imagesUrlString.size(); i++) {
            ImageUrl imageUrl = new ImageUrl(stamp.getIdStamp(), imagesUrlString.get(i));
            // Log.i("!@#", imageUrl.getUrl());
            viewModel.insertImageUrl(imageUrl);
        }

        stamp.setFlag(true);  // установили flag - информация загружена вся
        viewModel.updateStamp(stamp);
    }

    public void applyDetail() {

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

        List<ImageUrl> imagesUrl = new ArrayList<>();
        if (favouriteTag) {
            List<FavouriteImageURL> favouriteImagesUrl = viewModel.getFavouriteImagesUrlById(stamp.getIdStamp());
            imagesUrl.addAll(favouriteImagesUrl);
        } else {
            imagesUrl = viewModel.getImagesUrlById(stamp.getIdStamp());
        }
        int number = imagesUrl.size();
        if (number > 1) {
            textViewNumberOfPics.setTextColor(getResources().getColor(android.R.color.holo_red_light));
        }else {
            textViewNumberOfPics.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }

        textViewNumberOfPics.setText(" " + number + ":");
        //Log.i("!@#", imagesUrl.get(0).getUrl());

        recyclerViewImagesInfo.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewImagesInfo.setAdapter(adapter);
        adapter.setImagesUrl(imagesUrl);
        setColorHeart();
    }

    public void onClickLeft(View view) {
        if (currentNum > 1) {
            id--;
            currentNum--;
        } else {
            Toast.makeText(this, "Начало списка", Toast.LENGTH_SHORT).show();
        }
        if (favouriteTag) {
            //stamp = viewModel.getFavouriteStampById(id);
            List<FavouriteStamp> stamps = favouriteStampsLD.getValue();
            stamp = stamps.get(currentNum - 1);
        } else {
            stamp = viewModel.getStampById(id);
        }
        if (stamp == null) {
            Toast.makeText(this, "Информация в БД не найдена!", Toast.LENGTH_SHORT).show();
        } else {
            if (!stamp.isFlag()) {   //загрузка детальной информации из интернета
                downloadDetail();
            }
            applyDetail();  //применить детальную информацию на экран
            textViewNumRecord.setText("№ " + currentNum + " из " + recordsNum);

        }
    }


    public void onClickRight(View view) {
        if (onReachEndListener != null && currentNum == viewModel.getItemCountStamps() - 10 && recordsNum >= 100) {
            onReachEndListener.onReachEnd();
        }

        if (currentNum < recordsNum) {
            id++;
            currentNum++;
        } else {
            Toast.makeText(this, "Конец списка", Toast.LENGTH_SHORT).show();
        }
        if (favouriteTag) {
            // stamp = viewModel.getFavouriteStampById(id);
            List<FavouriteStamp> stamps = favouriteStampsLD.getValue();
            stamp = stamps.get(currentNum - 1);
            //  Toast.makeText(this, ""+stamps.size(), Toast.LENGTH_SHORT).show();

        } else {
            stamp = viewModel.getStampById(id);
        }
        if (stamp == null) {
            Toast.makeText(this, "Информация в БД не найдена!", Toast.LENGTH_SHORT).show();
        } else {
            if (!stamp.isFlag()) {   //загрузка детальной информации из интернета
                downloadDetail();
            }
            applyDetail();  //применить детальную информацию на экран
            textViewNumRecord.setText("№ " + currentNum + " из " + recordsNum);
        }
    }

    public void setColorHeart() {
        favouriteStamp = viewModel.getFavouriteStampByIdStamp(stamp.getIdStamp());   //получение марки  по ID марки в таблице избранного
        if (favouriteStamp == null) {                                 //устанавливаем сердце
            imageViewHeart.setImageResource(R.drawable.grayheart);
        } else {
            imageViewHeart.setImageResource(R.drawable.redheart);
        }
    }

    public void onClickChangeFavourite(View view) {
        if (favouriteStamp == null) {                 //проверяем, что в избранном нет такого
            viewModel.insertFavouriteStamp(new FavouriteStamp(stamp));     //сохраняем movie в таблицу favourite_movie (ПРЕОБРАЗОВАНИЕ ТИПОВ ЧЕРЕЗ КОНСТРУКТОР)

            List<ImageUrl> imagesUrl = viewModel.getImagesUrlById(stamp.getIdStamp());             //сохраняем соответств. url картинок в избранное
            for (int i = 0; i < imagesUrl.size(); i++) {
                FavouriteImageURL favouriteImageURL = new FavouriteImageURL(imagesUrl.get(i));
                // Log.i("!@#", imageUrl.getUrl());
                viewModel.insertFavouriteImageUrl(favouriteImageURL);
            }

            Toast.makeText(this, "Добавлено в избранное", Toast.LENGTH_SHORT).show();
        } else {   //Если существует в таблице, то удаляем его из избранного
            viewModel.deleteFavouriteStamp(favouriteStamp);
            //здесь нужно удалять так же все его ImageURL избранное !!!
            viewModel.deleteFavouriteImagesUrlById(favouriteStamp.getIdStamp());

            Toast.makeText(this, "Удалено из избранного", Toast.LENGTH_SHORT).show();
        }
        setColorHeart();
    }

}