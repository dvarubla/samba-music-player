package com.dvarubla.sambamusicplayer.filelist;

import android.annotation.SuppressLint;

import com.dvarubla.sambamusicplayer.smbutils.FolderItem;
import com.dvarubla.sambamusicplayer.smbutils.IFileOrFolderItem;
import com.dvarubla.sambamusicplayer.smbutils.LocationData;
import com.dvarubla.sambamusicplayer.toastman.IToastMan;

import javax.inject.Inject;

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
    IToastMan _toastMan;

    @Inject
    FileListPresenter(IFileListCtrl fileListCtrl){
        _fileListCtrl = fileListCtrl;
        _repeatSubj = PublishSubject.create();
    }

    @Override
    public void setView(final IFileListView view) {
        _view = view;
        _repeatSubj.onNext(new Object());
        setViewListeners(view);
    }

    @SuppressLint("CheckResult")
    private void setViewListeners(IFileListView view){
        view.onFlingLeft().subscribe(o -> _model.setNext());
        view.onFlingRight().subscribe(o -> _model.setPrevious());
        view.onMusicPlay().subscribe(o -> {
            _model.setPlaying(true);
            view.setPlaying(true);
        });
        view.onMusicStop().subscribe(o -> {
            _model.setPlaying(false);
            view.setPlaying(false);
        });
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
        ).repeatWhen(completed -> completed.zipWith(_repeatSubj, (a, b) -> a)).map(o -> {
            if (_curLocationData.getLast().isEmpty()) {
                _view.setTitle(_curLocationData.getShare());
            } else {
                _view.setTitle(_curLocationData.getLast());
            }
            return o;
        });
        _fileListCtrl.setItemsObs(obs);

        _fileListCtrl.itemClicked().subscribe(item -> {
            if(item instanceof FolderItem){
                _curLocationData.setPath(_model.addPath(item.getName()));
                _repeatSubj.onNext(new Object());
            } else {
                _model.playFile(item.getName());
            }
        });
        setViewListeners(view);
        _model.onFileAdded().subscribe(str -> _toastMan.showFileAdded(str));
        _model.onFilePlaying().subscribe(str -> _toastMan.showFilePlaying(str));
    }

    private Maybe<IFileOrFolderItem[]> getLoginAndPass(){
        return Maybe.just(new Object()).flatMap(o -> _view.showLoginPassDialog(_rootLocationData.getServer())).flatMap(
                loginPass -> {
                    _model.setLoginPassForServer(_rootLocationData, loginPass);
                    _repeatSubj.onNext(new Object());
                    return Maybe.empty();
                }
        );
    }

    @Override
    public boolean onBackClicked() {
        if(_curLocationData.getPath().equals(_rootLocationData.getPath())){
            return true;
        }
        _curLocationData.setPath(_model.removeFromPath());
        _repeatSubj.onNext(new Object());
        return false;
    }
}
