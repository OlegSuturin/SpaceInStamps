package com.oliverst.spaceinstamps.data;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.oliverst.spaceinstamps.MainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainViewModel extends AndroidViewModel {
    private static StampDatabase database;
    private LiveData<List<Stamp>> stampsLiveData;    //объект LiveData для хранения всех записей
    private LiveData<List<ImageUrl>> imagesUrlLiveData;

    public MainViewModel(@NonNull Application application) {
        super(application);
        database = StampDatabase.getInstance(getApplication());
        stampsLiveData = database.stampDao().getAllStamps();
        imagesUrlLiveData = database.stampDao().getAllImagesUrl();
    }

    public LiveData<List<Stamp>> getStampsLiveData() {
        return stampsLiveData;
    }

    public LiveData<List<ImageUrl>> getImagesUrlLiveData() {
        return imagesUrlLiveData;
    }
    // МЕТОДЫ работы с БД - таблица images_url (таб. stamps - в отдельных потоках , для каждого создан класс Task
    public void insertImageUrl(ImageUrl imageUrl){
        new InsertUrlTask().execute(imageUrl);
    }

    private static class InsertUrlTask extends AsyncTask<ImageUrl, Void, Void>{
        @Override
        protected Void doInBackground(ImageUrl... imageUrls) {
            if(imageUrls !=null && imageUrls.length>0){
             database.stampDao().insertImageUrl(imageUrls[0]);
            }
            return null;
        }
    }

    public void deleteAllImageUrlTask() {
        new DeleteAllUrlTask().execute();
    }

    private static class DeleteAllUrlTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            database.stampDao().deleteAllImagesUrl();
            return null;
        }
    }

    public List<ImageUrl> getImagesUrlById(int id) {
        try {
            return new GetUrlByIdTask().execute(id).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class GetUrlByIdTask extends AsyncTask<Integer, Void, List<ImageUrl>> {       //первый параметр - принимает, последний - возвращает
        @Override
        protected List<ImageUrl> doInBackground(Integer... integers) {
            if (integers != null && integers.length > 0) {
                return database.stampDao().getImagesUrlById(integers[0]);   // возвращаем результат
            }
            return null;
        }
    }




    // МЕТОДЫ работы с БД - таблица stamps (таб. stamps - в отдельных потоках , для каждого создан класс Task
    public void insertStamp(Stamp stamp) {
        new InsertTask().execute(stamp);
    }

    private static class InsertTask extends AsyncTask<Stamp, Void, Void> {
        @Override
        protected Void doInBackground(Stamp... stamps) {
            if (stamps != null && stamps.length > 0) {
                database.stampDao().insertStamp(stamps[0]);
            }
            return null;
        }
    }

    public void updateStamp(Stamp stamp) {
        new UpdateTask().execute(stamp);
    }

    private static class UpdateTask extends AsyncTask<Stamp, Void, Void> {
        @Override
        protected Void doInBackground(Stamp... stamps) {
            if (stamps != null && stamps.length > 0) {
                database.stampDao().updateStamp(stamps[0]);
            }
            return null;
        }
    }

    public void deleteStamp(Stamp stamp) {
        new DeleteTask().execute(stamp);
    }

    private static class DeleteTask extends AsyncTask<Stamp, Void, Void> {
        @Override
        protected Void doInBackground(Stamp... stamps) {
            if (stamps != null && stamps.length > 0) {
                database.stampDao().deleteStamp(stamps[0]);
            }
            return null;
        }
    }

    public void deleteAllStamps() {
        new DeleteAllTask().execute();
    }

    private static class DeleteAllTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            database.stampDao().deleteAllStamps();
            return null;
        }
    }


    public Stamp getStampById(int id) {
        try {
            return new GetStampByIdTask().execute(id).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class GetStampByIdTask extends AsyncTask<Integer, Void, Stamp> {       //первый параметр - принимает, последний - возвращает
        @Override
        protected Stamp doInBackground(Integer... integers) {
            if (integers != null && integers.length > 0) {
                return database.stampDao().getStampById(integers[0]);   // возвращаем результат Stamp
            }
            return null;
        }
    }

}// end of class
