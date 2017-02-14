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
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {


    private final static String stream = "http://bbcmedia.ic.llnwd.net/stream/bbcmedia_radio2_mf_p";
    Button play;
    TextView currentUrl;
    ProgressBar progressBar;
    boolean started = false;
    boolean prepared = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        play = (Button) findViewById(R.id.play);
        currentUrl = (TextView) findViewById(R.id.currentUrlPlaying);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (started) {
                    Intent intent = new Intent(MainActivity.this, BackgroundSoundService.class);
                    stopService(intent);
                    play.setText(R.string.play);
                    progressBar.setVisibility(View.INVISIBLE);
                    currentUrl.setText("");
                    started = false;

                } else {
                    Intent intent = new Intent(MainActivity.this, BackgroundSoundService.class);
                    intent.putExtra("stream", stream);
                    currentUrl.setText(stream);
                    progressBar.setVisibility(View.VISIBLE);
                    startService(intent);
                    play.setText(R.string.pause);
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
