package com.oliverst.russianstamps.data;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainViewModel extends AndroidViewModel {
    private static StampDatabase database;
    private LiveData<List<Stamp>> stampsLiveData;    //объект LiveData для хранения всех записей
    private LiveData<List<ImageUrl>> imagesUrlLiveData;
    private LiveData<List<FavouriteStamp>> favouriteStampsLiveData;
    private LiveData<List<FavouriteImageURL>> favouriteImagesUrlLiveData;

    public MainViewModel(@NonNull Application application) {
        super(application);
        database = StampDatabase.getInstance(getApplication());
        stampsLiveData = database.stampDao().getAllStamps();
        imagesUrlLiveData = database.stampDao().getAllImagesUrl();
        favouriteStampsLiveData = database.stampDao().getAllFavouriteStamps();
    }

    public LiveData<List<Stamp>> getStampsLiveData() {
        return stampsLiveData;
    }

    public LiveData<List<ImageUrl>> getImagesUrlLiveData() {
        return imagesUrlLiveData;
    }

    public LiveData<List<FavouriteStamp>> getFavouriteStampsLiveData() {
        return favouriteStampsLiveData;
    }

    public LiveData<List<FavouriteImageURL>> getFavouriteImagesUrlLiveData() {
        return favouriteImagesUrlLiveData;
    }
    // МЕТОДЫ работы с БД - таблица favourite_stamp (таб. stamps - в отдельных потоках , для каждого создан класс Task

    public void insertFavouriteStamp(FavouriteStamp favouriteStamp) {
        new InsertFavouriteTask().execute(favouriteStamp);
    }

    private static class InsertFavouriteTask extends AsyncTask<FavouriteStamp, Void, Void> {
        @Override
        protected Void doInBackground(FavouriteStamp... favouriteStamps) {
            if (favouriteStamps != null && favouriteStamps.length > 0) {
                database.stampDao().insertFavouriteStamp(favouriteStamps[0]);
            }
            return null;
        }
    }

    public void deleteFavouriteStamp(FavouriteStamp favouriteStamp) {
        new DeleteFavouriteTask().execute(favouriteStamp);
    }

    private static class DeleteFavouriteTask extends AsyncTask<FavouriteStamp, Void, Void> {
        @Override
        protected Void doInBackground(FavouriteStamp... favouriteStamps) {
            if (favouriteStamps != null && favouriteStamps.length > 0) {
                database.stampDao().deleteFavouriteStamp(favouriteStamps[0]);
            }
            return null;
        }
    }

    public FavouriteStamp getFavouriteStampByIdStamp(int idStamp) {
        try {
            return new GetFavouriteByIdStampTask().execute(idStamp).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class GetFavouriteByIdStampTask extends AsyncTask<Integer, Void, FavouriteStamp> {       //первый параметр - принимает, последний - возвращает
        @Override
        protected FavouriteStamp doInBackground(Integer... integers) {
            if (integers != null && integers.length > 0) {
                return database.stampDao().getFavouriteStampByIdStamp(integers[0]);   // возвращаем результат Stamp
            }
            return null;
        }
    }

        public FavouriteStamp getFavouriteStampById(long id) {
            try {
                return new GetFavouriteByIdTask().execute(id).get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }


        private static class GetFavouriteByIdTask extends AsyncTask<Long, Void, FavouriteStamp> {
            @Override
            protected FavouriteStamp doInBackground(Long... longs) {
                if (longs != null && longs.length > 0) {
                    return database.stampDao().getFavouriteStampById(longs[0]);
                }
                return null;
            }       //первый параметр - принимает, последний - возвращает


        }

        //------------------------------------------------------------------------------------------------------------
        // МЕТОДЫ работы с БД - таблица images_url (таб. stamps - в отдельных потоках , для каждого создан класс Task
        public void insertImageUrl(ImageUrl imageUrl) {
            new InsertUrlTask().execute(imageUrl);
        }

        private static class InsertUrlTask extends AsyncTask<ImageUrl, Void, Void> {
            @Override
            protected Void doInBackground(ImageUrl... imageUrls) {
                if (imageUrls != null && imageUrls.length > 0) {
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

        public List<ImageUrl> getImagesUrlById(int idStamp) {
            try {
                return new GetUrlByIdTask().execute(idStamp).get();
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

        public int getItemCountFavourite() {
            try {
                return new countFavouriteTask().execute().get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 0;
        }

        private static class countFavouriteTask extends AsyncTask<Void, Void, Integer> {
            @Override
            protected Integer doInBackground(Void... voids) {
                return database.stampDao().getItemCountFavouriteStamps();
            }
        }

    // МЕТОДЫ работы с БД - таблица favourite_images_url (таб. stamps - в отдельных потоках , для каждого создан класс Task

    public void insertFavouriteImageUrl(FavouriteImageURL favouriteImageURL) {
        new InsertFavouriteUrlTask().execute(favouriteImageURL);
    }

    private static class InsertFavouriteUrlTask extends AsyncTask<FavouriteImageURL, Void, Void> {
        @Override
        protected Void doInBackground(FavouriteImageURL... favouriteImageURLS) {
            if (favouriteImageURLS != null && favouriteImageURLS.length > 0) {
                database.stampDao().insertFavouriteImageUrl(favouriteImageURLS[0]);
            }
            return null;
        }
    }

    public void deleteAllFavouriteImageUrlTask() {
        new DeleteAllFavouriteUrlTask().execute();
    }

    private static class DeleteAllFavouriteUrlTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            database.stampDao().deleteAllFavouriteImagesUrl();
            return null;
        }
    }

    public List<FavouriteImageURL> getFavouriteImagesUrlById(int idStamp) {
        try {
            return new GetFavouriteUrlByIdTask().execute(idStamp).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class GetFavouriteUrlByIdTask extends AsyncTask<Integer, Void, List<FavouriteImageURL>> {       //первый параметр - принимает, последний - возвращает
        @Override
        protected List<FavouriteImageURL> doInBackground(Integer... integers) {
            if (integers != null && integers.length > 0) {
                return database.stampDao().getFavouriteImagesUrlById(integers[0]);   // возвращаем результат
            }
            return null;
        }
    }


    public void deleteFavouriteImagesUrlById(int idStamp) {
            new deleteFavouriteUrlByIdTask().execute(idStamp);
    }

    private static class deleteFavouriteUrlByIdTask extends AsyncTask<Integer, Void, Void> {       //первый параметр - принимает, последний - возвращает
        @Override
        protected Void doInBackground(Integer... integers) {
            if (integers != null && integers.length > 0) {
                database.stampDao().deleteFavouriteImagesUrlById(integers[0]);   // возвращаем результат
            }
            return null;
        }
    }

    public void deleteAllFavouriteStamps() {
        new DeleteAllFavouriteTask().execute();
    }

    private static class DeleteAllFavouriteTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            database.stampDao().deleteALLFavouriteStamps();
            return null;
        }
    }


//---------------------------------------------------------------------------------------

        // МЕТОДЫ работы с БД - таблица stamps (таб. stamps - в отдельных потоках , для каждого создан класс Task
        public int getItemCountStamps() {
            try {
                return new countStampsTask().execute().get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 0;
        }

        private static class countStampsTask extends AsyncTask<Void, Void, Integer> {
            @Override
            protected Integer doInBackground(Void... voids) {
                return database.stampDao().getItemCountStamps();
            }
        }

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


        public Stamp getStampByIdStamp(int idStamp) {
            try {
                return new GetStampByIdStampTask().execute(idStamp).get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        private static class GetStampByIdStampTask extends AsyncTask<Integer, Void, Stamp> {       //первый параметр - принимает, последний - возвращает
            @Override
            protected Stamp doInBackground(Integer... integers) {
                if (integers != null && integers.length > 0) {
                    return database.stampDao().getStampByIdStamp(integers[0]);   // возвращаем результат Stamp
                }
                return null;
            }
        }

        public Stamp getStampById(long id) {
            try {
                return new GetStampByIdTask().execute(id).get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        private static class GetStampByIdTask extends AsyncTask<Long, Void, Stamp> {       //первый параметр - принимает, последний - возвращает
            @Override
            protected Stamp doInBackground(Long... longs) {
                if (longs != null && longs.length > 0) {
                    return database.stampDao().getStampById(longs[0]);   // возвращаем результат Stamp
                }
                return null;
            }
        }

    }
