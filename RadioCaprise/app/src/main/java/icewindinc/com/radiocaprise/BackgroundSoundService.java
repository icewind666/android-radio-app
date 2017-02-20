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
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

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
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("cookie", "beget=begetok");


                try {
                    //stream = "http://79.120.39.202:9069";
                    stream = "http://usa8-vn.mixstream.net:8138";
                    //query = URLEncoder.encode(your_query_string, "utf-8");
                    URL url = new URL(stream);
                    Log.d("media", url.toURI().toString());
                    Uri parsed = Uri.parse(url.toURI().toString());
                    Log.d("media", parsed.toString());
                    mediaPlayer.setDataSource(getApplicationContext(), parsed, headers);
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            Log.d(TAG, "Player prepared. starting to play.");
                            mediaPlayer.start();
                        }
                    });

                    mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                        @Override
                        public boolean onError(MediaPlayer mp, int what, int extra) {
                            switch(what) {
                                case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                                    Log.d("media", "TIMEOUT");
                                    break;
                                case MediaPlayer.MEDIA_ERROR_MALFORMED:
                                    Log.d("media", "MALFORMED");
                                    break;
                                case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                                    Log.d("media", "UNSUPPORTED");
                                    break;
                                case MediaPlayer.MEDIA_ERROR_IO:
                                    Log.d("media", "IO error");
                                    break;
                                case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                                    Log.d("media", "SERVER DIED");
                                    break;
                                case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                                    Log.d("media", "UNKNOWN ERROR!");
                                    break;


                                default:
                                    Log.d("media", "ErrorErrorErrorErrorErrorError");
                            }
                            return false;
                        }
                    });

                    mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener(){
                        public void onBufferingUpdate(MediaPlayer mPlayer, int percent) {
                            Log.d("media"," Mediaplayer ready (preparation done). Inside buffer listener");
                            Log.d("media", "Buffered " + percent + " %");
                        }
                    });

                    mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                        @Override
                        public boolean onInfo(MediaPlayer mp, int what, int extra) {
                            switch (what) {
                                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                                    Toast.makeText(getApplicationContext(), "Start buffering", Toast.LENGTH_SHORT).show();
                                    break;
                                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                                    Toast.makeText(getApplicationContext(), "Stop buffering", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                            return false;
                        }
                    });
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (URISyntaxException e) {
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
