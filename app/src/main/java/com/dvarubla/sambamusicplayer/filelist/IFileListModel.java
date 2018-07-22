package com.dvarubla.sambamusicplayer.filelist;

import com.dvarubla.sambamusicplayer.smbutils.IFileOrFolderItem;
import com.dvarubla.sambamusicplayer.smbutils.LocationData;
import com.dvarubla.sambamusicplayer.smbutils.LoginPass;

import io.reactivex.Observable;

public interface IFileListModel {
    void setLocationData(LocationData location);
    Observable<IFileOrFolderItem[]> getFiles();
    void setLoginPassForServer(LocationData data, LoginPass lp);
    String addPath(String pathComp);
    String removeFromPath();
    void playFile(String file);
    void setNext();
    void setPrevious();
    void setPlaying(boolean playing);
    Observable<String> onFileAdded();
    Observable<String> onFilePlaying();
    Observable<Object> onPlaylistStop();
    boolean isPlaying();
}
