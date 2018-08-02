package com.dvarubla.sambamusicplayer.playlist;

import com.dvarubla.sambamusicplayer.smbutils.LocationData;

import io.reactivex.Observable;

public interface IPlaylist {
    void addFile(LocationData uri);
    Observable<String> onFileAdded();
    Observable<String> onFilePlaying();
    Observable<String> onTrackChanged();
    void playNext();
    void playPrev();
    void setPlaying(boolean playing);
    Observable<Object> onStop();
    void onExit();
    boolean isPlaying();
}
