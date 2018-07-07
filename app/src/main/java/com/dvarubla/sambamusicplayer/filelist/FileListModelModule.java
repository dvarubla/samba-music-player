package com.dvarubla.sambamusicplayer.filelist;

import com.dvarubla.sambamusicplayer.PerActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class FileListModelModule {
    @PerActivity
    @Provides
    IFileListModel getModel(FileListModel model){
        return model;
    }
}
