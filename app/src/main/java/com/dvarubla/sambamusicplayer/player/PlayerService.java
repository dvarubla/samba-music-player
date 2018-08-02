package com.dvarubla.sambamusicplayer.player;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.dvarubla.sambamusicplayer.R;

public class PlayerService extends Service {
    private WifiManager.WifiLock _wifiLock;
    private PowerManager.WakeLock _wakeLock;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("WakelockTimeout")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification notification = new NotificationCompat.Builder(this, "")
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.playing_music))
                .setSmallIcon(R.drawable.note_icon)
                .build();

        startForeground(2424, notification);

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(wifiManager != null) {
            _wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL, "Samba music player lock");
            _wifiLock.acquire();
        }
        PowerManager mgr = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
        if (mgr != null) {
            _wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Samba music player lock");
            _wakeLock.acquire();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        _wakeLock.release();
        _wifiLock.release();
        super.onDestroy();
    }
}
