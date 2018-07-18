package com.dvarubla.sambamusicplayer.player;

import android.content.Context;
import android.net.Uri;

import com.dvarubla.sambamusicplayer.smbutils.IFileStrm;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

import static com.google.android.exoplayer2.Player.STATE_ENDED;

public class Player implements IPlayer {
    Context _context;

    private ExoPlayer _player;
    private AtomicBoolean _isPlaying;
    private PublishSubject<Object> _stopSubj;
    private com.google.android.exoplayer2.Player.EventListener _listener;

    @Inject
    Player(Context context){
        _isPlaying = new AtomicBoolean(false);
        _context = context;
        _stopSubj = PublishSubject.create();
        DefaultTrackSelector trackSelector =
                new DefaultTrackSelector();
        _player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
        _listener = new  com.google.android.exoplayer2.Player.DefaultEventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if(playbackState == STATE_ENDED){
                    _isPlaying.set(false);
                    _stopSubj.onNext(new Object());
                }
                super.onPlayerStateChanged(playWhenReady, playbackState);
            }
        };
    }

    @Override
    public void play(String ext, IFileStrm strm) {
        stop();
        _isPlaying.set(true);
        SambaDataSource.Factory fact = new SambaDataSource.Factory(strm);
        ExtractorMediaSource.Factory srcFact = new ExtractorMediaSource.Factory(fact);
        MediaSource src = srcFact.createMediaSource(Uri.parse("file." + ext));
        _player.addListener(_listener);
        _player.prepare(src);
        _player.setPlayWhenReady(true);
    }

    @Override
    public void stop() {
        if(_isPlaying.get()){
            _player.removeListener(_listener);
            _player.stop();
        }
    }

    @Override
    public boolean isPlaying(){
        return _isPlaying.get();
    }

    @Override
    public Observable<Object> onStop() {
        return _stopSubj;
    }
}
