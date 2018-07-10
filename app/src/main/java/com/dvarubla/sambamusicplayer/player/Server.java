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
    private long _size;

    Server() {
        _stopSubj = PublishSubject.create();
        _flowableSubj = _stopSubj.toFlowable(BackpressureStrategy.BUFFER);
        try {
            _httpServerSocket = new ServerSocket(PORT);
            Observable.just(new Object()).observeOn(Schedulers.io()).map(
                    o -> _httpServerSocket.accept()
            ).flatMap(socket -> processSocket(socket, _strm, _size)).repeat().subscribe();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setPlayData(InputStream strm, long size) {
        if(_strm != null){
            _stopSubj.onNext(new Object());
        }
        _size = size;
        _strm = strm;
    }

    private Observable<Object> processSocket(Socket socket, InputStream strm, long size) throws IOException {
        OutputStream ostrm = socket.getOutputStream();
        return Observable.just(new Object()).map( o -> {
            BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter os = new PrintWriter(socket.getOutputStream(), true);
            //noinspection StatementWithEmptyBody
            while (!is.readLine().equals(""));
            os.print("HTTP/1.1 200 OK" + "\r\n");
            os.print("Content-Type: audio/mpeg" + "\r\n");
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
        byte[] arr = new byte[4096];
        return Observable.just(new Object()).repeatWhen(completed -> completed.flatMap(o -> {
            int len = strm.read(arr);
            if(len == -1){
                socket.close();
                return Observable.empty();
            }
            ostrm.write(arr, 0, len);
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
