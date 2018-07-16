package com.dvarubla.sambamusicplayer.smbutils;

import io.reactivex.Single;

public interface IFileStrm {
    Single<Integer> read(byte [] buf, int offset, int len);
    long getSize();
    void close();
}
