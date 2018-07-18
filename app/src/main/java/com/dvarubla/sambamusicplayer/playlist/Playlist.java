package com.dvarubla.sambamusicplayer.playlist;

import android.annotation.SuppressLint;

import com.dvarubla.sambamusicplayer.player.IPlayer;
import com.dvarubla.sambamusicplayer.settings.ILoginPassMan;
import com.dvarubla.sambamusicplayer.smbutils.ISmbUtils;
import com.dvarubla.sambamusicplayer.smbutils.LocationData;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class Playlist implements IPlaylist{
    private IPlayer _player;
    private ILoginPassMan _lpman;

    private ISmbUtils _smbUtils;
    private ArrayList<LocationData> _uris;
    private PublishSubject<LocationData> _playSubj;
    private PublishSubject<String> _playingSubj;
    private PublishSubject<String> _addedSubj;
    private int _curIndex;

    @SuppressLint("CheckResult")
    @Inject
    Playlist(IPlayer player, ILoginPassMan lpman, ISmbUtils smbUtils){
        _player = player;
        _lpman = lpman;
        _smbUtils = smbUtils;
        _curIndex = 0;
        _uris = new ArrayList<>();
        _playSubj = PublishSubject.create();
        _addedSubj = PublishSubject.create();
        _playingSubj = PublishSubject.create();
        _playSubj.observeOn(Schedulers.io()).concatMap(data -> _smbUtils.getFileStream(data, _lpman.getLoginPass(data)).toObservable().
                observeOn(Schedulers.io()).map(
                strm -> {
                    _player.play(getFileExt(data.getPath()), strm);
                    return strm;
                }
        )).subscribe();
        _player.onStop().subscribe(o -> playNext());
    }

    @Override
    public synchronized void addFile(LocationData uri) {
        _uris.add(uri);
        _addedSubj.onNext(getFileName(uri));
        if(_uris.size() == 1){
            _curIndex = 0;
            _playSubj.onNext(_uris.get(_curIndex));
        } else if(!_player.isPlaying() && _curIndex == _uris.size() - 1 - 1){
            _curIndex++;
            _playSubj.onNext(_uris.get(_curIndex));
        }
    }

    @Override
    public Observable<String> onFileAdded() {
        return _addedSubj;
    }

    @Override
    public Observable<String> onFilePlaying() {
        return _playingSubj;
    }

    @Override
    public synchronized void addFileAndPlay(LocationData uri) {
        _uris.add(uri);
        _addedSubj.onNext(getFileName(uri));
        _playSubj.onNext(_uris.get(_uris.size() - 1));
    }

    @Override
    public synchronized void playNext() {
        if (_curIndex != _uris.size() - 1) {
            _curIndex++;
            _playingSubj.onNext(getFileName(_uris.get(_curIndex)));
            _playSubj.onNext(_uris.get(_curIndex));
        }
    }

    @Override
    public synchronized void playPrev() {
        if(_curIndex != 0){
            _curIndex--;
            _playingSubj.onNext(getFileName(_uris.get(_curIndex)));
            _playSubj.onNext(_uris.get(_curIndex));
        }
    }

    private String getFileExt(String fileName){
        int i = fileName.lastIndexOf('.');
        return fileName.substring(i+1);
    }

    private String getFileName(LocationData path){
        int i = path.getPath().lastIndexOf('/');
        return path.getPath().substring(i+1);
    }
}
