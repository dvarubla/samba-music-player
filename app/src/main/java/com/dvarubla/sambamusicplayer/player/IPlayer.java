package com.dvarubla.sambamusicplayer.player;

import java.io.InputStream;

public interface IPlayer {
    void play(InputStream strm, long size);
}
