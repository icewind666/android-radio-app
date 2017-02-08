package icewindinc.com.radiocaprise;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {


    private final static String stream = "http://bbcmedia.ic.llnwd.net/stream/bbcmedia_radio2_mf_p";
    Button play;
    ProgressBar progressBar;
    boolean started = false;
    boolean prepared = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        play = (Button) findViewById(R.id.play);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (started) {
                    Intent intent = new Intent(MainActivity.this, BackgroundSoundService.class);
                    stopService(intent);
                    progressBar.setVisibility(View.INVISIBLE);

                    started = false;

                } else {
                    Intent intent = new Intent(MainActivity.this, BackgroundSoundService.class);
                    intent.putExtra("stream", stream);
                    progressBar.setVisibility(View.VISIBLE);
                    startService(intent);
                    started = true;
                }
            }
        });
    }
}
