package com.dvarubla.sambamusicplayer.smbutils;

import com.hierynomus.smbj.share.File;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.subjects.SingleSubject;
import io.reactivex.subjects.Subject;

public class FileStrm implements IFileStrm{
    private Subject<Observable<Object>> _subj;
    private File _file;
    private long _offset;
    private int _size;

    FileStrm(Subject<Observable<Object>> subj, File file, int size){
        _subj = subj;
        _file = file;
        _offset = 0;
        _size = size;
    }

    @Override
    public Single<Integer> read(byte[] buf, int len) {
        SingleSubject<Integer> subj = SingleSubject.create();
        Single<Integer> ret = subj.cache();
        _subj.onNext(Observable.fromCallable(() -> {
            int readLen = _file.read(buf, _offset, 0, len);
            _offset += readLen;
            subj.onSuccess(readLen);
            return new Object();
        }));
        return ret;
    }

    @Override
    public long getSize() {
        return _size;
    }

    @Override
    public void close() {
        _file.close();
    }
}
