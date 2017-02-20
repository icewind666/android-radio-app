package icewindinc.com.radiocaprise;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.ion.Ion;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Radio Caprise main activity
 */
public class MainActivity extends AppCompatActivity {

    private String stream = "";
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ArrayList<Genre> genres;
    private Timer metadataReceiverTimer;

    ImageButton play;
    boolean started = false;
    ExpandableListView expListView;
    ExpandableListAdapter listAdapter;
    HashMap<String, List<String>> listDataChild;
    ArrayList<String> listDataHeader;


    /**
     *
     * @param url
     */
    private void setStationImage(String url) {
        ImageView stView = (ImageView)findViewById(R.id.stationPicture);
        Ion.with(stView)
                .load(url);
        stView.setAlpha(0.3f);
    }

    /**
     *
     */
    private void prepareListData() {
        try {
            listDataHeader = new ArrayList<>();
            listDataChild = new HashMap<>();

            genres = CapriseResources.readAll(getApplicationContext(), R.raw.stations);

            for (Genre genre : genres) {
                listDataHeader.add(genre.name);
                List<String> stationNames = new ArrayList<>();

                for(CapriseStation station: genre.getStations() ) {
                    stationNames.add(station.name);
                }

                listDataChild.put(genre.name, stationNames);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        expListView = (ExpandableListView) findViewById(R.id.left_drawer);

        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                String stationName = listDataChild.get(listDataHeader.get(groupPosition)).get(
                                childPosition);

                CapriseStation stationObj = CapriseResources.findStationByName(stationName, genres);

                loadSelectedStation(stationObj);
                mDrawerLayout.closeDrawer(expListView);
                return false;
            }
        });


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        );

        // Set actionBarDrawerToggle as the DrawerListener
        mDrawerLayout.addDrawerListener(actionBarDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        play = (ImageButton) findViewById(R.id.play);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (started) {
                    Intent intent = new Intent(MainActivity.this, BackgroundSoundService.class);
                    stopService(intent);
                    stopMusicService();
                    started = false;

                } else {
                    Intent intent = new Intent(MainActivity.this, BackgroundSoundService.class);
                    intent.putExtra("stream", stream);
                    startService(intent);
                    started = true;
                    startMusicService(play);
                }
            }
        });
        //mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
    }

    /**
     * Loads station to main view.
     * Picture. Names, sets the stream;
     * @param capriseStation - station object
     */
    private void loadSelectedStation(CapriseStation capriseStation) {
        // 1. start picture loading. its async and slow then other things.
        setStationImage(capriseStation.picture);

        // 2. Set station name and genre
        setStationName(capriseStation.name);
        setStationGenre(capriseStation.genre.name);

        // 3. set the stream url
        setStreamUrl(capriseStation.stream);
    }

    /**
     *
     * @param stream
     */
    private void setStreamUrl(String stream) {
        this.stream = stream;
    }

    /**
     *
     * @param name
     */
    private void setStationGenre(String name) {
        TextView tv = (TextView) findViewById(R.id.genreValue);
        tv.setText(name);
    }

    /**
     *
     * @param name
     */
    private void setStationName(String name) {
        TextView tv = (TextView) findViewById(R.id.stationValue);
        tv.setText(name);
    }

    /**
     *
     * @param v
     */
    public void startMusicService(View v) {
        Intent serviceIntent = new Intent(MainActivity.this, NotificationService.class);
        serviceIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
        startService(serviceIntent);

        metadataReceiverTimer = new Timer();
        metadataReceiverTimer.schedule(new TimerTask() {
            public void run() {
                try {
                    MetadataTask task = new MetadataTask();
                    task.execute(new URL(stream));
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }, 0, 3000);

    }

    public void stopMusicService() {
        metadataReceiverTimer.cancel();
        stopService(new Intent(MainActivity.this, NotificationService.class));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // call ActionBarDrawerToggle.onOptionsItemSelected(), if it returns true
        // then it has handled the app icon touch event
        return actionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        actionBarDrawerToggle.syncState();
    }


    protected class MetadataTask extends AsyncTask<URL, Void, IcyStreamMeta> {
        protected IcyStreamMeta streamMeta;

        @Override
        protected IcyStreamMeta doInBackground(URL... urls) {
            streamMeta = new IcyStreamMeta(urls[0]);
            try {
                streamMeta.refreshMeta();
            } catch (IOException e) {
                // TODO: Handle
                Log.e(MetadataTask.class.toString(), e.getMessage());
            }
            return streamMeta;
        }

        @Override
        protected void onPostExecute(IcyStreamMeta result) {
            try {
                TextView tvArtist = (TextView) findViewById(R.id.artistValue);
                TextView tvSong = (TextView) findViewById(R.id.songValue);
                tvArtist.setText(streamMeta.getArtist());
                tvSong.setText(streamMeta.getTitle());
            } catch (IOException e) {
                // TODO: Handle
                Log.e(MetadataTask.class.toString(), e.getMessage());
            }
        }

    }
}
