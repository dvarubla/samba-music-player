package com.dvarubla.sambamusicplayer.filelist;

import com.dvarubla.sambamusicplayer.smbutils.LocationData;
import com.dvarubla.sambamusicplayer.smbutils.LoginPass;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.MaybeObserver;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class FileListPresenter implements IFileListPresenter {
    private IFileListCtrl _fileListCtrl;
    private LocationData _locationData;
    @Inject
    IFileListModel _model;

    @Inject
    FileListPresenter(IFileListCtrl fileListCtrl){
        _fileListCtrl = fileListCtrl;
    }

    private Maybe<String[]> getFiles(){
        return _model.getFiles(_locationData).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public void setView(final IFileListView view) {getFiles().switchIfEmpty(
                Maybe.create((MaybeOnSubscribe<Maybe<String[]>>) emitter -> view.showLoginPassDialog(_locationData.getServer()).
                subscribe(new MaybeObserver<LoginPass>() {
            @Override
            public void onSubscribe(Disposable d) {}

            @Override
            public void onSuccess(LoginPass loginPass) {
                _model.setLoginPassForServer(_locationData.getServer(), loginPass);
                emitter.onSuccess(getFiles());
            }

            @Override
            public void onError(Throwable e) {}

            @Override
            public void onComplete() {
                emitter.onComplete();
            }
        })).flatMap(maybe -> maybe)).subscribe(new MaybeObserver<String[]>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(String[] strings) {
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
}
