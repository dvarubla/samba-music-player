package com.dvarubla.sambamusicplayer.filelist;

import org.mockito.Mockito;

import dagger.Module;
import dagger.Provides;

@Module
public class FileListModuleT {

    @FileListScope
    @Provides
    IFileListCtrl ctrl(){
        return Mockito.mock(IFileListCtrl.class);
    }

    @FileListScope
    @Provides
    IFileListView view(){
        return Mockito.mock(IFileListView.class);
    }
}
