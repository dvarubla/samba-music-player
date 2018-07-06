package com.dvarubla.sambamusicplayer.filelist;

import com.dvarubla.sambamusicplayer.smbutils.LocationData;

import javax.inject.Inject;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Maybe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
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

    private Maybe<String[]> getFiles(){
        return _model.getFiles(_locationData);
    }

    @Override
    public void setView(final IFileListView view) {
        _view = view;
        getFiles().switchIfEmpty(
            getLoginAndPass()
        ).repeatWhen(completed -> _repeatSubj.toFlowable(BackpressureStrategy.MISSING)).
        toObservable().subscribe(new Observer<String[]>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(String[] strings) {
                _fileListCtrl.setItems(strings);
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });
    }

    @Override
    public void setLocation(String location) {
        _locationData = new LocationData(location);
    }

    private Maybe<String[]> getLoginAndPass(){
        return _view.showLoginPassDialog(_locationData.getServer()).flatMap(
                loginPass -> {
                    _model.setLoginPassForServer(_locationData.getServer(), loginPass);
                    _repeatSubj.onNext(new Object());
                    return Maybe.empty();
                }
        );
    }
}
