package com.dvarubla.sambamusicplayer.settings;

import com.dvarubla.sambamusicplayer.smbutils.LocationData;
import com.dvarubla.sambamusicplayer.smbutils.LoginPass;

import java.util.HashMap;

import javax.inject.Inject;

public class LoginPassMan implements ILoginPassMan{
    private HashMap<String, LoginPass> _authData;
    private ISettings _settings;

    @Inject
    LoginPassMan(ISettings settings){
        _settings = settings;
        _authData = _settings.getAuthData();
    }

    @Override
    public LoginPass getLoginPass(LocationData locData) {
        return _authData.get(locData.getServer());
    }

    @Override
    public void setLoginPass(LocationData locData, LoginPass lp) {
        _authData.put(locData.getServer(), lp);
        _settings.saveAuthData(_authData);
    }

    @Override
    public boolean haveLoginPass(LocationData locData) {
        return _authData.containsKey(locData.getServer());
    }
}
