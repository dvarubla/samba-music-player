package com.dvarubla.sambamusicplayer.player;

import com.dvarubla.sambamusicplayer.smbutils.IFileStrm;

import io.reactivex.Observable;
import io.reactivex.Single;

public interface IPlayer {
    Single<Object> addEnd(String name, IFileStrm strm);
    Single<Object> removeFirst();
    void clear();
    void stop();
    void stopService();
    void startService();
    boolean canPlay();
    void play();
    io.reactivex.Observable<Object> onNeedNext();
    Observable<Object> onStop();
}
