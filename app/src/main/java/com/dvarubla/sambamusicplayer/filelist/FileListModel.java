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
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class FileListModel implements IFileListModel {
    @Inject
    ISmbUtils _smbUtils;

    @Inject
    IPlayer _player;

    private PublishSubject<Object> _updateSubj;
    private Flowable<Object> _updateSubjFlowable;

    private HashMap<String, LoginPass> _authData;

    private LocationData _locData;

    private ISettings _settings;

    @Inject
    FileListModel(ISettings settings){
        _settings = settings;
        _authData = _settings.getAuthData();
        _updateSubj = PublishSubject.create();
        _updateSubjFlowable = _updateSubj.toFlowable(BackpressureStrategy.BUFFER);
    }

    private LoginPass getLoginPass(LocationData data){
        if(_authData.containsKey(data.getServer())){
            return _authData.get(data.getServer());
        } else {
            return new LoginPass("", "");
        }
    }

    @Override
    public Observable<IFileOrFolderItem[]> getFiles() {
        return Observable.just(new Object()).flatMap(
                o -> _smbUtils.getFilesFromShare(_locData, getLoginPass(_locData)).toObservable())
                .repeatWhen(
                    o -> _updateSubjFlowable.toObservable()
                );
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
        LocationData tLoc = _locData.clone();
        tLoc.setPath(joinPath(_locData.getPath(), file));
        _smbUtils.getFileStream(tLoc, getLoginPass(tLoc)).subscribe(
                strmAndSize -> _player.play(getFileExt(file), strmAndSize.strm, strmAndSize.size)
        );
    }

    private String getFileExt(String fileName){
        int i = fileName.lastIndexOf('.');
        return fileName.substring(i+1);
    }

    private String joinPath(String path1, String path2){
        if(path1.equals("")){
            return path2;
        }
        return path1 + "/" + path2;
    }
}
