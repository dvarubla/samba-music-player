package com.dvarubla.sambamusicplayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

public class Application extends android.support.multidex.MultiDexApplication {
    private static ApplicationComponent _settingsComp;
    @SuppressWarnings("FieldCanBeLocal")
    private WifiManager.WifiLock _wifiLock;
    @SuppressWarnings("FieldCanBeLocal")
    private PowerManager.WakeLock _wakeLock;

    @SuppressLint("WakelockTimeout")
    @Override
    public void onCreate() {
        super.onCreate();
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(wifiManager != null) {
            _wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL, "Samba music player lock");
            _wifiLock.acquire();
        }
        PowerManager mgr = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
        if (mgr != null) {
            _wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP , "Samba music player lock");
            _wakeLock.acquire();
        }
        _settingsComp = DaggerApplicationComponent.builder().setContext(getApplicationContext()).build();
    }

    public static ApplicationComponent getComponent() {
        return _settingsComp;
    }

    public static void processAppActivity(AppCompatActivity activity){
        activity.getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
        );
    }
}
