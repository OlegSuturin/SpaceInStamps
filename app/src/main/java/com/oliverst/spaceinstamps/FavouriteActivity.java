package com.oliverst.spaceinstamps;

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
import android.widget.Toast;

import com.oliverst.spaceinstamps.adapters.StampAdapter;
import com.oliverst.spaceinstamps.data.FavouriteStamp;
import com.oliverst.spaceinstamps.data.MainViewModel;
import com.oliverst.spaceinstamps.data.Stamp;

import java.util.ArrayList;
import java.util.List;

public class FavouriteActivity extends AppCompatActivity {
    private StampAdapter adapter;
    private RecyclerView recyclerViewTitle;
    private MainViewModel viewModel;

    private LiveData<List<FavouriteStamp>> stampsFavouriteLD;


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
                Intent intentToFavourite = new Intent(this  , FavouriteActivity.class);
                startActivity(intentToFavourite);
               // startActivityForResult(intentToFavourite, RESULT_FIRST_USER);
        }
        return super.onOptionsItemSelected(item);
    }
//-------------------------------------------------------------
@Override
protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    List<FavouriteStamp> stamps = stampsFavouriteLD.getValue();
    ArrayList<Stamp> favouriteStamps = new ArrayList<>();
    favouriteStamps.addAll(stamps);
    adapter.setStamps(favouriteStamps);
    adapter.notifyDataSetChanged();
    Toast.makeText(this, "Возврат", Toast.LENGTH_SHORT).show();
}


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle("Избранное");
        }

        recyclerViewTitle = findViewById(R.id.recyclerViewTitle);
        adapter = new StampAdapter();
        recyclerViewTitle.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTitle.setAdapter(adapter);
        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(MainViewModel.class);

        ArrayList<Stamp> stamps = new ArrayList<>();
          stampsFavouriteLD = viewModel.getFavouriteStampsLiveData();
          stampsFavouriteLD.observe(this, new Observer<List<FavouriteStamp>>() {
              @Override
              public void onChanged(List<FavouriteStamp> favouriteStamps) {
                  if(favouriteStamps!=null){
                           stamps.addAll(favouriteStamps);
                        adapter.setStamps(stamps);
                  }
              }
          });

        adapter.setOnStampClickListener(new StampAdapter.OnStampClickListener() {
            @Override
            public void onStampClick(int position) {
                // Toast.makeText(MainActivity.this, "" + position, Toast.LENGTH_SHORT).show();
             //   int positionTheme = spinnerThemeSelect.getSelectedItemPosition();
                Stamp stamp = adapter.getStamps().get(position);
                Intent intent = new Intent(FavouriteActivity.this, DetailStampActivity.class);
                intent.putExtra("id", stamp.getId());
                intent.putExtra("idStamp", stamp.getIdStamp());
                intent.putExtra("recordsNum", adapter.getItemCount());
                intent.putExtra("currentNum", position + 1);
                intent.putExtra("page", -1);
                intent.putExtra("positionTheme", -1);
                intent.putExtra("favouriteTag", true );
                startActivityForResult(intent, RESULT_FIRST_USER);
            }
        });




        }  //end of onCreate

}