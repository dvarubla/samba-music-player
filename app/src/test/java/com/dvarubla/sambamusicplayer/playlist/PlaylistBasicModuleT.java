package com.dvarubla.sambamusicplayer.playlist;

import com.dvarubla.sambamusicplayer.PerActivity;
import com.dvarubla.sambamusicplayer.player.IPlayer;
import com.dvarubla.sambamusicplayer.settings.ILoginPassMan;
import com.dvarubla.sambamusicplayer.smbutils.ISmbUtils;

import org.mockito.Mockito;

import dagger.Module;
import dagger.Provides;

@Module
public class PlaylistBasicModuleT {
    @Provides
    @PerActivity
    IPlayer getPlayer(){
        return Mockito.mock(IPlayer.class);
    }

    @Provides
    @PerActivity
    ISmbUtils getSmbUtils(){
        return Mockito.mock(ISmbUtils.class);
    }

    @Provides
    @PerActivity
    ILoginPassMan getLoginPassMan(){
        return Mockito.mock(ILoginPassMan.class);
    }
}
