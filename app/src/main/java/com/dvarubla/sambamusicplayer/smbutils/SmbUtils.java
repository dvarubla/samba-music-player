package com.dvarubla.sambamusicplayer.smbutils;

import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation;
import com.hierynomus.mssmb2.SMBApiException;
import com.hierynomus.protocol.commons.Factory;
import com.hierynomus.security.bc.BCSecurityProvider;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.SmbConfig;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.auth.Authenticator;
import com.hierynomus.smbj.auth.NtlmAuthenticator;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

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
    public Single<String[]> getFilesFromShare(final String shareName, final String path) {
        return Single.create((SingleOnSubscribe<String[]>) emitter -> {
            DiskShare share = (DiskShare) _session.connectShare(shareName);
            final ArrayList<String> data = new ArrayList<>();
            for (FileIdBothDirectoryInformation f : share.list(path, "*")) {
                data.add(f.getFileName());
            }
            emitter.onSuccess(data.toArray(new String[0]));
        }).subscribeOn(Schedulers.io());
    }
}
