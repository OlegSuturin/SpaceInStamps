package com.oliverst.russianstamps.utils;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.oliverst.russianstamps.data.Stamp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NetworkUtils {

    private static final String baseUrl = "http://www.philately.ru";
    private static final String searchBaseUrl = "http://www.philately.ru/cgi-bin/sql/search%s.cgi?&action=search&tag=%s&page=%s";
    private static final String searchBaseUrlByYear = "http://www.philately.ru/cgi-bin/sql/search%s.cgi?action=search&year=%s&page=%s";
    private static final String searchBaseUrlByKeyword = "http://www.philately.ru/cgi-bin/sql/search%s.cgi?action=search&cat_name=catalogue&keyword=%s&page=%s";
    private static final String detailBaseUrl = "http://www.philately.ru/cgi-bin/sql/search%s.cgi?action=view_details&id=%s";


//МЕТОДЫ ПАРСЕРЫ СТРАНИЦЫ РЕЗУЛЬТАТА ПОИСКА

    public static int parserRecordsNumber(String data) {
        Pattern pattern = Pattern.compile("<br>Найдено <b>(.*?)</b> записей<br>");
        Matcher matcher = pattern.matcher(data);
        String result = "";
        while (matcher.find()) {
            result = matcher.group(1);
        }
        if (!result.isEmpty()) {
            return Integer.parseInt(result);
        } else {      //единичный результат
            Pattern pattern1 = Pattern.compile("name=item value=\"(.*?)\"><input type=hidden");
            Matcher matcher1 = pattern1.matcher(data);
            while (matcher1.find()) {
                result = matcher1.group(1);
            }
            if (!result.isEmpty()) {
                return Integer.parseInt(result) * (-1);
            }
            return 0;
        }
    }

    public static ArrayList<Stamp> parserTitlesStamp(String data) {
        //буферные строки
        ArrayList<Stamp> stamps = new ArrayList<>();

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

        String year = "";
        String stringPatternY;
        Pattern patternYear;
        Matcher matcherYear;

        String name = "";
        String stringPatternName;
        Pattern patternName;
        Matcher matcherName;
        String bufName = "";

        String quantity = "";
        String stringPatternQuantity = "</a></td><td align=center>(.*?)</td>";
        Pattern patternQuantity = Pattern.compile(stringPatternQuantity);
        Matcher matcherQuantity;

        String detailUrl = "";
        String stringPatternUrl;
        Pattern patternUrl;
        Matcher matcherUrl;

        for (String buf : stringsBuf) {
            matcherId = patternId.matcher(buf);
            if (matcherId.find()) {
                idStamp = matcherId.group(1);
            }

            stringPatternY = String.format("id=%s\">(.*?)</a></td><td>", idStamp);
            patternYear = Pattern.compile(stringPatternY);
            matcherYear = patternYear.matcher(buf);
            if (matcherYear.find()) {
                year = matcherYear.group(1);
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

            stringPatternUrl = String.format("/cgi-bin/(.*?)id=%s", idStamp);
            patternName = Pattern.compile(stringPatternUrl);
            matcherUrl = patternName.matcher(buf);
            if (matcherUrl.find()) {
                detailUrl = baseUrl + matcherUrl.group(0);
            }

            if (!idStamp.isEmpty()) {
                Stamp stamp = new Stamp(Integer.parseInt(idStamp), Integer.parseInt(year), name, quantity, detailUrl);

                stamps.add(stamp);
            }

        }
        return stamps;
    }

    public static Stamp parserTitlesSingleStamp(String data, int idStamp, String range) {

        Stamp stamp = null;
        String detailUrl = String.format(detailBaseUrl, range, idStamp);

        String year = "";
        String quantity = "";
        String name = "";

       // Log.i("!@#", "single");

        String buf = "";
        String stringPatternBuf = "Страна:</FONT>(.*?)</tr></table><br></center></TD>";

        Pattern patternBuf = Pattern.compile(stringPatternBuf);
        Matcher matcherBuf = patternBuf.matcher(data);
        if (matcherBuf.find()) {
            buf = matcherBuf.group(0);
        }
       // Log.i("!@#", buf);

        if (buf != null) {
            String stringPatternY = "Год выпуска:</FONT>&nbsp;&nbsp;<Font face=\"Verdana, Arial, Helvetica\" Size=2 Color=#003399>(.*?)&nbsp;&nbsp;<Font";
            Pattern patternYear;
            Matcher matcherYear;
            patternYear = Pattern.compile(stringPatternY);
            matcherYear = patternYear.matcher(buf);
            if (matcherYear.find()) {
                year = matcherYear.group(1);
            }
           // Log.i("!@#", year);

            String stringPatternQuantity = "Кол. марок:</FONT>&nbsp;&nbsp;<Font face=\"Verdana, Arial, Helvetica\" Size=2 Color=#003399>(.*?)</TD>";
            Pattern patternQuantity = Pattern.compile(stringPatternQuantity);
            Matcher matcherQuantity;
            matcherQuantity = patternQuantity.matcher(buf);
            if (matcherQuantity.find()) {
                quantity = matcherQuantity.group(1);
            }
           // Log.i("!@#", quantity);


            String stringPatternName = "Название выпуска:</FONT>&nbsp;&nbsp;<Font face=\"Verdana, Arial, Helvetica\" Size=2 Color=#003399>(.*?)</TD></TR><TR><TD";
            Pattern patternName = Pattern.compile(stringPatternName);
            Matcher matcherName = patternName.matcher(buf);
            if (matcherName.find()) {
                name = matcherName.group(1);
            }
           // Log.i("!@#", name);

            int y = 0;
            if (!year.isEmpty()) {
                y = Integer.parseInt(year);
            }

            stamp = new Stamp(idStamp, y, name, quantity, detailUrl);
        }

        return stamp;
    }

    public static Stamp parserDetailStamp(String data, String year, String range) {
        Stamp detailStamp = null;

        String buf = "";
        //String stringPatternBuf = "Страна:</FONT>(.*?)</td></tr></table></td></tr></table><br></center></TD>";
        String stringPatternBuf = "Страна:</FONT>(.*?)</tr></table></td></tr></table><br></center></TD>";
        //      </a> </td><td></td> </tr></table></td></tr></table><br></center></TD>

        Pattern patternBuf = Pattern.compile(stringPatternBuf);
        Matcher matcherBuf = patternBuf.matcher(data);
        if (matcherBuf.find()) {
            buf = matcherBuf.group(0);
        }
       // Log.i("!@#", buf);

        String country = "";
        String stringPatternCountry = "Color=#003399>(.*?)</TD>";
        Pattern patternCountry = Pattern.compile(stringPatternCountry);
        Matcher matcherCountry = patternCountry.matcher(buf);
        if (matcherCountry.find()) {
            country = matcherCountry.group(1);
        }

        String dateRelease = "";
        String stringPatternRelease = String.format("%s&nbsp;&nbsp;<Font face=\"Verdana, Arial, Helvetica\" Size=2 Color=#003399>(.*?)</TD>", year);
        Pattern patternRelease = Pattern.compile(stringPatternRelease);
        Matcher matcherRelease = patternRelease.matcher(buf);
        if (matcherRelease.find()) {
            dateRelease = year + "  " + matcherRelease.group(1);
        }

        String overview = "";
        String stringOverview = "Описание:</FONT>&nbsp;&nbsp;<Font face=\"Verdana, Arial, Helvetica\" Size=2 Color=#003399>(.*?)</TD></TR><TR>";
        Pattern patternOverview = Pattern.compile(stringOverview);
        Matcher matcherOverview = patternOverview.matcher(buf);
        if (matcherOverview.find()) {
            overview = matcherOverview.group(1);
        }

        String specifications = "";
        String stringSpecifications = "";
        if (range.isEmpty()) {                                                       //если период 1992-2020
            stringSpecifications = ">Тираж,.:</font>(.*?)&nbsp;&nbsp;<";
        } else {
            stringSpecifications = ">Тираж, тыс.:</font>(.*?)</TD></TR><TR><TD";
        }
        Pattern patternSpecification = Pattern.compile(stringSpecifications);
        Matcher matcherSpecifications = patternSpecification.matcher(buf);
        if (matcherSpecifications.find()) {
            specifications = matcherSpecifications.group(1).trim();
        }

        // -----перенесено из title parser
        String price = "";
        String bufPrice = "";
        String stringPatternPrice = "Цена *(.*?)руб.";
        Pattern patternPrice = Pattern.compile(stringPatternPrice);
        Matcher matcherPrice = patternPrice.matcher(buf);
        if (matcherPrice.find()) {
            bufPrice = matcherPrice.group(0);
        }
        stringPatternPrice = ":</font>(.*?)руб.";
        patternPrice = Pattern.compile(stringPatternPrice);
        matcherPrice = patternPrice.matcher(bufPrice);
        if (matcherPrice.find()) {
            price = matcherPrice.group(1).trim();
        }
        //Log.i("!@#", price);

        String catalogNumberMich = "";
        String catalogNumberITC = "";
        String catalogNumberSK = "";

        String stringPatternMich = "Михель:</font>(.*?)&nbsp;&nbsp;<";
        Pattern patternNumberMich = Pattern.compile(stringPatternMich);
        Matcher matcherNumberMich = patternNumberMich.matcher(buf);
        if (matcherNumberMich.find()) {
            catalogNumberMich = matcherNumberMich.group(1).trim();
        }
        //Log.i("!@#", catalogNumberMich);

                                 //ИТЦ:</font>1455</TD></TR></table>
        String stringPatternITC = "ИТЦ:</font>(.*?)&nbsp;&nbsp;<";
        Pattern patternNumberITC = Pattern.compile(stringPatternITC);
        Matcher matcherNumberITC = patternNumberITC.matcher(buf);
        if (matcherNumberITC.find()) {
            catalogNumberITC = matcherNumberITC.group(1);
        }else {                                                 //если не найден - второй вариант паттерна
            stringPatternITC = "ИТЦ:</font>(.*?)</TD></TR>";
            patternNumberITC = Pattern.compile(stringPatternITC);
            matcherNumberITC = patternNumberITC.matcher(buf);
           if(matcherNumberITC.find()){
                catalogNumberITC = matcherNumberITC.group(1);
            }
        }

        //Log.i("!@#", catalogNumberITC);


        String stringPatternSK = "СК:</font>(.*?)</TD></TR></table>";
        Pattern patternNumberSK = Pattern.compile(stringPatternSK);
        Matcher matcherNumberSK = patternNumberSK.matcher(buf);
        if (matcherNumberSK.find()) {
            catalogNumberSK = matcherNumberSK.group(1).trim();
        }
        //Log.i("!@#", catalogNumberSK);

        return new Stamp(country, dateRelease, overview, specifications, price, catalogNumberITC, catalogNumberSK, catalogNumberMich);
    }

    public static ArrayList<String> parseImagesUrl(String data) {
        String buf = "";
        String stringPatternBuf = "Страна:</FONT>(.*?)</tr></table></td></tr></table><br></center></TD>";

        Pattern patternBuf = Pattern.compile(stringPatternBuf);
        Matcher matcherBuf = patternBuf.matcher(data);
        if (matcherBuf.find()) {
            buf = matcherBuf.group(0);
        }

        ArrayList<String> imagesUrl = new ArrayList<>();
        String stringPatternImageUrl = "http://(.*?).jpg";
        Pattern patternImageUrl = Pattern.compile(stringPatternImageUrl);
        Matcher matcherImageUrl = patternImageUrl.matcher(buf);
        while (matcherImageUrl.find()) {
            String url = matcherImageUrl.group(0);
            imagesUrl.add(url);
        }
        return imagesUrl;
    }

    //МЕТОД, ФОРМИРУЕТ СТРОКУ ЗАПРОСА URL - по теме
    public static URL buildURL(String theme, int page, String range) {
        String urlString;
        URL urlResult = null;

        //String theme = "Космос";
        try {
            String encodeTheme = URLEncoder.encode(theme, "windows-1251");
           // urlString = String.format(searchBaseUrl, range, encodeTheme, page);
            urlString = String.format(searchBaseUrl, range, theme, page);
            urlResult = new URL(urlString);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return urlResult;
    }

    //МЕТОД, ФОРМИРУЕТ СТРОКУ ЗАПРОСА URL - по году

    public static URL buildURLByYear(int year, int page, String range) {
        String urlString;
        URL urlResult = null;

        urlString = String.format(searchBaseUrlByYear, range, year, page);
        try {
            urlResult = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return urlResult;
    }

    //МЕТОД, ФОРМИРУЕТ СТРОКУ ЗАПРОСА URL - по ключевому слову

    public static URL buildURLByKeyword(String keyword, int page, String range) {
        String urlString;
        URL urlResult = null;
        try {
            String encodeKeyword = URLEncoder.encode(keyword, "windows-1251");
            urlString = String.format(searchBaseUrlByKeyword, range, encodeKeyword, page);
            urlResult = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return urlResult;
    }

    //----------------------------------------метод и класс заменены лоадером------------------------------------------------
    public static String getStampsFromNetwork(String theme, int page, String range) {
        String result = null;
        URL url = buildURL(theme, page, range);
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

    public static String getDetailFromNetwork(String urlAsString) {
        String result = null;
        URL url = null;
        try {
            url = new URL(urlAsString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
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

    //=================================================loader===============================
    public static class DataLoader extends AsyncTaskLoader<String> {
        private Bundle bundle;
        private OnStartLoadingListener onStartLoadingListener;

        public DataLoader(@NonNull Context context, Bundle bundle) {  //конструктор
            super(context);
            this.bundle = bundle;
        }

        public interface OnStartLoadingListener {    //слушатель на Старт загрузки данных
            void onStartLoading();
        }

        public void setOnStartLoadingListener(OnStartLoadingListener onStartLoadingListener) {
            this.onStartLoadingListener = onStartLoadingListener;
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            if (onStartLoadingListener != null) {
                onStartLoadingListener.onStartLoading();
            }
            forceLoad();
        }

        @Nullable
        @Override
        public String loadInBackground() {
            if (bundle == null) {
                return null;
            }
            StringBuilder resultDownload = new StringBuilder();
            String urlAsString = bundle.getString("url");
            URL url = null;
            try {
                url = new URL(urlAsString);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            if (url == null) {
                return null;
            } else {
                HttpURLConnection urlConnection = null;
                try {
                    urlConnection = (HttpURLConnection) url.openConnection();
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
