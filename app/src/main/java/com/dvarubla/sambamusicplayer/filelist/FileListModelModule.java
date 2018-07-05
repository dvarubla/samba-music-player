package com.dvarubla.sambamusicplayer.filelist;

import dagger.Module;
import dagger.Provides;

@Module
public class FileListModelModule {
    @FileListScope
    @Provides
    IFileListModel getModel(FileListModel model){
        return model;
    }
}
