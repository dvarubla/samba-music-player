package com.dvarubla.sambamusicplayer.filelist;

import com.dvarubla.sambamusicplayer.smbutils.IFileOrFolderItem;
import com.dvarubla.sambamusicplayer.smbutils.LocationData;
import com.dvarubla.sambamusicplayer.smbutils.LoginPass;

import io.reactivex.Maybe;

public interface IFileListModel {
    void setLocationData(LocationData location);
    Maybe<IFileOrFolderItem[]> getFiles();
    void setLoginPassForServer(String server, LoginPass lp);
}
