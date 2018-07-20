package com.dvarubla.sambamusicplayer.player;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;

import com.dvarubla.sambamusicplayer.smbutils.IFileStrm;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.SingleSubject;

import static com.google.android.exoplayer2.Player.DISCONTINUITY_REASON_PERIOD_TRANSITION;
import static com.google.android.exoplayer2.Player.STATE_ENDED;

public class Player implements IPlayer {
    Context _context;

    private ExoPlayer _player;
    private PublishSubject<Object> _needNextSubj;
    private ConcatenatingMediaSource _concatSrc;
    private boolean _firstStopHandled;

    @Inject
    Player(Context context){
        _context = context;
        _needNextSubj = PublishSubject.create();
        DefaultTrackSelector trackSelector =
                new DefaultTrackSelector();
        _player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
        _concatSrc = new ConcatenatingMediaSource();
        _player.prepare(_concatSrc);
        _player.setPlayWhenReady(true);
        _firstStopHandled = false;
        com.google.android.exoplayer2.Player.EventListener _listener = new com.google.android.exoplayer2.Player.DefaultEventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == STATE_ENDED) {
                    if (_firstStopHandled) {
                        _needNextSubj.onNext(new Object());
                    } else {
                        _firstStopHandled = true;
                    }
                }
                super.onPlayerStateChanged(playWhenReady, playbackState);
            }

            @Override
            public void onPositionDiscontinuity(int reason) {
                if (reason == DISCONTINUITY_REASON_PERIOD_TRANSITION) {
                    _needNextSubj.onNext(new Object());
                }
                super.onPositionDiscontinuity(reason);
            }
        };
        _player.addListener(_listener);
    }

    private MediaSource createMediaSource(String ext, IFileStrm strm){
        SambaDataSource.Factory fact = new SambaDataSource.Factory(strm);
        ExtractorMediaSource.Factory srcFact = new ExtractorMediaSource.Factory(fact);
        return srcFact.createMediaSource(Uri.parse("file." + ext));
    }

    @SuppressLint("CheckResult")
    @Override
    public Single<Object> addEnd(String name, IFileStrm strm) {
        SingleSubject<Object> subj = SingleSubject.create();
        _concatSrc.addMediaSource(createMediaSource(name, strm), () -> {
            _player.setPlayWhenReady(true);
            if(_concatSrc.getSize() == 1){
                _player.seekTo(0, C.TIME_UNSET);
            }
            subj.onSuccess(new Object());
        });
        return subj.observeOn(Schedulers.io());
    }

    @Override
    public Single<Object> removeFirst() {
        SingleSubject<Object> subj = SingleSubject.create();
        _concatSrc.removeMediaSource(0, () -> subj.onSuccess(new Object()));
        return subj.observeOn(Schedulers.io());
    }

    @Override
    public boolean isStopped(){
        return _player.getPlayWhenReady();
    }

    @Override
    public Observable<Object> onNeedNext() {
        return _needNextSubj;
    }

    @Override
    public void clear() {
        _player.setPlayWhenReady(false);
        _concatSrc = new ConcatenatingMediaSource();
        _firstStopHandled = false;
        _player.prepare(_concatSrc, true, true);
        _player.setPlayWhenReady(true);
    }
}
