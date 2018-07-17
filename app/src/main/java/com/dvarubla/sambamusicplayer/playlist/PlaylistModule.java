package com.dvarubla.sambamusicplayer.playlist;

import com.dvarubla.sambamusicplayer.PerApplication;

import dagger.Module;
import dagger.Provides;

@Module
public class PlaylistModule {

    @PerApplication
    @Provides
    IPlaylist getPlaylist(Playlist playlist){
        return playlist;
    }
}
