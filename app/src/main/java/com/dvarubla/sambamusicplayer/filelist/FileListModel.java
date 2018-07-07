package com.dvarubla.sambamusicplayer.filelist;

import com.dvarubla.sambamusicplayer.settings.ISettings;
import com.dvarubla.sambamusicplayer.smbutils.IFileOrFolderItem;
import com.dvarubla.sambamusicplayer.smbutils.ISmbUtils;
import com.dvarubla.sambamusicplayer.smbutils.LocationData;
import com.dvarubla.sambamusicplayer.smbutils.LoginPass;

import java.util.HashMap;

import javax.inject.Inject;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.subjects.PublishSubject;

public class FileListModel implements IFileListModel {
    @Inject
    ISmbUtils _smbUtils;

    private PublishSubject<Object> _updateSubj;

    private HashMap<String, LoginPass> _authData;

    private LocationData _locData;

    private ISettings _settings;

    @Inject
    FileListModel(ISettings settings){
        _settings = settings;
        _authData = _settings.getAuthData();
        _updateSubj = PublishSubject.create();
    }

    @Override
    public Observable<IFileOrFolderItem[]> getFiles() {
        return Observable.just(new Object()).
                flatMap(o -> {
                    if(_authData.containsKey(_locData.getServer())){
                        return _smbUtils.connectToServer(_locData.getServer(), _authData.get(_locData.getServer())).toObservable();
                    } else {
                        return _smbUtils.connectToServer(_locData.getServer(), new LoginPass("", "")).toObservable();
                    }
                }).
                flatMap(o -> getFilesWrap());
    }

    private Observable<IFileOrFolderItem[]> getFilesWrap(){
        return Single.just(new Object()).flatMap(o -> _smbUtils.getFilesFromShare(_locData.getShare(), _locData.getPath())).
                repeatWhen(completed -> _updateSubj.toFlowable(BackpressureStrategy.BUFFER)).toObservable();
    }

    @Override
    public void setLoginPassForServer(String server, LoginPass lp) {
        _authData.put(server, lp);
        _settings.saveAuthData(_authData);
    }

    @Override
    public void setLocationData(LocationData location) {
        _locData = location.clone();
    }

    @Override
    public String addPath(String pathComp) {
        if(_locData.getPath().equals("")){
            _locData.setPath(pathComp);
        } else {
            _locData.setPath(_locData.getPath() + "/" + pathComp);
        }
        return _locData.getPath();
    }

    @Override
    public void update(){
        _updateSubj.onNext(new Object());
    }
}
