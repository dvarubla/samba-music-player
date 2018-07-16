package com.dvarubla.sambamusicplayer.player;

import com.dvarubla.sambamusicplayer.smbutils.IFileStrm;

public interface IPlayer {
    void play(String name, IFileStrm strm);
    void stop();
}
