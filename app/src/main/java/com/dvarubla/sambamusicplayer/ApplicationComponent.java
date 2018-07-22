package com.dvarubla.sambamusicplayer;

import android.content.Context;

import com.dvarubla.sambamusicplayer.player.IPlayer;
import com.dvarubla.sambamusicplayer.player.PlayerModule;
import com.dvarubla.sambamusicplayer.playlist.IPlaylist;
import com.dvarubla.sambamusicplayer.playlist.PlaylistModule;
import com.dvarubla.sambamusicplayer.settings.ILoginPassMan;
import com.dvarubla.sambamusicplayer.settings.ISettings;
import com.dvarubla.sambamusicplayer.settings.LoginPassModule;
import com.dvarubla.sambamusicplayer.settings.SettingsModule;
import com.dvarubla.sambamusicplayer.smbutils.ISmbUtils;
import com.dvarubla.sambamusicplayer.smbutils.SmbUtilsModule;
import com.dvarubla.sambamusicplayer.toastman.IToastMan;
import com.dvarubla.sambamusicplayer.toastman.IToastManActivity;
import com.dvarubla.sambamusicplayer.toastman.ToastManModule;

import dagger.BindsInstance;
import dagger.Component;

@PerApplication
@Component(modules = {
        SettingsModule.class, SmbUtilsModule.class, PlayerModule.class,
        PlaylistModule.class, LoginPassModule.class, ToastManModule.class
})
public interface ApplicationComponent {
    IPlayer getPlayer();
    ISettings getSettings();
    ISmbUtils getSmbUtils();
    IPlaylist getPlaylist();
    ILoginPassMan getLoginPassMan();
    IToastMan getToastMan();
    IToastManActivity getToastManActivity();

    @Component.Builder
    interface Builder {
        ApplicationComponent build();
        @BindsInstance
        Builder setContext(Context context);
    }
}
