package com.dvarubla.sambamusicplayer.filelist;

import com.dvarubla.sambamusicplayer.PerActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class FileListPresenterModule {
    @PerActivity
    @Provides
    IFileListPresenter getIFileListPresenter(FileListPresenter presenter){
        return presenter;
    }

    @PerActivity
    @Provides
    IFileListCtrl getIFileListCtrl(FileListCtrl ctrl){
        return ctrl;
    }

    @PerActivity
    @Provides
    FileListCtrl getFileListCtrl(){
        return new FileListCtrl();
    }
}
