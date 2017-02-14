package icewindinc.com.radiocaprise;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

/**
 * BackgroundSoundService for playing stream in background mode
 * Created by icewind on 08.02.17.
 */

public class BackgroundSoundService extends Service {
    private static final String TAG = "RadioCapriseService";
    private MediaPlayer mediaPlayer;
    private String stream;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Service onStartCommand");

        Bundle extras = intent.getExtras();

        if(extras == null)
            Log.d(TAG," Started without stream extra value!");
        else
        {
            String from = (String) extras.get("stream");
            Log.d(TAG, from);
            stream = from;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                try {
                    mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(Uri.encode(stream)));
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            Log.d(TAG, "Player prepared. starting to play.");
                            mediaPlayer.start();
                        }
                    });
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Stopping player");
        mediaPlayer.stop();
        mediaPlayer.release();
    }

}
