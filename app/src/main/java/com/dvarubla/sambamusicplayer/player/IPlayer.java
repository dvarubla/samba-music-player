package com.dvarubla.sambamusicplayer.player;

import com.dvarubla.sambamusicplayer.smbutils.IFileStrm;

import io.reactivex.Observable;

public interface IPlayer {
    void play(String name, IFileStrm strm);
    void stop();
    Observable<Object> onStop();
    Observable<Object> onStart();
    Observable<Object> onFileFinish();
}
