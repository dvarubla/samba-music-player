package com.dvarubla.sambamusicplayer.filelist;

import com.dvarubla.sambamusicplayer.PerActivity;

import dagger.Component;

@PerActivity
@Component(modules = {FileListModuleT.class, FileListModuleModelMT.class})
public interface FileListComponentPresenterT {
    void inject(FileListPresenterShowT test);
    void inject(FileListPresenterClickT test);
}
