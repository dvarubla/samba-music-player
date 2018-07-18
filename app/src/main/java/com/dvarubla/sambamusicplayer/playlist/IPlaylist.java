package com.dvarubla.sambamusicplayer.playlist;

import com.dvarubla.sambamusicplayer.smbutils.LocationData;

import io.reactivex.Observable;

public interface IPlaylist {
    void addFile(LocationData uri);
    void addFileAndPlay(LocationData uri);
    Observable<String> onFileAdded();
    void playNext();
    void playPrev();
}
