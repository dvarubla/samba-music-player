package com.dvarubla.sambamusicplayer.filelist;

import com.dvarubla.sambamusicplayer.ApplicationComponent;
import com.dvarubla.sambamusicplayer.PerActivity;
import com.dvarubla.sambamusicplayer.toastman.IToastManActivity;

import dagger.Component;

@PerActivity
@Component(
    modules = {FileListPresenterModule.class, FileListModelModule.class},
    dependencies = {ApplicationComponent.class}
)
public interface FileListComponent {
    void inject(FileListActivity activity);
    FileListCtrl getFileListCtrl();
    IToastManActivity getToastManActivity();
}
