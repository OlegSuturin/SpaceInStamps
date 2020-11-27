package com.oliverst.spaceinstamps.utils;


import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.oliverst.spaceinstamps.MainActivity;
import com.oliverst.spaceinstamps.data.Stamp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NetworkUtils {

    private static String baseUrl = "http://www.philately.ru/cgi-bin/sql/search2.cgi?&action=search&tag=%s&page=%s";
                                //  http://www.philately.ru/cgi-bin/sql/search2.cgi?action=search&category2=&year=&number=&tag=%%C0%E2%E8%E0&cat_name=&page=2&lang=&keyword=

    public static final int COSMOS = 0;         //Космос
    public static final int FLORA = 1;          //Флора
    public static final int FAUNA = 2;          //Фауна
    public static final int SPORT = 3;          //Спорт
    public static final int OLYMPIC = 4;        //Олимпиада
    public static final int NEW_YEAR = 5;       //Новый год
    public static final int ART = 6;            //Искусство
    public static final int ARCHITECTURE = 7;   //Архитектура
    public static final int HISTORY = 8;        //История
    public static final int WAR = 9;            //Война
    public static final int SYMBOLS = 10;        //Символы, ордена
    public static final int AUTO = 11;           //Автомобили
    public static final int AVIATION = 12;       //Авиация
    public static final int BOATS = 13;           //Корабли, флот

    public static final String THEME_COSMOS = "%CA%EE%F1%EC%EE%F1";
    public static final String THEME_FLORA = "%D4%EB%EE%F0%E0";
    public static final String THEME_FAUNA = "%F4%E0%F3%ED%E0";
    public static final String THEME_SPORT = "%D1%EF%EE%F0%F2";
    public static final String THEME_OLYMPIC = "%CE%EB%E8%EC%EF%E8%E0%E4%E0";
    public static final String THEME_NEW_YEAR = "%CD%EE%E2%FB%E9%20%E3%EE%E4";
    public static final String THEME_ART = "%C8%F1%EA%F3%F1%F1%F2%E2%EE";
    public static final String THEME_ARCHITECTURE = "%C0%F0%F5%E8%F2%E5%EA%F2%F3%F0%E0";
    public static final String THEME_HISTORY = "%C8%F1%F2%EE%F0%E8%FF";
    public static final String THEME_WAR = "%C2%EE%E9%ED%E0";
    public static final String THEME_SYMBOLS = "%D1%E8%EC%E2%EE%EB%E8%EA%E0";
    public static final String THEME_AUTO = "%C0%E2%F2%EE";
    public static final String THEME_AVIATION = "%C0%E2%E8%E0";
    public static final String THEME_BOATS = "%CA%EE%F0%E0%E1%EB%E8";

    private static int recordsNumber;

    public static int getRecordsNumber() {
        return recordsNumber;
    }


//МЕТОД ПАРСЕР СТРАНИЦЫ РЕЗУЛЬТАТА ПОИСКА

    public static void parserRecordsNumber(String data) {
        Pattern pattern = Pattern.compile("<br>Найдено <b>(.*?)</b> записей<br>");
        Matcher matcher = pattern.matcher(data);
        String result = "";
        while (matcher.find()) {
            result = matcher.group(1);
        }
        recordsNumber = Integer.parseInt(result);
    }

    public static ArrayList<String> parserTitlesStamp(String data) {
        ArrayList<String> stringsBuf = new ArrayList<>();

        Pattern pattern = Pattern.compile("<tr><td align=center><a href=\"(.*?)</td></tr>");
        Matcher matcher = pattern.matcher(data);
        while (matcher.find()) {
            String line = matcher.group(1);
            // Log.i("!@#", line);
            stringsBuf.add(line);
        }

        Pattern patternId = Pattern.compile("&id=(.*?)\">");
        Matcher matcherId;
        String id;

        for (String buf : stringsBuf) {
            matcherId = patternId.matcher(buf);
            if (matcherId.find()) {
                id = matcherId.group(1);
                Log.i("!@#", "" + id);
            }

        }
        return stringsBuf;
    }


    //МЕТОД, ФОРМИРУЕС СТРОКУ ЗАПРОСА URL
    public static URL buildURL(int themeNumber, int page) {
        String urlString;
        URL urlResult = null;

        switch (themeNumber) {
            case COSMOS:
                urlString = String.format(baseUrl, THEME_COSMOS, page);
                break;
            case FLORA:
                urlString = String.format(baseUrl, THEME_FLORA, page);
                break;
            case FAUNA:
                urlString = String.format(baseUrl, THEME_FAUNA, page);
                break;
            case SPORT:
                urlString = String.format(baseUrl, THEME_SPORT, page);
                break;
            case OLYMPIC:
                urlString = String.format(baseUrl, THEME_OLYMPIC, page);
                break;
            case NEW_YEAR:
                urlString = String.format(baseUrl, THEME_NEW_YEAR, page);
                break;
            case ART:
                urlString = String.format(baseUrl, THEME_ART, page);
                break;
            case ARCHITECTURE:
                urlString = String.format(baseUrl, THEME_ARCHITECTURE, page);
                break;
            case HISTORY:
                urlString = String.format(baseUrl, THEME_HISTORY, page);
                break;
            case WAR:
                urlString = String.format(baseUrl, THEME_WAR, page);
                break;
            case SYMBOLS:
                urlString = String.format(baseUrl, THEME_SYMBOLS, page);
                break;
            case AUTO:
                urlString = String.format(baseUrl, THEME_AUTO, page);
                break;
            case AVIATION:
                urlString = String.format(baseUrl, THEME_AVIATION, page);
                break;
            case BOATS:
                urlString = String.format(baseUrl, THEME_BOATS, page);
                break;

            default:
                urlString = "";
        }
        //Log.i("!@#", urlString);
        try {
            urlResult = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return urlResult;
    }

    //-------------------------------------------------------------------------------------------------
    public static String getStampsFromNetwork(int themeNumber, int page) {
        String result = null;
        URL url = buildURL(themeNumber, page);
        //здесь запускаем загрузку в другом программном потоке
        DataLoadTask task = new DataLoadTask();
        try {
            result = task.execute(url).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static class DataLoadTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {

            StringBuilder resultDownload = new StringBuilder();
            if (urls == null && urls.length == 0) {
                return null;
            } else {
                HttpURLConnection urlConnection = null;
                try {
                    urlConnection = (HttpURLConnection) urls[0].openConnection();
                    InputStream in = urlConnection.getInputStream();

                    InputStreamReader reader = new InputStreamReader(in, Charset.forName("windows-1251"));

                    BufferedReader bufferedReader = new BufferedReader(reader);
                    String line = bufferedReader.readLine();
                    while (line != null) {
                        resultDownload.append(line);
                        line = bufferedReader.readLine();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return resultDownload.toString();
        }
    }


}
