package com.dvarubla.sambamusicplayer.filelist;

import com.dvarubla.sambamusicplayer.smbutils.SmbUtilsComponent;

import dagger.Component;

@FileListScope
@Component(modules = {FileListPresenterModule.class, FileListModelModule.class}, dependencies = SmbUtilsComponent.class)
public interface FileListComponent {
    void inject(FileListActivity activity);
    FileListCtrl getFileListCtrl();
}
