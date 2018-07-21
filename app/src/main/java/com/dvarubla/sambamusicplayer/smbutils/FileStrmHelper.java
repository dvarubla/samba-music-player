package com.dvarubla.sambamusicplayer.smbutils;

import com.hierynomus.msdtyp.AccessMask;
import com.hierynomus.mssmb2.SMB2CreateDisposition;
import com.hierynomus.mssmb2.SMB2CreateOptions;
import com.hierynomus.mssmb2.SMB2ShareAccess;
import com.hierynomus.smbj.share.DiskShare;
import com.hierynomus.smbj.share.File;

import java.util.Collections;
import java.util.EnumSet;

import io.reactivex.Observable;

public class FileStrmHelper {
    private LocationData _locData;
    private SmbUtils _smbUtils;
    private LoginPass _loginPass;
    FileStrmHelper(SmbUtils smbUtils, LocationData locData, LoginPass loginPass){
        _smbUtils = smbUtils;
        _locData = locData;
        _loginPass = loginPass;
    }

    Observable<File> openFile() {
        return Observable.fromCallable( () -> {
            DiskShare share = _smbUtils.checkShare(_locData, _loginPass);
            return share.openFile(_locData.getPath(),
                    EnumSet.of(AccessMask.GENERIC_READ), null, SMB2ShareAccess.ALL,
                    SMB2CreateDisposition.FILE_OPEN, Collections.singleton(SMB2CreateOptions.FILE_SEQUENTIAL_ONLY));
            }
        ).compose(_smbUtils.composeNet());
    }
}
