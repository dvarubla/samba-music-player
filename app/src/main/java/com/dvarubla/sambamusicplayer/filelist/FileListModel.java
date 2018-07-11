package com.dvarubla.sambamusicplayer.filelist;

import android.annotation.SuppressLint;

import com.dvarubla.sambamusicplayer.player.IPlayer;
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

    @Inject
    IPlayer _player;

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
        String path = joinPath(_locData.getPath(), pathComp);
        _locData.setPath(path);
        return path;
    }

    @Override
    public String removeFromPath() {
        _locData.setPath(_locData.getPath().replaceFirst("(?:^|/)[^/]+$", ""));
        return _locData.getPath();
    }

    @Override
    public void update(){
        _updateSubj.onNext(new Object());
    }

    @SuppressLint("CheckResult")
    @Override
    public void playFile(String file){
        _smbUtils.getFileStream(_locData.getShare(), joinPath(_locData.getPath(), file)).subscribe(
                strmAndSize -> _player.play(strmAndSize.strm, strmAndSize.size)
        );
    }

    private String joinPath(String path1, String path2){
        if(path1.equals("")){
            return path2;
        }
        return path1 + "/" + path2;
    }
}
