package com.dvarubla.sambamusicplayer.filelist;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;

import com.dvarubla.sambamusicplayer.smbutils.IFileOrFolderItem;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

class FileListCtrl implements IFileListCtrl {
    private FileListAdapter _adapter;
    FileListCtrl(){
        _adapter = new FileListAdapter();
    }
    void setView(RecyclerView view){
        view.setAdapter(_adapter);
    }

    @SuppressLint("CheckResult")
    @Override
    public void setItemsObs(Observable<IFileOrFolderItem[]> obs){
        obs.observeOn(AndroidSchedulers.mainThread()).subscribe(items -> _adapter.setItems(items));
    }
}
