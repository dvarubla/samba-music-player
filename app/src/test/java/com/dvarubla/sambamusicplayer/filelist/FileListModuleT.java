package com.dvarubla.sambamusicplayer.filelist;

import com.dvarubla.sambamusicplayer.PerActivity;

import org.mockito.Mockito;

import dagger.Module;
import dagger.Provides;

@Module
public class FileListModuleT {

    @PerActivity
    @Provides
    IFileListCtrl ctrl(){
        return Mockito.mock(IFileListCtrl.class);
    }

    @PerActivity
    @Provides
    IFileListView view(){
        return Mockito.mock(IFileListView.class);
    }
}
