package com.dvarubla.sambamusicplayer.filelist;

import com.dvarubla.sambamusicplayer.PerActivity;
import com.dvarubla.sambamusicplayer.toastman.IToastMan;

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

    @PerActivity
    @Provides
    IToastMan toastMan(){
        return Mockito.mock(IToastMan.class);
    }
}
