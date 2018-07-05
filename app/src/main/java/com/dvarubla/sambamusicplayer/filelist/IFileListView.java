package com.dvarubla.sambamusicplayer.filelist;

import com.dvarubla.sambamusicplayer.smbutils.LoginPass;

import io.reactivex.Maybe;

interface IFileListView {
    Maybe<LoginPass> showLoginPassDialog(String server);
}
