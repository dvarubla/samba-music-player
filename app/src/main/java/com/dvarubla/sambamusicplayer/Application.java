package com.dvarubla.sambamusicplayer;

public class Application extends android.support.multidex.MultiDexApplication {
    private static ApplicationComponent _settingsComp;

    @Override
    public void onCreate() {
        super.onCreate();
        _settingsComp = DaggerApplicationComponent.builder().setContext(getApplicationContext()).build();
    }

    public static ApplicationComponent getComponent() {
        return _settingsComp;
    }
}
