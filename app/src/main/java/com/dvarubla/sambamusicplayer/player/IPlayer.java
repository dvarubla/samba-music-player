package com.dvarubla.sambamusicplayer.player;

import com.dvarubla.sambamusicplayer.smbutils.IFileStrm;

import io.reactivex.Single;

public interface IPlayer {
    Single<Object> addEnd(String name, IFileStrm strm);
    Single<Object> removeFirst();
    void clear();
    boolean isStopped();
    io.reactivex.Observable<Object> onNeedNext();
}
