package com.dvarubla.sambamusicplayer.filelist;

import dagger.Module;
import dagger.Provides;

@Module
public class FileListPresenterModule {
    @FileListScope
    @Provides
    IFileListPresenter getIFileListPresenter(FileListPresenter presenter){
        return presenter;
    }

    @FileListScope
    @Provides
    IFileListCtrl getIFileListCtrl(FileListCtrl ctrl){
        return ctrl;
    }

    @FileListScope
    @Provides
    FileListCtrl getFileListCtrl(){
        return new FileListCtrl();
    }
}
