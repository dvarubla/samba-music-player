package com.dvarubla.sambamusicplayer.player;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.dvarubla.sambamusicplayer.R;

public class PlayerService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification notification = new NotificationCompat.Builder(this, "")
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.playing_music))
                .setSmallIcon(R.drawable.note_icon)
                .build();

        startForeground(2424, notification);
        return START_STICKY;
    }
}
