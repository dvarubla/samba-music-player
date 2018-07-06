package com.dvarubla.sambamusicplayer.filelist;

import org.mockito.Mockito;

import dagger.Module;
import dagger.Provides;

@Module
public class FileListModuleModelMT {
    @FileListScope
    @Provides
    IFileListModel model(){
        return Mockito.mock(IFileListModel.class);
    }

}
