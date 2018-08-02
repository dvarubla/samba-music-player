package com.dvarubla.sambamusicplayer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

public class Application extends android.support.multidex.MultiDexApplication {
    private static ApplicationComponent _settingsComp;

    @Override
    public void onCreate() {
        super.onCreate();
        _settingsComp = DaggerApplicationComponent.builder().setContext(getApplicationContext()).build();
        startService(new Intent(getApplicationContext(), ExitService.class));
    }

    static void onExit(){
        ItemSingleton.clear();
        _settingsComp.getPlaylist().onExit();
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
