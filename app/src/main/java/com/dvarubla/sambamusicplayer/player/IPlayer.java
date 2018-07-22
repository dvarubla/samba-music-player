package com.dvarubla.sambamusicplayer.player;

import com.dvarubla.sambamusicplayer.smbutils.IFileStrm;

import io.reactivex.Observable;
import io.reactivex.Single;

public interface IPlayer {
    Single<Object> addEnd(String name, IFileStrm strm);
    Single<Object> removeFirst();
    void clear();
    void stop();
    boolean play();
    io.reactivex.Observable<Object> onNeedNext();
    void onExit();
    Observable<Object> onStop();
}
