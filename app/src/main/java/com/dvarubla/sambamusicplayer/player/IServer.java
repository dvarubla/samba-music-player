package com.dvarubla.sambamusicplayer.player;

import com.dvarubla.sambamusicplayer.smbutils.IFileStrm;

import io.reactivex.Observable;

public interface IServer {
    int PORT = 8889;
    void setPlayData(String ext, IFileStrm strm);
    void stop();
    void start();
    Observable<Object> onStop();
    Observable<Object> onStart();
    Observable<Object> onFileFinish();
}
