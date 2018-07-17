package com.dvarubla.sambamusicplayer.settings;

import com.dvarubla.sambamusicplayer.smbutils.LocationData;
import com.dvarubla.sambamusicplayer.smbutils.LoginPass;

public interface ILoginPassMan {
    LoginPass getLoginPass(LocationData locData);
    boolean haveLoginPass(LocationData locData);
    void setLoginPass(LocationData locData, LoginPass lp);
}
