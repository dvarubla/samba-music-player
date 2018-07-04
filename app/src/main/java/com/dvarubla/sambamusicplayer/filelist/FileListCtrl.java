package com.dvarubla.sambamusicplayer.filelist;

import android.support.v7.widget.RecyclerView;

class FileListCtrl implements IFileListCtrl {
    private FileListAdapter _adapter;
    FileListCtrl(){
        _adapter = new FileListAdapter();
    }
    void setView(RecyclerView view){
        view.setAdapter(_adapter);
    }

    @Override
    public void setItems(String[] items){
        _adapter.setStrings(items);
    }
}
