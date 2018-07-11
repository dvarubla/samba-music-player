package com.dvarubla.sambamusicplayer.player;

import java.io.InputStream;

public interface IPlayer {
    void play(String name, InputStream strm, long size);
}
