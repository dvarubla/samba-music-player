package com.dvarubla.sambamusicplayer.filelist;

import com.dvarubla.sambamusicplayer.smbutils.IFileOrFolderItem;
import com.dvarubla.sambamusicplayer.smbutils.LocationData;
import com.dvarubla.sambamusicplayer.smbutils.LoginPass;

import io.reactivex.Observable;

public interface IFileListModel {
    void setLocationData(LocationData location);
    Observable<IFileOrFolderItem[]> getFiles();
    void setLoginPassForServer(String server, LoginPass lp);
    String addPath(String pathComp);
    String removeFromPath();
    void update();
    void playFile(String file);
}
