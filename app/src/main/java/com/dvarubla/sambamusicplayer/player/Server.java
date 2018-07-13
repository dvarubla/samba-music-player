package com.dvarubla.sambamusicplayer.player;

import com.dvarubla.sambamusicplayer.smbutils.IFileStrm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class Server implements IServer{
    private enum MsgType{
        CONTINUE, STOP, START, FILE_FINISH
    }
    private ServerSocket _httpServerSocket;
    private Subject<MsgType> _statusSubj;
    private Subject<Object> _onStopSubj;
    private Subject<Object> _onStartSubj;
    private Subject<Object> _onFileFinishSubj;
    private IFileStrm _strm;
    private String _ext;
    private HashMap<String, String> _contTypeMap;
    private final static int BUFFER_SIZE = 1024 * 100;
    private byte[] _byteBuffer = new byte[BUFFER_SIZE];

    static class State{
        Socket socket;
        public enum Val{
            STARTED, ACTIVE, STOPPED, FILE_FINISHED
        }
        Val val;
    }

    Server() {
        setContentTypes();
        _statusSubj = PublishSubject.<MsgType>create().toSerialized();
        _onStopSubj = PublishSubject.create();
        _onStartSubj = PublishSubject.create();
        _onFileFinishSubj = PublishSubject.create();
        try {
            _httpServerSocket = new ServerSocket(PORT);
            State state = new State();
            state.val = State.Val.STOPPED;
            Observable.defer( () -> {
                switch (state.val){
                    case STARTED:
                        state.socket = _httpServerSocket.accept();
                        state.val = State.Val.ACTIVE;
                        return Observable.just(state).map(this::processSocket);
                    case ACTIVE:
                        return Observable.just(state);
                }
                return Observable.empty();
            }).
            flatMap(
                    o -> this.copyBytes(state)
            ).repeatWhen(c -> c.zipWith(_statusSubj.observeOn(Schedulers.io()), (a, b) -> b).flatMap(el -> {
                switch (el) {
                    case STOP:
                        if (state.val == State.Val.ACTIVE) {
                            finish(state);
                            _onFileFinishSubj.onNext(new Object());
                            _onStopSubj.onNext(new Object());
                            state.val = State.Val.STOPPED;
                        } else if (state.val == State.Val.FILE_FINISHED) {
                            _onStopSubj.onNext(new Object());
                            state.val = State.Val.STOPPED;
                        }
                        break;
                    case START:
                        if (state.val == State.Val.STOPPED) {
                            state.val = State.Val.STARTED;
                            _onStartSubj.onNext(new Object());
                        }
                        break;
                    case FILE_FINISH:
                        finish(state);
                        _onFileFinishSubj.onNext(new Object());
                        state.val = State.Val.FILE_FINISHED;
                        break;
                }
                if(el == MsgType.CONTINUE) {
                    return Observable.just(el).delay(100, TimeUnit.MILLISECONDS);
                } else {
                    return Observable.just(el);
                }
            })).subscribeOn(Schedulers.io()).subscribe();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setPlayData(String ext, IFileStrm strm) {
        _ext = ext;
        _strm = strm;
    }

    @Override
    public void stop() {
        _statusSubj.onNext(MsgType.STOP);
    }

    @Override
    public void start() {
        _statusSubj.onNext(MsgType.START);
    }

    @Override
    public Observable<Object> onStop() {
        return _onStopSubj;
    }

    @Override
    public Observable<Object> onFileFinish() {
        return _onFileFinishSubj;
    }

    @Override
    public Observable<Object> onStart() {
        return _onStartSubj;
    }

    private void setContentTypes(){
        _contTypeMap = new HashMap<>();
        _contTypeMap.put("mp3", "audio/mpeg");
        _contTypeMap.put("flac", "audio/flac");
        _contTypeMap.put("wav", "audio/wav");
        _contTypeMap.put("aac", "audio/aac");
        _contTypeMap.put("m4a", "audio/aac");
    }

    private String getContentType(String str){
        return _contTypeMap.get(str);
    }

    private State processSocket(State state) throws IOException {
        BufferedReader is = new BufferedReader(new InputStreamReader(state.socket.getInputStream()));
        PrintWriter os = new PrintWriter(state.socket.getOutputStream(), true);
        //noinspection StatementWithEmptyBody
        while (!is.readLine().equals(""));
        os.print("HTTP/1.1 200 OK" + "\r\n");
        os.print("Content-Type: " + getContentType(_ext) + "\r\n");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        os.print("Date: " + simpleDateFormat.format(new Date()) + "\r\n");
        os.print("Server: SambaMusicPlayer\r\n");
        os.print("Accept-Ranges: bytes\r\n");
        os.print("Content-Length: " + _strm.getSize() + "\r\n");
        os.print("Connection: Keep-Alive\r\n");
        os.print("\r\n");
        os.flush();
        return state;
    }

    private Observable<Object> copyBytes(State state) throws IOException {
        OutputStream ostrm = state.socket.getOutputStream();
        return _strm.read(_byteBuffer, BUFFER_SIZE).map(len -> {
            if(len != -1){
                try {
                    ostrm.write(_byteBuffer, 0, len);
                    ostrm.flush();
                    _statusSubj.onNext(MsgType.CONTINUE);
                } catch (SocketException exc){
                    _statusSubj.onNext(MsgType.FILE_FINISH);
                }
            } else {
                _statusSubj.onNext(MsgType.FILE_FINISH);
            }
            return new Object();
        }).toObservable();
    }

    private void finish(State state) throws IOException {
        state.socket.close();
    }
}
