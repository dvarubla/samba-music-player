package com.dvarubla.sambamusicplayer.smbutils;

import io.reactivex.Maybe;

public interface ISmbUtils {
    Maybe<IFileOrFolderItem[]> getFilesFromShare(LocationData locData, LoginPass loginPass);
    Maybe<IFileStrm> getFileStream(LocationData locData, LoginPass loginPass);
}
