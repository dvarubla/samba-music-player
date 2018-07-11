package com.dvarubla.sambamusicplayer.player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class Server implements IServer{
    private ServerSocket _httpServerSocket;
    private PublishSubject<Object> _stopSubj;
    private Flowable<Object> _flowableSubj;
    private InputStream _strm;
    private String _ext;
    private long _size;
    private HashMap<String, String> _contTypeMap;
    private byte[] _byteBuffer = new byte[65536];

    Server() {
        setContentTypes();
        _stopSubj = PublishSubject.create();
        _flowableSubj = _stopSubj.toFlowable(BackpressureStrategy.BUFFER);
        try {
            _httpServerSocket = new ServerSocket(PORT);
            Observable.just(new Object()).observeOn(Schedulers.io()).map(
                    o -> _httpServerSocket.accept()
            ).flatMap(socket -> processSocket(socket, _strm, _size, _ext)).repeat().subscribe();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setPlayData(String ext, InputStream strm, long size) {
        if(_strm != null){
            _stopSubj.onNext(new Object());
        }
        _ext = ext;
        _size = size;
        _strm = strm;
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

    private Observable<Object> processSocket(Socket socket, InputStream strm, long size, String ext) throws IOException {
        OutputStream ostrm = socket.getOutputStream();
        return Observable.just(new Object()).map( o -> {
            BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter os = new PrintWriter(socket.getOutputStream(), true);
            //noinspection StatementWithEmptyBody
            while (!is.readLine().equals(""));
            os.print("HTTP/1.1 200 OK" + "\r\n");
            os.print("Content-Type: " + getContentType(ext) + "\r\n");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            os.print("Date: " + simpleDateFormat.format(new Date()) + "\r\n");
            os.print("Server: SambaMusicPlayer\r\n");
            os.print("Accept-Ranges: bytes\r\n");
            os.print("Content-Length: " + size + "\r\n");
            os.print("Connection: Keep-Alive\r\n");
            os.print("\r\n");
            os.flush();
            return new Object();
        }).flatMap(o -> copyBytes(strm, ostrm, socket));
    }

    private Observable<Object> copyBytes(InputStream strm, OutputStream ostrm, Socket socket){
        return Observable.just(new Object()).repeatWhen(completed -> completed.flatMap(o -> {
            int len = strm.read(_byteBuffer);
            if(len == -1){
                socket.close();
                return Observable.empty();
            }
            ostrm.write(_byteBuffer, 0, len);
            ostrm.flush();
            return Observable.just(new Object());
        }).delay(40, TimeUnit.MILLISECONDS).takeUntil(
                _flowableSubj.toObservable()
        ).switchIfEmpty(emitter -> {
            try {
                socket.close();
                strm.close();
            } catch (IOException e) {
                emitter.onError(e);
            }
            emitter.onComplete();
        }));
    }
}
