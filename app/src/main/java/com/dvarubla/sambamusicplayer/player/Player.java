package com.dvarubla.sambamusicplayer.player;

import android.media.MediaPlayer;

import com.dvarubla.sambamusicplayer.smbutils.IFileStrm;

import java.io.IOException;

import javax.inject.Inject;

import io.reactivex.Observable;

public class Player implements IPlayer {
    private IServer _server;

    private MediaPlayer _player;

    @Inject
    Player(IServer server){
        _player = new MediaPlayer();
        _server = server;
    }

    @Override
    public void play(String ext, IFileStrm strm) {
        _server.setPlayData(ext, strm);
        _server.start();
        try {
            _player.setDataSource("http://localhost:" + IServer.PORT + "/t." + ext);
            _player.prepare();
            _player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        _player.reset();
        _server.stop();
    }

    @Override
    public Observable<Object> onStop() {
        return _server.onStop();
    }
    @Override
    public Observable<Object> onStart() {
        return _server.onStart();
    }
    @Override
    public Observable<Object> onFileFinish() {
        return _server.onFileFinish();
    }
}
