package hcmute.edu.vn.spotifyclone.service;

import static hcmute.edu.vn.spotifyclone.MyApplication.CHANNEL_ID;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import hcmute.edu.vn.spotifyclone.MusicPlay_Activity;
import hcmute.edu.vn.spotifyclone.MyReceiver;
import hcmute.edu.vn.spotifyclone.R;
import hcmute.edu.vn.spotifyclone.model.Song;

public class SongService extends Service {

    public static final int ACTION_PAUSE = 1;
    public static final int ACTION_RESUME = 2;
    public static final int ACTION_CLEAR = 3;
    public static final int ACTION_NEXT = 4;
    public static final int ACTION_PREVIOUS = 5;
    public static final int ACTION_SEND_INFO = 6;


    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private Song recentSong;
    private List<Song> recentPlaylist = new ArrayList<>();
    public int indexSong = 0;
    public int totalDuration = 1;
    private int currentProgress = 1;
    private int nextProgress = 1;
    RemoteViews remoteViews;
    private Handler mHandler;

    public SongService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        remoteViews = new RemoteViews(getPackageName(), R.layout.foreground_music_play);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        if(bundle != null) {
            Song song = (Song) bundle.get("object_song");
            List<Song> listSong = bundle.getParcelableArrayList("object_list_song");

            if(song != null && listSong != null) {
                recentSong = song;
                recentPlaylist = listSong;

                indexSong = 0;
                for (Song temp : recentPlaylist) {
                    if (recentSong.getSongId().equals(temp.getSongId())){
                        break;
                    }
                    indexSong++;
                }

                isPlaying = true;
                runMusic(recentSong);
                sendNotification(recentSong);
            } else if (song == null && listSong != null){

                recentPlaylist = listSong;
                indexSong = 0;
                recentSong = recentPlaylist.get(indexSong);

//                recentSong = song;
//                isPlaying = true;
//                runMusic(song);
//                sendNotification(song);

                isPlaying = true;
                runMusic(recentSong);
                sendNotification(recentSong);
            }
        }

        int musicAction = intent.getIntExtra("action_music_service", 0);
        if (musicAction == ACTION_SEND_INFO) {
            nextProgress = intent.getIntExtra("change_progress", 0);
        }
        handleMusicAction(musicAction);

        mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendProgressToActivity();
                mHandler.postDelayed(this, 500);
            }
        }, 500);

        return START_NOT_STICKY;
    }

    public void runMusic(Song song){

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        mediaPlayer = new MediaPlayer();

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        mediaPlayer.setAudioAttributes(audioAttributes);

        String audioUrl = song.getSource();

        try {
            mediaPlayer.setDataSource(audioUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                nextMusic();
                sendActionToActivity(ACTION_NEXT);
            }
        });

        mediaPlayer.prepareAsync();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
                totalDuration = mediaPlayer.getDuration();
            }
        });
    }

    public void handleMusicAction(int action){
        switch (action) {
            case ACTION_PAUSE:
                pauseMusic();
                break;
            case ACTION_RESUME:
                resumeMusic();
                break;
            case ACTION_CLEAR:
                stopSelf();
                sendActionToActivity(ACTION_CLEAR);
                break;
            case ACTION_NEXT:
                nextMusic();
                sendActionToActivity(ACTION_NEXT);
                break;
            case ACTION_PREVIOUS:
                previousMusic();
                sendActionToActivity(ACTION_PREVIOUS);
                break;
            case ACTION_SEND_INFO:
                if (mediaPlayer != null){
                    mediaPlayer.seekTo(nextProgress);
                }
                break;
        }
    }

    private void previousMusic() {
        if(indexSong - 1 >= 0) {
            indexSong --;
            recentSong = recentPlaylist.get(indexSong);
            runMusic(recentSong);
        }
    }

    private void nextMusic() {
        if (indexSong + 1 < recentPlaylist.size()){
            indexSong++;
            recentSong = recentPlaylist.get(indexSong);
            runMusic(recentSong);
        }
    }

    private void resumeMusic() {
        if(mediaPlayer != null && isPlaying == false){
            mediaPlayer.start();
            isPlaying = true;
            sendNotification(recentSong);
            sendActionToActivity(ACTION_RESUME);
        }
    }

    private void pauseMusic() {
        if(mediaPlayer != null && isPlaying == true){
            mediaPlayer.pause();
            isPlaying = false;
            sendNotification(recentSong);
            sendActionToActivity(ACTION_PAUSE);
        }
    }

    private void sendNotification(Song song) {
        Intent intent = new Intent(this, MusicPlay_Activity.class);
        intent.putExtra("sondIdFromService", song.getSongId());
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        remoteViews.setTextViewText(R.id.tvTitleSong, song.getSongName());
        remoteViews.setTextViewText(R.id.tvSingerSong, song.getSinger());
        InputStream inputStream = null;
        try {
            inputStream = new URL(song.getImage()).openStream();
        }catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        remoteViews.setImageViewBitmap(R.id.imgSong_fg, bitmap);


        if(isPlaying == true) {
            remoteViews.setOnClickPendingIntent(R.id.btnPlay_fg, getPendingIntent(this, ACTION_PAUSE));
            remoteViews.setImageViewResource(R.id.btnPlay_fg, R.drawable.button_pause);
        } else {
            remoteViews.setOnClickPendingIntent(R.id.btnPlay_fg, getPendingIntent(this, ACTION_RESUME));
            remoteViews.setImageViewResource(R.id.btnPlay_fg, R.drawable.button_play);
        }

        remoteViews.setOnClickPendingIntent(R.id.btnClear, getPendingIntent(this, ACTION_CLEAR));
        remoteViews.setOnClickPendingIntent(R.id.btnNext_fg, getPendingIntent(this, ACTION_NEXT));
        remoteViews.setOnClickPendingIntent(R.id.btnPrevious_fg, getPendingIntent(this, ACTION_PREVIOUS));


        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.play_arrow)
                .setContentIntent(pendingIntent)
                .setCustomBigContentView(remoteViews)
                .build();

        startForeground(1, notification);
    }

    private PendingIntent getPendingIntent(Context context, int action) {
        Intent intent = new Intent(this, MyReceiver.class);
        intent.putExtra("action_music", action);

        return PendingIntent.getBroadcast(context.getApplicationContext(), action, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void sendActionToActivity(int myaction){
        Intent intent = new Intent("send_action_to_act");
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_song", recentSong);
        bundle.putBoolean("status_player", isPlaying);
        bundle.putInt("action_music", myaction);

        intent.putExtras(bundle);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void sendProgressToActivity(){

        if (mediaPlayer == null){
            mHandler.removeCallbacksAndMessages(null);
            return;
        }
        Intent intent = new Intent("send_in4_to_act");
        Bundle bundle = new Bundle();
        bundle.putInt("total_duration", totalDuration);
        bundle.putInt("current_progress", mediaPlayer.getCurrentPosition());

        intent.putExtras(bundle);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}