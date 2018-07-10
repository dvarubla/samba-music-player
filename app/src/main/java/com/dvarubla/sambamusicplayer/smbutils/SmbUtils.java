package com.dvarubla.sambamusicplayer.smbutils;

import com.hierynomus.msdtyp.AccessMask;
import com.hierynomus.msfscc.FileAttributes;
import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation;
import com.hierynomus.msfscc.fileinformation.FileStandardInformation;
import com.hierynomus.mssmb2.SMB2CreateDisposition;
import com.hierynomus.mssmb2.SMB2ShareAccess;
import com.hierynomus.mssmb2.SMBApiException;
import com.hierynomus.security.bc.BCSecurityProvider;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.SmbConfig;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.auth.NtlmAuthenticator;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;
import com.hierynomus.smbj.share.File;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;

import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.schedulers.Schedulers;

public class SmbUtils implements ISmbUtils {
    private SMBClient _client;
    private Connection _connection;
    private Session _session;

    SmbUtils(){
        SmbConfig config = SmbConfig.builder().withAuthenticators(Collections.singletonList(new NtlmAuthenticator.Factory()))
                .withSecurityProvider(new BCSecurityProvider()).build();
        _client = new SMBClient(config);
    }
    @Override
    public Maybe<Object> connectToServer(final String serverName, final LoginPass loginPass) {
        return Maybe.create(emitter -> {
            try {
                _connection = _client.connect(serverName);
                AuthenticationContext ac = new AuthenticationContext(loginPass.getLogin(), loginPass.getPass().toCharArray(), "");
                _session = _connection.authenticate(ac);
                emitter.onSuccess(new Object());
            } catch (IOException e) {
                emitter.onError(e);
            } catch (SMBApiException e){
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Single<IFileOrFolderItem[]> getFilesFromShare(final String shareName, final String path) {
        return Single.create((SingleOnSubscribe<IFileOrFolderItem[]>) emitter -> {
            DiskShare share = (DiskShare) _session.connectShare(shareName);
            final ArrayList<IFileOrFolderItem> dirData = new ArrayList<>();
            final ArrayList<IFileOrFolderItem> fileData = new ArrayList<>();
            for (FileIdBothDirectoryInformation f : share.list(path)) {
                if(! (f.getFileName().equals(".") || f.getFileName().equals("..")) ) {
                    if ((f.getFileAttributes() & FileAttributes.FILE_ATTRIBUTE_DIRECTORY.getValue()) != 0) {
                        dirData.add(new FolderItem(f.getFileName()));
                    } else if(f.getFileName().matches(".*\\.mp3$")){
                        fileData.add(new FileItem(f.getFileName()));
                    }
                }
            }
            dirData.addAll(fileData);
            emitter.onSuccess(dirData.toArray(new IFileOrFolderItem[0]));
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Single<StrmAndSize> getFileStream(String shareName, String path) {
        return Single.<StrmAndSize>create(emitter -> {
            try {
                DiskShare share = (DiskShare) _session.connectShare(shareName);
                FileStandardInformation info = share.getFileInformation(path, FileStandardInformation.class);
                File file = share.openFile(path,
                        EnumSet.of(AccessMask.GENERIC_READ), null, SMB2ShareAccess.ALL,
                        SMB2CreateDisposition.FILE_OPEN, null);
                InputStream input = file.getInputStream();
                emitter.onSuccess(new StrmAndSize(input, info.getEndOfFile()));
            } catch(Exception e){
                e.printStackTrace();
            }
        }).subscribeOn(Schedulers.io());
    }
}
