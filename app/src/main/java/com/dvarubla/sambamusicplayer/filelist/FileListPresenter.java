package com.dvarubla.sambamusicplayer.filelist;

import com.dvarubla.sambamusicplayer.smbutils.IFileOrFolderItem;
import com.dvarubla.sambamusicplayer.smbutils.LocationData;

import javax.inject.Inject;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class FileListPresenter implements IFileListPresenter {
    private IFileListCtrl _fileListCtrl;
    private LocationData _locationData;
    private PublishSubject<Object> _repeatSubj;
    private IFileListView _view;
    @Inject
    IFileListModel _model;

    @Inject
    FileListPresenter(IFileListCtrl fileListCtrl){
        _fileListCtrl = fileListCtrl;
        _repeatSubj = PublishSubject.create();
    }

    private Maybe<IFileOrFolderItem[]> getFiles(){
        return _model.getFiles(_locationData);
    }

    @Override
    public void setView(final IFileListView view) {
        _view = view;
        Observable<IFileOrFolderItem[]> obs = getFiles().switchIfEmpty(
            getLoginAndPass()
        ).repeatWhen(completed -> _repeatSubj.toFlowable(BackpressureStrategy.MISSING)).
        toObservable();
        _fileListCtrl.setItemsObs(obs);
    }

    @Override
    public void setLocation(String location) {
        _locationData = new LocationData(location);
    }

    private Maybe<IFileOrFolderItem[]> getLoginAndPass(){
        return _view.showLoginPassDialog(_locationData.getServer()).flatMap(
                loginPass -> {
                    _model.setLoginPassForServer(_locationData.getServer(), loginPass);
                    _repeatSubj.onNext(new Object());
                    return Maybe.empty();
                }
        );
    }
}
