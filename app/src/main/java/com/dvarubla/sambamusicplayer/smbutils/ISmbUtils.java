package com.dvarubla.sambamusicplayer.smbutils;

import io.reactivex.Maybe;
import io.reactivex.Single;

public interface ISmbUtils {
    Maybe<Object> connectToServer(String serverName, LoginPass loginPass);
    Single<String[]> getFilesFromShare(String shareName, String path);
}
