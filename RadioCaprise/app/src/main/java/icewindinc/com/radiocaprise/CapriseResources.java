package icewindinc.com.radiocaprise;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import icewindinc.com.radiocaprise.common.IWUtils;

import static icewindinc.com.radiocaprise.common.IWUtils.parseJsonArrayFromString;

/**
 * Retrieves parsed resources from the cloud.
 * Pictures, categories, streaming URLs
 * Created by icewind on 08.02.17.
 */
public class CapriseResources {

    /**
     * Finds station in parsed values by given name
     * @param name - station name
     * @param genres - parsed caprise resources
     * @return CapriseStation object if found, null otherwise
     */
    public static CapriseStation findStationByName(String name, ArrayList<Genre> genres) {
        for (Genre g: genres) {
            for (CapriseStation station: g.getStations()) {
                if(station.name.equals(name)) {
                    return station;
                }
            }
        }
        return null; // if nothing found
    }

    public static ArrayList<Genre> readAll(Context ctx, int id) throws IOException {
        ArrayList<Genre> genres = new ArrayList<>();

        String jsonString = getRawResourceAsString(ctx, id);

        ArrayList<JSONObject> objs = IWUtils.parseJsonArrayFromString(jsonString);

        for (JSONObject obj:objs) {
            try {
                JSONArray stationsObj = (JSONArray) obj.get("stations");
                String genreName = obj.getString("genre");
                Genre newGenre = new Genre(genreName);

                for (int i = 0; i < stationsObj.length(); i++) {
                    JSONObject jo = (JSONObject) stationsObj.get(i);
                    String stName = jo.getString("name");
                    String stStream = jo.getString("stream");
                    String stPic = jo.getString("pic_url");
                    newGenre.addStation(stName, stStream, stPic);
                }

                genres.add(newGenre);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return genres;

    }

    private static String getRawResourceAsString(Context ctx, int id) throws IOException {
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try (InputStream is = ctx.getResources().openRawResource(id)) {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        }

        return writer.toString();
    }

}
