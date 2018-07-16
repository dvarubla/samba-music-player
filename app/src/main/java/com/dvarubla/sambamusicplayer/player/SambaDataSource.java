package com.dvarubla.sambamusicplayer.player;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.dvarubla.sambamusicplayer.smbutils.IFileStrm;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;

import static com.google.android.exoplayer2.C.RESULT_END_OF_INPUT;

class SambaDataSource implements DataSource{

    public static class Factory implements DataSource.Factory{
        private IFileStrm _strm;
        public Factory(IFileStrm strm){
            _strm = strm;
        }

        @Override
        public DataSource createDataSource() {
            return new SambaDataSource(_strm);
        }
    }

    private class BufferAndSize{
        byte[] buffer;
        int size;
        BufferAndSize(byte[] _buffer, int _size){
            buffer = _buffer;
            size = _size;
        }
    }

    private IFileStrm _strm;
    private int _size;
    private final int BUFFER_SIZE = 1024 * 256;
    private int _bufOffset;
    private BufferAndSize _buffer;
    private Uri _uri;

    private SambaDataSource(IFileStrm strm){
        _strm = strm;
        _size = (int) strm.getSize();
        _buffer = new BufferAndSize(new byte[BUFFER_SIZE], 0);
        _bufOffset = 0;
    }

    @Override
    public long open(DataSpec dataSpec) {
        _uri = dataSpec.uri;
        return _size;
    }

    @SuppressLint("CheckResult")
    @Override
    public int read(byte[] buffer, int offset, int readLength) {
        if(_buffer.size == -1){
            return RESULT_END_OF_INPUT;
        }
        int prevRead = 0;
        if(_bufOffset + readLength > _buffer.size){
            int delta = _buffer.size - _bufOffset;
            if(delta != 0) {
                System.arraycopy(_buffer.buffer, _bufOffset, buffer, offset, delta);
                offset += delta;
                readLength -= delta;
                prevRead = delta;
            }
            _buffer.size = _strm.read(_buffer.buffer, 0, BUFFER_SIZE).blockingGet();
            if(_buffer.size == -1){
                if(delta != 0){
                    return delta;
                }
                return RESULT_END_OF_INPUT;
            }
            _bufOffset = 0;
        }
        int curRead = Math.min(readLength, _buffer.size - _bufOffset);
        System.arraycopy(_buffer.buffer, _bufOffset, buffer, offset, curRead);
        _bufOffset += curRead;
        return prevRead + curRead;
    }

    @Nullable
    @Override
    public Uri getUri() {
        return _uri;
    }

    @Override
    public void close() {
        _strm.close();
    }
}
