package com.dvarubla.sambamusicplayer.player;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

public class Player implements IPlayer {
    @Inject
    IServer _server;
    @Inject
    Context _context;

    private  MediaPlayer _player;

    @Inject
    Player(){
        _player = new MediaPlayer();
    }

    @Override
    public void play(String ext, InputStream strm, long size) {
        _player.reset();
        _server.setPlayData(ext, strm, size);
        try {
            _player.setDataSource(_context, Uri.parse("http://localhost:" + IServer.PORT));
            _player.prepare();
            _player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
