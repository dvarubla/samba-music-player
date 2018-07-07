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

    @Override
    public void setView(final IFileListView view) {
        _view = view;
        _model.setLocationData(_locationData);
        _repeatSubj.onNext(new Object());
    }

    @Override
    public void init(final IFileListView view, String location){
        _view = view;
        _locationData = new LocationData(location);
        _model.setLocationData(_locationData);
        Observable<IFileOrFolderItem[]> obs = _model.getFiles().switchIfEmpty(
                getLoginAndPass()
        ).repeatWhen(completed -> _repeatSubj.toFlowable(BackpressureStrategy.MISSING)).toObservable();
        _fileListCtrl.setItemsObs(obs);
    }

    private Maybe<IFileOrFolderItem[]> getLoginAndPass(){
        return Maybe.just(new Object()).flatMap(o -> _view.showLoginPassDialog(_locationData.getServer())).flatMap(
                loginPass -> {
                    _model.setLoginPassForServer(_locationData.getServer(), loginPass);
                    _repeatSubj.onNext(new Object());
                    return Maybe.empty();
                }
        );
    }
}
