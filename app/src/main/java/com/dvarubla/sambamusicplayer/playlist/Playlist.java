package com.dvarubla.sambamusicplayer.playlist;

import android.annotation.SuppressLint;

import com.dvarubla.sambamusicplayer.player.IPlayer;
import com.dvarubla.sambamusicplayer.settings.ILoginPassMan;
import com.dvarubla.sambamusicplayer.smbutils.ISmbUtils;
import com.dvarubla.sambamusicplayer.smbutils.LocationData;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import io.reactivex.Emitter;
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
    private boolean _isPlaying;
    private int _numAdded;
    private AtomicInteger _numTasks;

    @Override
    public void setPlaying(boolean playing) {
        if(!playing){
            _quantumSubj.onNext(Observable.fromCallable(() -> {
                clear();
                _isPlaying = false;
                return new Object();
            }));
        } else {
            _numTasks.incrementAndGet();
            _quantumSubj.onNext(Observable.<Observable<Object>>create(emitter -> {
                clear();
                _isPlaying = true;
                if(_curIndex != _uris.size()) {
                    setPlaying(_curIndex);
                    addItem(emitter, _uris.get(_curIndex));
                }
                if(_curIndex != _uris.size() - 1) {
                    addItem(emitter, _uris.get(_curIndex + 1));
                }
                emitter.onComplete();
            }).concatMap(o -> o).doFinally(() -> _numTasks.decrementAndGet()));
        }
    }

    @SuppressLint("CheckResult")
    @Inject
    Playlist(IPlayer player, ILoginPassMan lpman, ISmbUtils smbUtils){
        _isPlaying = false;
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

    private void addItem(Emitter<Observable<Object>> emitter, LocationData data){
        if(_isPlaying) {
            _numAdded++;
            emitter.onNext(
                    _smbUtils.getFileStream(data, _lpman.getLoginPass(data)).toObservable().
                            observeOn(Schedulers.io()).map(
                            strm -> _player.addEnd(data.getFileExt(), strm)
                    ).concatMap(Single::toObservable)
            );
        }
    }

    private void removeFirst(Emitter<Observable<Object>> emitter){
        if(_isPlaying) {
            _numAdded--;
            emitter.onNext(_player.removeFirst().toObservable());
        }
    }

    private void clear(){
        if(_isPlaying) {
            _numAdded = 0;
            _player.clear();
        }
    }

    private void setPlaying(int index){
        if(_isPlaying) {
            _playingSubj.onNext(_uris.get(index).getLast());
        }
    }

    private void onNeedNext(){
        if(_numTasks.get() == 0) {
            _numTasks.incrementAndGet();
            _quantumSubj.onNext(Observable.<Observable<Object>>create(
                    emitter -> {
                        removeFirst(emitter);
                        if (_curIndex != _uris.size() - 1) {
                            _curIndex++;
                            setPlaying(_curIndex);
                            if (_curIndex != _uris.size() - 1) {
                                _numAdded++;
                                addItem(emitter, _uris.get(_curIndex + 1));
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
                    _addedSubj.onNext(uri.getLast());
                    if(_stopped){
                        if(_uris.size() == 1){
                            _curIndex = 0;
                        } else {
                            _curIndex++;
                        }
                        _stopped = false;
                        addItem(emitter, _uris.get(_curIndex));
                    } else if(_numAdded != 2 && _curIndex != _uris.size() - 1){
                        addItem(emitter, _uris.get(_curIndex + 1));
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
                        setPlaying(_curIndex);
                        clear();
                        addItem(emitter, _uris.get(_curIndex));
                        if(_curIndex != _uris.size() - 1) {
                            addItem(emitter, _uris.get(_curIndex + 1));
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
                        clear();
                        _curIndex--;
                        setPlaying(_curIndex);
                        addItem(emitter, _uris.get(_curIndex));
                        if(_curIndex != _uris.size() - 1) {
                            addItem(emitter, _uris.get(_curIndex + 1));
                        }
                    }
                    emitter.onComplete();
                }
        ).concatMap(o -> o).doFinally(() -> _numTasks.decrementAndGet()));
    }
}
