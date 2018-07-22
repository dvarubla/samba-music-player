package com.dvarubla.sambamusicplayer.smbutils;

import com.dvarubla.sambamusicplayer.Util;
import com.hierynomus.smbj.common.SMBRuntimeException;
import com.hierynomus.smbj.share.File;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.SingleSubject;
import io.reactivex.subjects.Subject;

public class FileStrm implements IFileStrm{
    private Subject<Observable<Object>> _subj;
    private File _file;
    private long _offset;
    private int _size;
    private SingleSubject<Object> _fileOpened;
    private FileStrmHelper _helper;

    FileStrm(Subject<Observable<Object>> subj, FileStrmHelper helper, int size){
        _subj = subj;
        _helper = helper;
        _fileOpened = SingleSubject.create();
        _subj.onNext(
            openFile()
        );
        _offset = 0;
        _size = size;
    }

    private Observable<Object> openFile(){
        return _helper.openFile().map(f -> {
            _file = f;
            _fileOpened.onSuccess(new Object());
            return new Object();
        });
    }

    @Override
    public Single<Integer> read(byte[] buf, int bufferOffset, int len) {
        SingleSubject<Integer> subj = SingleSubject.create();
        Single<Integer> ret = subj.cache();
        _subj.onNext(_fileOpened.toObservable().flatMap(o -> Observable.fromCallable(() -> {
            int readLen;
            try {
                readLen = _file.read(buf, _offset, bufferOffset, len);
                _offset += readLen;
            } catch (SMBRuntimeException ex){
                if(Util.getCause(ex) instanceof InterruptedException){
                    readLen = -1;
                } else {
                    throw ex;
                }
            }
            subj.onSuccess(readLen);
            return new Object();
        })).subscribeOn(Schedulers.io()).retryWhen(o -> o.flatMap(a -> openFile()).delay(50, TimeUnit.MILLISECONDS)));
        return ret;
    }

    @Override
    public long getSize() {
        return _size;
    }

    @Override
    public void close() {
        try {
            if(_file != null) {
                _file.close();
            }
        } catch (SMBRuntimeException ignored){
        }
    }
}
