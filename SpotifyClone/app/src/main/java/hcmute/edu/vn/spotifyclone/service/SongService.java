package hcmute.edu.vn.spotifyclone.service;

import static hcmute.edu.vn.spotifyclone.MyApplication.CHANNEL_ID;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.IOException;

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

    private MediaPlayer mediaPlayer = new MediaPlayer();
    private boolean isPlaying = false;
    private Song recentSong;

    public SongService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        if(bundle != null) {
            Song song = (Song) bundle.get("object_song");

            if(song != null) {
                recentSong = song;
                isPlaying = true;
                runMusic(song);
                sendNotification(song);
            }
        }

        int musicAction = intent.getIntExtra("action_music_service", 0);
        handleMusicAction(musicAction);

        return START_NOT_STICKY;
    }

    public void runMusic(Song song){

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

        mediaPlayer.prepareAsync();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
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
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.foreground_music_play);
        remoteViews.setTextViewText(R.id.tvTitleSong, song.getSongName());
        remoteViews.setTextViewText(R.id.tvSingerSong, song.getSinger());

        if(isPlaying == true) {
            remoteViews.setOnClickPendingIntent(R.id.btnPlay_fg, getPendingIntent(this, ACTION_PAUSE));
            remoteViews.setImageViewResource(R.id.btnPlay_fg, R.drawable.button_pause);
        } else {
            remoteViews.setOnClickPendingIntent(R.id.btnPlay_fg, getPendingIntent(this, ACTION_RESUME));
            remoteViews.setImageViewResource(R.id.btnPlay_fg, R.drawable.button_play);
        }

        remoteViews.setOnClickPendingIntent(R.id.btnClear, getPendingIntent(this, ACTION_CLEAR));

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
        bundle.putBoolean("status_player", isPlaying);
        bundle.putInt("action_music", myaction);

        intent.putExtras(bundle);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}