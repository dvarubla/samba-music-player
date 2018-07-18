package com.dvarubla.sambamusicplayer.playlist;

import com.dvarubla.sambamusicplayer.PerActivity;

import dagger.Component;

@PerActivity
@Component(modules = {PlaylistBasicModuleT.class})
public interface PlaylistComponentT {
    void inject(PlaylistBasicT test);
    Playlist getPlaylist();
}
