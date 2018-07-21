package com.dvarubla.sambamusicplayer.smbutils;

import android.annotation.SuppressLint;

import com.hierynomus.msfscc.FileAttributes;
import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation;
import com.hierynomus.msfscc.fileinformation.FileStandardInformation;
import com.hierynomus.mssmb2.SMBApiException;
import com.hierynomus.security.bc.BCSecurityProvider;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.SmbConfig;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.auth.NtlmAuthenticator;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.MaybeSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class SmbUtils implements ISmbUtils {
    private static final int MAX_RETRIES = 6;
    private static class ServerAndShare{
        String server;
        String share;

        @Override
        public int hashCode() {
            return server.hashCode() << 6 | share.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ServerAndShare &&
                    server.equals(((ServerAndShare) obj).server) && share.equals(((ServerAndShare) obj).share);
        }

        ServerAndShare(String server, String share){
            this.server = server;
            this.share = share;
        }
    }

    private SMBClient _client;
    private HashMap<String, Connection> _connections;
    private HashMap<String, Session> _sessions;
    private HashMap<ServerAndShare, DiskShare> _shares;
    private Subject<Observable<Object>> _quantumSubj;

    @SuppressLint("CheckResult")
    SmbUtils(){
        SmbConfig config = SmbConfig.builder().withAuthenticators(Collections.singletonList(new NtlmAuthenticator.Factory()))
                .withSecurityProvider(new BCSecurityProvider()).build();
        _client = new SMBClient(config);
        _connections = new HashMap<>();
        _sessions = new HashMap<>();
        _shares = new HashMap<>();
        _quantumSubj = PublishSubject.<Observable<Object>>create().toSerialized();
        _quantumSubj.concatMap(e -> e).subscribe();
    }

    private void clearConnData(){
        _shares.clear();
        _sessions.clear();
        _connections.clear();
    }

    <T1> ObservableTransformer<T1, T1> composeNet(){
        return input -> input.subscribeOn(Schedulers.io()).retryWhen(o -> o.zipWith(Observable.range(1, MAX_RETRIES), (n, i) -> {
            if(i == MAX_RETRIES){
                return Observable.error(n);
            } else {
                return Observable.timer(50 * i, TimeUnit.MILLISECONDS);
            }
        }).flatMap(a -> a)).retryWhen(o -> o.map(a -> {
            clearConnData();
            return a;
        }));
    }

    DiskShare checkShare(LocationData locData, LoginPass loginPass) throws IOException {
        if(!_connections.containsKey(locData.getServer())){
            _connections.put(locData.getServer(), _client.connect(locData.getServer()));
        }
        if (!_sessions.containsKey(locData.getServer())) {
            Connection conn = _connections.get(locData.getServer());
            AuthenticationContext ac = new AuthenticationContext(loginPass.getLogin(), loginPass.getPass().toCharArray(), "");
            _sessions.put(locData.getServer(), conn.authenticate(ac));
        }
        if (!_shares.containsKey(new ServerAndShare(locData.getServer(), locData.getShare()))) {
            Session sess = _sessions.get(locData.getServer());
            _shares.put(
                    new ServerAndShare(locData.getServer(), locData.getShare()),
                    (DiskShare) sess.connectShare(locData.getShare())
            );
        }
        return _shares.get(new ServerAndShare(locData.getServer(), locData.getShare()));
    }

    @Override
    public Maybe<IFileOrFolderItem[]> getFilesFromShare(LocationData locData, LoginPass loginPass) {
        MaybeSubject<IFileOrFolderItem[]> subj = MaybeSubject.create();
        Maybe<IFileOrFolderItem[]> ret = subj.cache();
        _quantumSubj.onNext(Observable.fromCallable( () -> {
            try {
                DiskShare share = checkShare(locData, loginPass);
                final ArrayList<IFileOrFolderItem> dirData = new ArrayList<>();
                final ArrayList<IFileOrFolderItem> fileData = new ArrayList<>();
                for (FileIdBothDirectoryInformation f : share.list(locData.getPath())) {
                    if (!(f.getFileName().equals(".") || f.getFileName().equals(".."))) {
                        if ((f.getFileAttributes() & FileAttributes.FILE_ATTRIBUTE_DIRECTORY.getValue()) != 0) {
                            dirData.add(new FolderItem(f.getFileName()));
                        } else if (f.getFileName().matches(".*\\.(?:mp3|flac|wav|aac|m4a)$")) {
                            fileData.add(new FileItem(f.getFileName()));
                        }
                    }
                }
                Collections.sort(dirData, (it1, it2) -> it1.getName().compareTo(it2.getName()));
                Collections.sort(fileData, (it1, it2) -> it1.getName().compareTo(it2.getName()));
                dirData.addAll(fileData);
                subj.onSuccess(dirData.toArray(new IFileOrFolderItem[0]));
            } catch (SMBApiException exc){
                subj.onComplete();
            }
            return new Object();
        }).compose(composeNet()));
        return ret.observeOn(Schedulers.io());
    }

    @Override
    public Maybe<IFileStrm> getFileStream(LocationData locData, LoginPass loginPass) {
        MaybeSubject<IFileStrm> subj = MaybeSubject.create();
        Maybe<IFileStrm> ret = subj.cache();
        _quantumSubj.onNext(Observable.fromCallable( () -> {
            DiskShare share = checkShare(locData, loginPass);
            FileStandardInformation info = share.getFileInformation(locData.getPath(), FileStandardInformation.class);
            subj.onSuccess(new FileStrm(
                    _quantumSubj,
                    new FileStrmHelper(this, locData, loginPass),
                    (int) info.getEndOfFile()
            ));
            return new Object();
        }).compose(composeNet()));
        return ret.observeOn(Schedulers.io());
    }
}
