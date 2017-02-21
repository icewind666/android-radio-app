package icewindinc.com.radiocaprise.common;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * IWUtils class for common operations.
 * Mostly network, json
 * Created by icewind on 17.02.17.
 */
public class IWUtils {
    private static IWUtils ourInstance = new IWUtils();

    public static IWUtils getInstance() {
        return ourInstance;
    }

    private IWUtils() {
    }

    public static JSONObject parseObjectFromUrl(URL link) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns array of json objects parsed from given URL.
     * Always uses http GET method to retrieve contents.
     * @param link URL of json data to parse
     * @return array of parsed json objects
     */
    public static ArrayList<JSONObject> parseArrayFromUrl(String link) {
        ArrayList<JSONObject> items = new ArrayList<>();

        try {
            URL url = new URL(link);
            HttpURLConnection urlConnection =
                    (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            // gets the server json data
            BufferedReader bufferedReader =
                    new BufferedReader(new InputStreamReader(
                            urlConnection.getInputStream()));

            String next;
            while ((next = bufferedReader.readLine()) != null){
                JSONArray ja = new JSONArray(next);

                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jo = (JSONObject) ja.get(i);
                    items.add(jo);
                }
            }
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return items;
    }


    public static ArrayList<JSONObject> parseJsonArrayFromString(String jsonStr) {
        ArrayList<JSONObject> items = new ArrayList<>();

        try {
                JSONArray ja = new JSONArray(jsonStr);

                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jo = (JSONObject) ja.get(i);
                    items.add(jo);
                }
        }
        catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return items;
    }

//
//    private class ParseTask extends AsyncTask<Void, Void, String> {
//
//        HttpURLConnection urlConnection = null;
//        BufferedReader reader = null;
//        String resultJson = "";
//
//        @Override
//        protected String doInBackground(Void... params) {
//            // получаем данные с внешнего ресурса
//            try {
//                URL url = new URL("http://androiddocs.ru/api/friends.json");
//
//                urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setRequestMethod("GET");
//                urlConnection.connect();
//
//                InputStream inputStream = urlConnection.getInputStream();
//                StringBuffer buffer = new StringBuffer();
//
//                reader = new BufferedReader(new InputStreamReader(inputStream));
//
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    buffer.append(line);
//                }
//
//                resultJson = buffer.toString();
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return resultJson;
//        }
//
//        @Override
//        protected void onPostExecute(String strJson) {
//            super.onPostExecute(strJson);
//            // выводим целиком полученную json-строку
//            Log.d(LOG_TAG, strJson);
//
//            JSONObject dataJsonObj = null;
//            String secondName = "";
//
//            try {
//                dataJsonObj = new JSONObject(strJson);
//                JSONArray friends = dataJsonObj.getJSONArray("friends");
//
//                // 1. достаем инфо о втором друге - индекс 1
//                JSONObject secondFriend = friends.getJSONObject(1);
//                secondName = secondFriend.getString("name");
//                Log.d(LOG_TAG, "Второе имя: " + secondName);
//
//                // 2. перебираем и выводим контакты каждого друга
//                for (int i = 0; i < friends.length(); i++) {
//                    JSONObject friend = friends.getJSONObject(i);
//
//                    JSONObject contacts = friend.getJSONObject("contacts");
//
//                    String phone = contacts.getString("mobile");
//                    String email = contacts.getString("email");
//                    String skype = contacts.getString("skype");
//
//                    Log.d(LOG_TAG, "phone: " + phone);
//                    Log.d(LOG_TAG, "email: " + email);
//                    Log.d(LOG_TAG, "skype: " + skype);
//                }
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//    }

}
