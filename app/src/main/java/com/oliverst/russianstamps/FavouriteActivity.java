package com.oliverst.russianstamps;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.oliverst.russianstamps.adapters.StampAdapter;
import com.oliverst.russianstamps.data.FavouriteStamp;
import com.oliverst.russianstamps.data.MainViewModel;
import com.oliverst.russianstamps.data.Stamp;

import java.util.ArrayList;
import java.util.List;

public class FavouriteActivity extends AppCompatActivity {
    private StampAdapter adapter;
    private RecyclerView recyclerViewTitle;
    private MainViewModel viewModel;
    private ArrayList<Stamp> stamps = new ArrayList<>();
    private LiveData<List<FavouriteStamp>> stampsFavouriteLD;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        setResult(RESULT_OK, intent);
        finish();
        super.onBackPressed();
    }

    //------------------------------------------------menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.favourite_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int idMenu = item.getItemId();
        switch (idMenu) {
            case R.id.itemMain:
                Intent intent = new Intent(this, MainActivity.class);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.itemClear:
                viewModel.deleteAllFavouriteStamps();
                viewModel.deleteAllFavouriteImageUrlTask();
                adapter.clearStamps();
        }
        return super.onOptionsItemSelected(item);
    }

    //-------------------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.title_favourite);
        }

        recyclerViewTitle = findViewById(R.id.recyclerViewTitle);
        adapter = new StampAdapter();
        recyclerViewTitle.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTitle.setAdapter(adapter);
        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(MainViewModel.class);

        stampsFavouriteLD = viewModel.getFavouriteStampsLiveData();
        stampsFavouriteLD.observe(this, new Observer<List<FavouriteStamp>>() {
            @Override
            public void onChanged(List<FavouriteStamp> favouriteStamps) {
                if (favouriteStamps != null) {
                    stamps.addAll(favouriteStamps);
                    adapter.setStamps(stamps);
                }
            }
        });

        adapter.setOnStampClickListener(new StampAdapter.OnStampClickListener() {
            @Override
            public void onStampClick(int position) {
                Stamp stamp = adapter.getStamps().get(position);
                Intent intent = new Intent(FavouriteActivity.this, DetailStampActivity.class);
                intent.putExtra("id", stamp.getId());
                intent.putExtra("idStamp", stamp.getIdStamp());
                intent.putExtra("recordsNum", adapter.getItemCount());
                intent.putExtra("currentNum", position + 1);
                intent.putExtra("page", -1);
                intent.putExtra("positionTheme", -1);
                intent.putExtra("favouriteTag", true);
                startActivityForResult(intent, RESULT_FIRST_USER);
            }
        });
    }  //end of onCreate

}