package com.dvarubla.sambamusicplayer.player;

import com.dvarubla.sambamusicplayer.PerApplication;

import dagger.Module;
import dagger.Provides;

@Module
public class PlayerModule {
    @PerApplication
    @Provides
    public IPlayer getPlayer(Player player){
        return player;
    }
}
