package com.dvarubla.sambamusicplayer.filelist;

import com.dvarubla.sambamusicplayer.smbutils.LocationData;
import com.dvarubla.sambamusicplayer.smbutils.LoginPass;

import io.reactivex.Maybe;

public interface IFileListModel {
    Maybe<String[]> getFiles(LocationData location);
    void setLoginPassForServer(String server, LoginPass lp);
}
