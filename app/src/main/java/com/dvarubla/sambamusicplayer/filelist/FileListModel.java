package com.dvarubla.sambamusicplayer.filelist;

import com.dvarubla.sambamusicplayer.smbutils.ISmbUtils;
import com.dvarubla.sambamusicplayer.smbutils.LocationData;
import com.dvarubla.sambamusicplayer.smbutils.LoginPass;

import javax.inject.Inject;

import io.reactivex.Maybe;

public class FileListModel implements IFileListModel {
    @Inject
    ISmbUtils _smbUtils;

    private LoginPass _lp;

    @Inject
    FileListModel(){
        _lp = new LoginPass("", "");
    }

    @Override
    public Maybe<String[]> getFiles(final LocationData location) {
        return Maybe.just(new Object()).
                flatMap(o -> _smbUtils.connectToServer(location.getServer(), _lp)).
                flatMap(o -> _smbUtils.getFilesFromShare(location.getShare(), location.getPath()).toMaybe());
    }

    @Override
    public void setLoginPassForServer(String server, LoginPass lp) {
        _lp = lp;
    }
}
