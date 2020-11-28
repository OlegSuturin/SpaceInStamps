package com.oliverst.spaceinstamps.utils;


import android.os.AsyncTask;
import android.util.Log;

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

    private static final String baseUrl = "http://www.philately.ru";
    private static final String searshBaseUrl = "http://www.philately.ru/cgi-bin/sql/search2.cgi?&action=search&tag=%s&page=%s";
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


//МЕТОД ПАРСЕР СТРАНИЦЫ РЕЗУЛЬТАТА ПОИСКА

    public static int parserRecordsNumber(String data) {
        Pattern pattern = Pattern.compile("<br>Найдено <b>(.*?)</b> записей<br>");
        Matcher matcher = pattern.matcher(data);
        String result = "";
        while (matcher.find()) {
            result = matcher.group(1);
        }
        return Integer.parseInt(result);
    }

    public static ArrayList<String> parserTitlesStamp(String data) {
        //буферные строки
        ArrayList<String> stringsBuf = new ArrayList<>();
        Pattern pattern = Pattern.compile("<tr><td align=center><a href=\"(.*?)</td></tr>");
        Matcher matcher = pattern.matcher(data);
        while (matcher.find()) {
            String line = matcher.group(0);
            stringsBuf.add(line);
        }

        String idStamp = "";
        Pattern patternId = Pattern.compile("&id=(.*?)\">");
        Matcher matcherId;

        String releaseYear = "";
        String stringPatternRY;
        Pattern patternReleaseYear;
        Matcher matcherReleaseYear;

        String catalogNumberITC = "";
        String stringPatternITC = "</a></td><td>(.*?)</td><td>";
        Pattern patternNumberITC = Pattern.compile(stringPatternITC);
        Matcher matcherNumberITC;

        String catalogNumberSK = "";
        String stringPatternSK;
        Pattern patternNumberSK;
        Matcher matcherNumberSK;

        String catalogNumberMich = "";
        String stringPatternMich;
        Pattern patternNumberMich;
        Matcher matcherNumberMich;

        String name = "";
        String stringPatternName;
        Pattern patternName;
        Matcher matcherName;
        String bufName = "";

        String quantity = "";
        String stringPatternQuantity = "</a></td><td align=center>(.*?)</td>";
        Pattern patternQuantity = Pattern.compile(stringPatternQuantity);
        Matcher matcherQuantity;

        String price = "";
        String stringPatternPrice = "</td>\t<td>(.*?)</td></tr>";
        Pattern patternPrice = Pattern.compile(stringPatternPrice);
        Matcher matcherPrice;

        String detailUrl = "";
        String stringPatternUrl;
        Pattern patternUrl;
        Matcher matcherUrl;

        for (String buf : stringsBuf) {
            matcherId = patternId.matcher(buf);
            if (matcherId.find()) {
                idStamp = matcherId.group(1);
            }

            stringPatternRY = String.format("id=%s\">(.*?)</a></td><td>", idStamp);
            patternReleaseYear = Pattern.compile(stringPatternRY);
            matcherReleaseYear = patternReleaseYear.matcher(buf);
            if (matcherReleaseYear.find()) {
                releaseYear = matcherReleaseYear.group(1);
            }

            matcherNumberITC = patternNumberITC.matcher(buf);
            if (matcherNumberITC.find()) {
                catalogNumberITC = matcherNumberITC.group(1);
            }

            stringPatternSK = String.format("%s</td><td>(.*?)</td><td>", catalogNumberITC);
            patternNumberSK = Pattern.compile(stringPatternSK);
            matcherNumberSK = patternNumberSK.matcher(buf);
            if (matcherNumberSK.find()) {
                catalogNumberSK = matcherNumberSK.group(1);
            }

            stringPatternMich = String.format("%s</td><td>(.*?)</td><td align", catalogNumberSK);
            patternNumberMich = Pattern.compile(stringPatternMich);
            matcherNumberMich = patternNumberMich.matcher(buf);
            if (matcherNumberMich.find()) {
                catalogNumberMich = matcherNumberMich.group(1);
            }

            stringPatternName = String.format("id=%s\">(.*?)</td><td align=center>", idStamp);
            patternName = Pattern.compile(stringPatternName);
            matcherName = patternName.matcher(buf);
            if (matcherName.find()) {
                bufName = matcherName.group(1);
            }

            stringPatternName = String.format("id=%s\">(.*?)</a>", idStamp);
            patternName = Pattern.compile(stringPatternName);
            matcherName = patternName.matcher(bufName);
            if (matcherName.find()) {
                name = matcherName.group(1);
            }

            matcherQuantity = patternQuantity.matcher(buf);
            if (matcherQuantity.find()) {
                quantity = matcherQuantity.group(1);
            }

            matcherPrice = patternPrice.matcher(buf);
            if (matcherPrice.find()) {
                price = matcherPrice.group(1);
            }

            stringPatternUrl = String.format("/cgi-bin/(.*?)id=%s", idStamp);
            patternName = Pattern.compile(stringPatternUrl);
            matcherUrl = patternName.matcher(buf);
            if (matcherUrl.find()) {
                detailUrl = baseUrl + matcherUrl.group(0);
            }

            Log.i("!@#", "id:" + idStamp + " год:" + releaseYear + " ITC:" + catalogNumberITC + " SK:" + catalogNumberSK + " Mich:" + catalogNumberMich
                    + " Название выпуска:" + name + " Кол-во:" + quantity + " Цена: " + price + " Url:" + detailUrl);
        }
        return stringsBuf;
    }


    //МЕТОД, ФОРМИРУЕС СТРОКУ ЗАПРОСА URL
    public static URL buildURL(int themeNumber, int page) {
        String urlString;
        URL urlResult = null;

        switch (themeNumber) {
            case COSMOS:
                urlString = String.format(searshBaseUrl, THEME_COSMOS, page);
                break;
            case FLORA:
                urlString = String.format(searshBaseUrl, THEME_FLORA, page);
                break;
            case FAUNA:
                urlString = String.format(searshBaseUrl, THEME_FAUNA, page);
                break;
            case SPORT:
                urlString = String.format(searshBaseUrl, THEME_SPORT, page);
                break;
            case OLYMPIC:
                urlString = String.format(searshBaseUrl, THEME_OLYMPIC, page);
                break;
            case NEW_YEAR:
                urlString = String.format(searshBaseUrl, THEME_NEW_YEAR, page);
                break;
            case ART:
                urlString = String.format(searshBaseUrl, THEME_ART, page);
                break;
            case ARCHITECTURE:
                urlString = String.format(searshBaseUrl, THEME_ARCHITECTURE, page);
                break;
            case HISTORY:
                urlString = String.format(searshBaseUrl, THEME_HISTORY, page);
                break;
            case WAR:
                urlString = String.format(searshBaseUrl, THEME_WAR, page);
                break;
            case SYMBOLS:
                urlString = String.format(searshBaseUrl, THEME_SYMBOLS, page);
                break;
            case AUTO:
                urlString = String.format(searshBaseUrl, THEME_AUTO, page);
                break;
            case AVIATION:
                urlString = String.format(searshBaseUrl, THEME_AVIATION, page);
                break;
            case BOATS:
                urlString = String.format(searshBaseUrl, THEME_BOATS, page);
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
