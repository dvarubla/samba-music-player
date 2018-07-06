package com.dvarubla.sambamusicplayer.filelist;

import com.dvarubla.sambamusicplayer.smbutils.IFileOrFolderItem;

import io.reactivex.Observable;

public interface IFileListCtrl {
    void setItemsObs(Observable<IFileOrFolderItem[]> obs);
}
