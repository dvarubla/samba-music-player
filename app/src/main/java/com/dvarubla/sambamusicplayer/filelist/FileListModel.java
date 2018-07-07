package com.dvarubla.sambamusicplayer.filelist;

import com.dvarubla.sambamusicplayer.settings.ISettings;
import com.dvarubla.sambamusicplayer.smbutils.IFileOrFolderItem;
import com.dvarubla.sambamusicplayer.smbutils.ISmbUtils;
import com.dvarubla.sambamusicplayer.smbutils.LocationData;
import com.dvarubla.sambamusicplayer.smbutils.LoginPass;

import java.util.HashMap;

import javax.inject.Inject;

import io.reactivex.Maybe;

public class FileListModel implements IFileListModel {
    @Inject
    ISmbUtils _smbUtils;

    private HashMap<String, LoginPass> _authData;

    private LocationData _locData;

    private ISettings _settings;

    @Inject
    FileListModel(ISettings settings){
        _settings = settings;
        _authData = _settings.getAuthData();
    }

    @Override
    public Maybe<IFileOrFolderItem[]> getFiles() {
        return Maybe.just(new Object()).
                flatMap(o -> {
                    if(_authData.containsKey(_locData.getServer())){
                        return _smbUtils.connectToServer(_locData.getServer(), _authData.get(_locData.getServer()));
                    } else {
                        return _smbUtils.connectToServer(_locData.getServer(), new LoginPass("", ""));
                    }
                }).
                flatMap(o -> _smbUtils.getFilesFromShare(_locData.getShare(), _locData.getPath()).toMaybe());
    }

    @Override
    public void setLoginPassForServer(String server, LoginPass lp) {
        _authData.put(server, lp);
        _settings.saveAuthData(_authData);
    }

    @Override
    public void setLocationData(LocationData location) {
        _locData = location;
    }
}
