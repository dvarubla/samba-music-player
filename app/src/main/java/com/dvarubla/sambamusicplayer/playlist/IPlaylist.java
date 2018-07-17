package com.dvarubla.sambamusicplayer.playlist;

import com.dvarubla.sambamusicplayer.smbutils.LocationData;

public interface IPlaylist {
    void addFile(LocationData uri);
    void addFileAndPlay(LocationData uri);
    void playNext();
    void playPrev();
}
