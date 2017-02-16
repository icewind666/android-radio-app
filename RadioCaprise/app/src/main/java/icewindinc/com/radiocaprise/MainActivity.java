package icewindinc.com.radiocaprise;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.koushikdutta.ion.Ion;

public class MainActivity extends AppCompatActivity {


    private final static String stream = "http://bbcmedia.ic.llnwd.net/stream/bbcmedia_radio2_mf_p";

    ImageButton play;
    boolean started = false;

    private void setStationImage(String url) {
        ImageView stView = (ImageView)findViewById(R.id.stationPicture);
        Ion.with(stView)
                .load(url);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ListView mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        //mDrawerList.setAdapter(new ArrayAdapter<String>(this,
         //       R.layout.drawer_list_item, mPlanetTitles));

        play = (ImageButton) findViewById(R.id.play);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (started) {
                    Intent intent = new Intent(MainActivity.this, BackgroundSoundService.class);
                    stopService(intent);
                    started = false;

                } else {
                    Intent intent = new Intent(MainActivity.this, BackgroundSoundService.class);
                    intent.putExtra("stream", stream);
                    startService(intent);
                    started = true;
                    startService(play);
                }
            }
        });
    }

    public void startService(View v) {
        Intent serviceIntent = new Intent(MainActivity.this, NotificationService.class);
        serviceIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
        startService(serviceIntent);
    }
}
