package icewindinc.com.radiocaprise;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;

import java.io.IOException;
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

    private String stream;
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

    BackgroundSoundService mService;
    boolean mBound = false;
    int previousVolume;
    private ArrayList<CapriseStation> payedUserStations;



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

            filterStationsListAvailable();

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

    private void filterStationsListAvailable() throws IOException {
        ArrayList<Genre> genreList = CapriseResources.readAll(getApplicationContext(), R.raw.stations);
        for (Genre stationsList : genres) {
            //if (payedUserStations.)
            //TODO: show only payed stations
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stream = "";
        setContentView(R.layout.activity_main);

        requestUserPayments();

        expListView = (ExpandableListView) findViewById(R.id.left_drawer);

        // preparing list data
        prepareListData();
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        // setting list adapter
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
        expListView.setAdapter(listAdapter);
        setSliderPanel();
        setPlayButton();
        attachVolumeControl();
    }

    private void requestUserPayments() {
        payedUserStations = new ArrayList<>();
        //TODO: implement
    }

    private void setSliderPanel() {
        // setting slide panel
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        );

        // Set actionBarDrawerToggle as the DrawerListener
        mDrawerLayout.addDrawerListener(actionBarDrawerToggle);

        // this if for icon changing in slide panel : opened/closed
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        catch (NullPointerException ex) {
            Log.d("radio", "Error: getSupportActionBar() returned null");
        }


    }

    private void setPlayButton() {
        play = (ImageButton) findViewById(R.id.play);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (started) {
                    Intent intent = new Intent(MainActivity.this, BackgroundSoundService.class);
                    stopService(intent);
                    stopMusicService();
                    started = false;
                    play.setImageResource(R.mipmap.play);

                } else {
                    Intent intent = new Intent(MainActivity.this, BackgroundSoundService.class);
                    intent.putExtra("stream", stream);
                    startService(intent);
                    started = true;
                    startMusicService(play);

                    // change icon
                    play.setImageResource(R.mipmap.pause);
                }
            }
        });
    }

    private void attachVolumeControl() {
        final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int a = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int c = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        final TextView percentValue = (TextView) findViewById(R.id.percentValue);
        final SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);

        seekBar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, Integer.parseInt(percentValue.getText().toString().trim()), 0);
                seekBar.setProgress(Integer.parseInt(percentValue.getText().toString().trim()));
            }
        });

        seekBar.setMax(a);
        seekBar.setProgress(c);
        percentValue.setText("" + c);
        final ImageButton muteBtn = (ImageButton) findViewById(R.id.mute);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, arg1, 0);
                if (arg1 != 0) {
                    muteBtn.setImageResource(R.mipmap.ic_volume_up_black_48dp);
                }
                else {
                    muteBtn.setImageResource(R.mipmap.ic_volume_mute_black_48dp);
                }
                percentValue.setText("" + seekBar.getProgress());
            }
        });


        muteBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(seekBar.getProgress() == 0) {
                    seekBar.setProgress(previousVolume);
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, previousVolume, 0);
                    muteBtn.setImageResource(R.mipmap.ic_volume_off_black_48dp);
                }
                else {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                    previousVolume = Integer.parseInt(percentValue.getText().toString().trim());
                    seekBar.setProgress(0);
                    muteBtn.setImageResource(R.mipmap.ic_volume_mute_black_48dp);
                }
            }
        });
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

        // stopping notify bar service
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

    private CapriseStation getNextStation(CapriseStation currentStation) {
        //TODO: return next available station
    }



    private class MetadataTask extends AsyncTask<URL, Void, IcyStreamMeta> {
        private IcyStreamMeta streamMeta;

        @Override
        protected IcyStreamMeta doInBackground(URL... urls) {
            streamMeta = new IcyStreamMeta(urls[0]);
            try {
                streamMeta.refreshMeta();
            } catch (IOException e) {
                // TODO: Handle
                Log.e("radio", e.getMessage());
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
                Log.e("radio", e.getMessage());
            }
        }

    }
}
