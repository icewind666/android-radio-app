package icewindinc.com.radiocaprise;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

import wseemann.media.FFmpegMediaPlayer;

/**
 * BackgroundSoundService for playing stream in background mode
 * Created by icewind on 08.02.17.
 */

public class BackgroundSoundService extends Service {
    private static final String TAG = "RadioCapriseService";
    private FFmpegMediaPlayer mp;
    private String stream;
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();


    public class LocalBinder extends Binder {
        BackgroundSoundService getService() {
            // Return this instance of LocalService so clients can call public methods
            return BackgroundSoundService.this;
        }
    }
    /**
     *
     */
    public void setVolume(int value) {
        if (mp != null) {
            mp.setVolume((float)value, (float)value);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
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
                mp = new FFmpegMediaPlayer();
                mp.setOnPreparedListener(new FFmpegMediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(FFmpegMediaPlayer mp) {
                        mp.start();
                    }
                });
                mp.setOnErrorListener(new FFmpegMediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(FFmpegMediaPlayer mp, int what, int extra) {
                        mp.release();
                        Log.d("media-player", "Player error! Releasing");
                        return false;
                    }
                });

                try {
                    mp.setDataSource(stream);

                    mp.prepareAsync();
                } catch (IllegalArgumentException | SecurityException | IOException | IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        }
        ).start();

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Stopping player");
        mp.stop();
        mp.release();
    }

}
