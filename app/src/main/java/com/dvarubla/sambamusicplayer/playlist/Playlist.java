package com.dvarubla.sambamusicplayer.playlist;

import android.annotation.SuppressLint;

import com.dvarubla.sambamusicplayer.player.IPlayer;
import com.dvarubla.sambamusicplayer.settings.ILoginPassMan;
import com.dvarubla.sambamusicplayer.smbutils.ISmbUtils;
import com.dvarubla.sambamusicplayer.smbutils.LocationData;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class Playlist implements IPlaylist{
    private IPlayer _player;
    private ILoginPassMan _lpman;

    private ISmbUtils _smbUtils;
    private ArrayList<LocationData> _uris;
    private PublishSubject<Observable> _quantumSubj;
    private PublishSubject<String> _playingSubj;
    private PublishSubject<String> _addedSubj;
    private int _curIndex;
    private boolean _stopped;
    private int _numAdded;
    private AtomicInteger _numTasks;

    @SuppressLint("CheckResult")
    @Inject
    Playlist(IPlayer player, ILoginPassMan lpman, ISmbUtils smbUtils){
        _stopped = true;
        _player = player;
        _lpman = lpman;
        _smbUtils = smbUtils;
        _curIndex = 0;
        _uris = new ArrayList<>();
        _addedSubj = PublishSubject.create();
        _playingSubj = PublishSubject.create();
        _quantumSubj = PublishSubject.create();
        _numTasks = new AtomicInteger(0);

        _quantumSubj.concatMap(obs -> obs.observeOn(Schedulers.io())).subscribe();

        _player.onNeedNext().subscribe(o -> onNeedNext());
    }

    private Observable<Object> addItem(LocationData data){
        _numAdded++;
        return _smbUtils.getFileStream(data, _lpman.getLoginPass(data)).toObservable().
                observeOn(Schedulers.io()).map(
                strm -> _player.addEnd(getFileExt(data.getPath()), strm)
        ).concatMap(Single::toObservable);
    }

    private Observable<Object> removeFirst(){
        _numAdded--;
        return _player.removeFirst().toObservable();
    }

    private void onNeedNext(){
        if(_numTasks.get() == 0) {
            _numTasks.incrementAndGet();
            _quantumSubj.onNext(Observable.<Observable<Object>>create(
                    emitter -> {
                        emitter.onNext(removeFirst());
                        if (_curIndex != _uris.size() - 1) {
                            _curIndex++;
                            _playingSubj.onNext(getFileName(_uris.get(_curIndex)));
                            if (_curIndex != _uris.size() - 1) {
                                _numAdded++;
                                emitter.onNext(addItem(_uris.get(_curIndex + 1)));
                            }
                        } else {
                            _stopped = true;
                        }
                        emitter.onComplete();
                    }
            ).concatMap(o -> o).doFinally(() -> _numTasks.decrementAndGet()));
        }
    }

    @Override
    public void addFile(LocationData uri) {
        _quantumSubj.onNext(Observable.<Observable<Object>>create(
                emitter -> {
                    _uris.add(uri);
                    _addedSubj.onNext(getFileName(uri));
                    if(_stopped){
                        if(_uris.size() == 1){
                            _curIndex = 0;
                        } else {
                            _curIndex++;
                        }
                        _stopped = false;
                        emitter.onNext(addItem(_uris.get(_curIndex)));
                    } else if(_numAdded != 2 && _curIndex != _uris.size() - 1){
                        emitter.onNext(addItem(_uris.get(_curIndex + 1)));
                    }
                    emitter.onComplete();
                }
        ).concatMap(o -> o));
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
    public void playNext() {
        _numTasks.incrementAndGet();
        _quantumSubj.onNext(Observable.<Observable<Object>>create(
                emitter -> {
                    if (_curIndex != _uris.size() - 1) {
                        _curIndex++;
                        _playingSubj.onNext(getFileName(_uris.get(_curIndex)));
                        _numAdded = 0;
                        _player.clear();
                        emitter.onNext(addItem(_uris.get(_curIndex)));
                        if(_curIndex != _uris.size() - 1) {
                            emitter.onNext(addItem(_uris.get(_curIndex + 1)));
                        }
                    }
                    emitter.onComplete();
                }
        ).concatMap(o -> o).doFinally(() -> _numTasks.decrementAndGet()));
    }

    @Override
    public void playPrev() {
        _numTasks.incrementAndGet();
        _quantumSubj.onNext(Observable.<Observable<Object>>create(
                emitter -> {
                    if(_curIndex != 0){
                        _numAdded = 0;
                        _player.clear();
                        _curIndex--;
                        _playingSubj.onNext(getFileName(_uris.get(_curIndex)));
                        emitter.onNext(addItem(_uris.get(_curIndex)));
                        if(_curIndex != _uris.size() - 1) {
                            emitter.onNext(addItem(_uris.get(_curIndex + 1)));
                        }
                    }
                    emitter.onComplete();
                }
        ).concatMap(o -> o).doFinally(() -> _numTasks.decrementAndGet()));
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
