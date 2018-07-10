package com.dvarubla.sambamusicplayer.player;

import java.io.InputStream;

public interface IServer {
    int PORT = 8889;
    void setPlayData(InputStream strm, long size);
}
