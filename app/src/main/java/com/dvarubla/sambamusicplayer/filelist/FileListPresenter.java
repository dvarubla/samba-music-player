package com.dvarubla.sambamusicplayer.filelist;

import android.annotation.SuppressLint;

import com.dvarubla.sambamusicplayer.smbutils.FolderItem;
import com.dvarubla.sambamusicplayer.smbutils.IFileOrFolderItem;
import com.dvarubla.sambamusicplayer.smbutils.LocationData;

import javax.inject.Inject;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class FileListPresenter implements IFileListPresenter {
    private IFileListCtrl _fileListCtrl;
    private LocationData _rootLocationData;
    private PublishSubject<Object> _repeatSubj;
    private LocationData _curLocationData;
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
        _model.setLocationData(_rootLocationData);
        _repeatSubj.onNext(new Object());
    }

    @SuppressLint("CheckResult")
    @Override
    public void init(final IFileListView view, String location){
        _view = view;
        _rootLocationData = new LocationData(location);
        _curLocationData = _rootLocationData.clone();
        _model.setLocationData(_rootLocationData);
        Observable<IFileOrFolderItem[]> obs = _model.getFiles().switchIfEmpty(
                getLoginAndPass().toObservable()
        ).repeatWhen(completed -> completed.toFlowable(BackpressureStrategy.BUFFER).zipWith(
                _repeatSubj.toFlowable(BackpressureStrategy.BUFFER), (a, b) -> a
        ).toObservable());
        _fileListCtrl.setItemsObs(obs);
        _fileListCtrl.itemClicked().subscribe(item -> {
            if(item instanceof FolderItem){
                _curLocationData.setPath(_model.addPath(item.getName()));
                _model.update();
            }
        });
    }

    private Maybe<IFileOrFolderItem[]> getLoginAndPass(){
        return Maybe.just(new Object()).flatMap(o -> _view.showLoginPassDialog(_rootLocationData.getServer())).flatMap(
                loginPass -> {
                    _model.setLoginPassForServer(_rootLocationData.getServer(), loginPass);
                    _repeatSubj.onNext(new Object());
                    return Maybe.empty();
                }
        );
    }
}
