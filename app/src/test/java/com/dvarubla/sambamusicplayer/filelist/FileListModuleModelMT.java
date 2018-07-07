package com.dvarubla.sambamusicplayer.filelist;

import com.dvarubla.sambamusicplayer.PerActivity;

import org.mockito.Mockito;

import dagger.Module;
import dagger.Provides;

@Module
public class FileListModuleModelMT {
    @PerActivity
    @Provides
    IFileListModel model(){
        return Mockito.mock(IFileListModel.class);
    }

}
