package com.dvarubla.sambamusicplayer.filelist;

import com.dvarubla.sambamusicplayer.player.IPlayer;
import com.dvarubla.sambamusicplayer.settings.ISettings;
import com.dvarubla.sambamusicplayer.smbutils.IFileOrFolderItem;
import com.dvarubla.sambamusicplayer.smbutils.ISmbUtils;
import com.dvarubla.sambamusicplayer.smbutils.LocationData;
import com.dvarubla.sambamusicplayer.smbutils.LoginPass;

import java.util.HashMap;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class FileListModel implements IFileListModel {
    @Inject
    ISmbUtils _smbUtils;

    private IPlayer _player;

    private PublishSubject<Object> _onAudioClickSubj;

    private HashMap<String, LoginPass> _authData;

    private LocationData _locData;
    private LocationData _fileLocData;

    private ISettings _settings;


    @Inject
    FileListModel(ISettings settings, IPlayer player){
        _player = player;
        _settings = settings;
        _authData = _settings.getAuthData();
        PublishSubject<Object> onStopSubj = PublishSubject.create();
        _onAudioClickSubj = PublishSubject.create();

        Observable.just(new Object()).observeOn(Schedulers.io()).
        delaySubscription(_onAudioClickSubj).map(o -> {
            _player.stop();
            return new Object();
        }).zipWith(onStopSubj, (a, b) -> b).observeOn(Schedulers.io()).
        flatMap(o -> _smbUtils.getFileStream(_fileLocData, getLoginPass(_fileLocData)).toObservable().
        observeOn(Schedulers.io()).map(
            strm -> {
                _player.play(getFileExt(_fileLocData.getPath()), strm);
                return strm;
            }
        )).
        zipWith(_player.onStart(), (a, b) -> a).repeatWhen(c -> c.zipWith(_onAudioClickSubj, (a, b) -> a)).
        zipWith(_player.onFileFinish(), (a, b) -> a).map(strm -> {
            strm.close();
            return new Object();
        }).subscribe();
        _player.onStop().subscribe(onStopSubj);
        onStopSubj.onNext(new Object());
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
                o -> _smbUtils.getFilesFromShare(_locData, getLoginPass(_locData)).toObservable()
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
    public void playFile(String file){
        _fileLocData = _locData.clone();
        _fileLocData.setPath(joinPath(_locData.getPath(), file));
        _onAudioClickSubj.onNext(new Object());
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
