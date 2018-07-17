package com.dvarubla.sambamusicplayer.filelist;

import com.dvarubla.sambamusicplayer.smbutils.LoginPass;

import io.reactivex.Maybe;
import io.reactivex.Observable;

interface IFileListView {
    Maybe<LoginPass> showLoginPassDialog(String server);
    Observable<Object> onFlingLeft();
    Observable<Object> onFlingRight();
}
