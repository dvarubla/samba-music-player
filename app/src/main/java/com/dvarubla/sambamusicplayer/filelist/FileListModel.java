package com.dvarubla.sambamusicplayer.filelist;

import com.dvarubla.sambamusicplayer.smbutils.IFileOrFolderItem;
import com.dvarubla.sambamusicplayer.smbutils.ISmbUtils;
import com.dvarubla.sambamusicplayer.smbutils.LocationData;
import com.dvarubla.sambamusicplayer.smbutils.LoginPass;

import javax.inject.Inject;

import io.reactivex.Maybe;

public class FileListModel implements IFileListModel {
    @Inject
    ISmbUtils _smbUtils;

    private LoginPass _lp;

    private LocationData _locData;

    @Inject
    FileListModel(){
        _lp = new LoginPass("", "");
    }

    @Override
    public Maybe<IFileOrFolderItem[]> getFiles() {
        return Maybe.just(new Object()).
                flatMap(o -> _smbUtils.connectToServer(_locData.getServer(), _lp)).
                flatMap(o -> _smbUtils.getFilesFromShare(_locData.getShare(), _locData.getPath()).toMaybe());
    }

    @Override
    public void setLoginPassForServer(String server, LoginPass lp) {
        _lp = lp;
    }

    @Override
    public void setLocationData(LocationData location) {
        _locData = location;
    }
}
