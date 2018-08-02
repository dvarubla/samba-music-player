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
    void clear();
    void setPlaying(boolean playing);
    Observable<Object> onStop();
    boolean isPlaying();
}
