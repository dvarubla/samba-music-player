package com.dvarubla.sambamusicplayer.filelist;

import dagger.Component;

@FileListScope
@Component(modules = FileListPresenterModule.class)
public interface FileListComponent {
    void inject(FileListActivity activity);
    FileListCtrl getFileListCtrl();
}
