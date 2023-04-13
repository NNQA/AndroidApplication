package hcmute.edu.vn.spotifyclone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import hcmute.edu.vn.spotifyclone.service.SongService;

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

//        action
        int musicAction = intent.getIntExtra("action_music", 0);

        Intent intentService = new Intent(context, SongService.class);
        intentService.putExtra("action_music_service", musicAction);

        context.startService(intentService);

//        Information

    }
}
