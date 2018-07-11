package com.dvarubla.sambamusicplayer.smbutils;

import java.io.InputStream;

import io.reactivex.Maybe;

public interface ISmbUtils {
    class StrmAndSize{
        public InputStream strm;
        public long size;
        StrmAndSize(InputStream strm, long size){
            this.size = size;
            this.strm = strm;
        }
    }
    Maybe<IFileOrFolderItem[]> getFilesFromShare(LocationData locData, LoginPass loginPass);
    Maybe<StrmAndSize> getFileStream(LocationData locData, LoginPass loginPass);
}
