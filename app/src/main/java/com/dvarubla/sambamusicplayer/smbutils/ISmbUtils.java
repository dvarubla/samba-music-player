package com.dvarubla.sambamusicplayer.smbutils;

import java.io.InputStream;

import io.reactivex.Maybe;
import io.reactivex.Single;

public interface ISmbUtils {
    class StrmAndSize{
        public InputStream strm;
        public long size;
        public StrmAndSize(InputStream strm, long size){
            this.size = size;
            this.strm = strm;
        }
    }
    Maybe<Object> connectToServer(String serverName, LoginPass loginPass);
    Single<IFileOrFolderItem[]> getFilesFromShare(String shareName, String path);
    Single<StrmAndSize> getFileStream(String shareName, String path);
}
