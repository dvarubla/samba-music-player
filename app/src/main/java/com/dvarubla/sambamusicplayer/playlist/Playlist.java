package com.dvarubla.sambamusicplayer.playlist;

import android.annotation.SuppressLint;

import com.dvarubla.sambamusicplayer.player.IPlayer;
import com.dvarubla.sambamusicplayer.settings.ILoginPassMan;
import com.dvarubla.sambamusicplayer.smbutils.ISmbUtils;
import com.dvarubla.sambamusicplayer.smbutils.LocationData;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import io.reactivex.Emitter;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class Playlist implements IPlaylist{
    private IPlayer _player;
    private ILoginPassMan _lpman;

    private ISmbUtils _smbUtils;
    private ArrayList<LocationData> _uris;
    private PublishSubject<Observable> _quantumSubj;
    private PublishSubject<String> _playingSubj;
    private PublishSubject<String> _trackChangedSubj;
    private PublishSubject<String> _addedSubj;
    private Subject<Object> _onStopSubj;
    private int _curIndex;
    private boolean _stopped;
    private AtomicBoolean _isPlaying;
    private boolean _needClearWhenPlayed;
    private int _numAdded;

    @Override
    public boolean isPlaying() {
        return _isPlaying.get();
    }

    @Override
    public void setPlaying(boolean playing) {
        if(!playing){
            _player.stop();
            _quantumSubj.onNext(Observable.fromCallable(() -> {
                _needClearWhenPlayed = false;
                _isPlaying.set(false);
                _player.stopService();
                return new Object();
            }));
        } else {
            _quantumSubj.onNext(Observable.<Observable<Object>>create(emitter -> {
                if(_player.canPlay()) {
                    _isPlaying.set(true);
                    if (_needClearWhenPlayed) {
                        clear();
                    }
                    if (_curIndex != _uris.size()) {
                        _player.startService();
                        if (_numAdded == 0) {
                            addItem(emitter, _uris.get(_curIndex));
                        }
                        if (_numAdded != 2 && _curIndex != _uris.size() - 1) {
                            addItem(emitter, _uris.get(_curIndex + 1));
                        }
                    }
                    emitter.onNext(Observable.create(em -> {
                        _player.play();
                        em.onComplete();
                    }));
                } else {
                    _onStopSubj.onNext(new Object());
                }
                emitter.onComplete();
            }).concatMap(o -> o));
        }
    }

    @SuppressLint("CheckResult")
    @Inject
    Playlist(IPlayer player, ILoginPassMan lpman, ISmbUtils smbUtils){
        _isPlaying = new AtomicBoolean();
        _isPlaying.set(false);
        _needClearWhenPlayed = false;
        _stopped = true;
        _player = player;
        _lpman = lpman;
        _smbUtils = smbUtils;
        _curIndex = 0;
        _uris = new ArrayList<>();
        _addedSubj = PublishSubject.create();
        _playingSubj = PublishSubject.create();
        _quantumSubj = PublishSubject.create();
        _trackChangedSubj = PublishSubject.create();
        _onStopSubj = PublishSubject.create().toSerialized();

        _quantumSubj.concatMap(obs -> obs.observeOn(Schedulers.io())).subscribe();

        _player.onNeedNext().subscribe(o -> onNeedNext());
        _player.onStop().subscribe(o -> {
            setPlaying(false);
            _onStopSubj.onNext(new Object());
        });
    }

    private void addItem(Emitter<Observable<Object>> emitter, LocationData data){
        if(_isPlaying.get()) {
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
        if(_isPlaying.get()) {
            _numAdded--;
            emitter.onNext(_player.removeFirst().toObservable());
        }
    }

    private void clear(){
        if(_isPlaying.get()) {
            doClear();
        }
    }

    private void doClear(){
        _numAdded = 0;
        _player.clear();
    }

    private void setCurrent(int index){
        if(_isPlaying.get()) {
            _playingSubj.onNext(_uris.get(index).getLast());
        } else {
            _trackChangedSubj.onNext(_uris.get(index).getLast());
        }
    }

    private void onNeedNext(){
        _quantumSubj.onNext(Observable.<Observable<Object>>create(
                emitter -> {
                    if(_numAdded != 0) {
                        removeFirst(emitter);
                        if (_curIndex != _uris.size() - 1) {
                            _curIndex++;
                            setCurrent(_curIndex);
                            if (_curIndex != _uris.size() - 1) {
                                addItem(emitter, _uris.get(_curIndex + 1));
                            }
                        } else {
                            _player.stopService();
                            _stopped = true;
                        }
                    }
                    emitter.onComplete();
                }
        ).concatMap(o -> o));
    }

    @Override
    public void addFile(LocationData uri) {
        _quantumSubj.onNext(Observable.<Observable<Object>>create(
                emitter -> {
                    _uris.add(uri);
                    _addedSubj.onNext(uri.getLast());
                    if(_stopped){
                        _player.startService();
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
    public Observable<String> onTrackChanged() {
        return _trackChangedSubj;
    }

    @Override
    public Observable<String> onFilePlaying() {
        return _playingSubj;
    }

    @Override
    public void playNext() {
        _quantumSubj.onNext(Observable.<Observable<Object>>create(
                emitter -> {
                    if (_curIndex != _uris.size() - 1) {
                        _needClearWhenPlayed = true;
                        _curIndex++;
                        setCurrent(_curIndex);
                        clear();
                        addItem(emitter, _uris.get(_curIndex));
                        if(_curIndex != _uris.size() - 1) {
                            addItem(emitter, _uris.get(_curIndex + 1));
                        }
                    }
                    emitter.onComplete();
                }
        ).concatMap(o -> o));
    }

    @Override
    public void playPrev() {
        _quantumSubj.onNext(Observable.<Observable<Object>>create(
                emitter -> {
                    if(_curIndex != 0){
                        _needClearWhenPlayed = true;
                        clear();
                        _curIndex--;
                        setCurrent(_curIndex);
                        addItem(emitter, _uris.get(_curIndex));
                        if(_curIndex != _uris.size() - 1) {
                            addItem(emitter, _uris.get(_curIndex + 1));
                        }
                    }
                    emitter.onComplete();
                }
        ).concatMap(o -> o));
    }

    @Override
    public Observable<Object> onStop() {
        return _onStopSubj;
    }

    @Override
    public void onExit() {
        _quantumSubj.onNext(Observable.fromCallable(() -> {
            doClear();
            _uris.clear();
            setPlaying(false);
            return new Object();
        }));
    }
}
