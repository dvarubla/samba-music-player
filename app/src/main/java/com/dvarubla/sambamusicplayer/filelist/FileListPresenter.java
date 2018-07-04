package com.dvarubla.sambamusicplayer.filelist;

import javax.inject.Inject;

public class FileListPresenter implements IFileListPresenter {
    private IFileListCtrl _fileListCtrl;

    @Inject
    FileListPresenter(IFileListCtrl fileListCtrl){
        _fileListCtrl = fileListCtrl;
        _fileListCtrl.setItems(new String[]{
                "a", "b"
        });
    }

    @Override
    public void setView(IFileListView view) {
    }
}
