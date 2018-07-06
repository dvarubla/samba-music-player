package com.dvarubla.sambamusicplayer.filelist;

import dagger.Component;

@FileListScope
@Component(modules = {FileListModuleT.class, FileListModuleModelMT.class})
public interface FileListComponentPresenterT {
    void inject(FileListPresenterT test);
}
