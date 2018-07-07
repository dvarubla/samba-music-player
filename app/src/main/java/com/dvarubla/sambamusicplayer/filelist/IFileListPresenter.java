package com.dvarubla.sambamusicplayer.filelist;

interface IFileListPresenter {
    void setView(IFileListView view);
    void init(final IFileListView view, String location);
    boolean onBackClicked();
}
