package com.dvarubla.sambamusicplayer.filelist;

interface IFileListPresenter {
    void setView(IFileListView view);
    void setLocation(String location);
}
