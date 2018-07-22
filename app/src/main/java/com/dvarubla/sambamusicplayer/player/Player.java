package com.dvarubla.sambamusicplayer.player;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.os.RemoteException;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.dvarubla.sambamusicplayer.smbutils.IFileStrm;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.SingleSubject;

import static android.media.AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
import static com.google.android.exoplayer2.Player.DISCONTINUITY_REASON_PERIOD_TRANSITION;
import static com.google.android.exoplayer2.Player.STATE_ENDED;
import static com.google.android.exoplayer2.Player.STATE_READY;

public class Player implements IPlayer {
    Context _context;

    private final PlaybackStateCompat.Builder _stateBuilder = new PlaybackStateCompat.Builder().setActions(
            PlaybackStateCompat.ACTION_PLAY
                    | PlaybackStateCompat.ACTION_STOP
                    | PlaybackStateCompat.ACTION_PAUSE
                    | PlaybackStateCompat.ACTION_PLAY_PAUSE
                    | PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                    | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
    );

    private MediaSessionCompat _mediaSession;
    private MediaControllerCompat _controller;
    private AudioManager _audioManager;

    private ExoPlayer _player;
    private PublishSubject<Object> _needNextSubj;
    private PublishSubject<Object> _onStopSubj;
    private ConcatenatingMediaSource _concatSrc;
    private boolean _firstStopHandled;
    private SongMData _songMData;

    private AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = focusChange -> {
        if(focusChange != AudioManager.AUDIOFOCUS_GAIN){
            _controller.getTransportControls().stop();
            _onStopSubj.onNext(new Object());
        }
    };

    @Inject
    Player(Context context){
        _context = context;
        _needNextSubj = PublishSubject.create();
        _onStopSubj = PublishSubject.create();
        DefaultTrackSelector trackSelector =
                new DefaultTrackSelector();
        _player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
        _concatSrc = new ConcatenatingMediaSource();
        _player.prepare(_concatSrc);
        _player.setPlayWhenReady(false);
        _firstStopHandled = false;
        com.google.android.exoplayer2.Player.EventListener eventListener = new com.google.android.exoplayer2.Player.DefaultEventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == STATE_ENDED) {
                    if (_firstStopHandled) {
                        _needNextSubj.onNext(new Object());
                    } else {
                        _firstStopHandled = true;
                    }
                    _controller.getTransportControls().stop();
                } else if(playbackState == STATE_READY){
                    getMetadata();
                    _controller.getTransportControls().play();
                }
                super.onPlayerStateChanged(playWhenReady, playbackState);
            }

            @Override
            public void onPositionDiscontinuity(int reason) {
                if (reason == DISCONTINUITY_REASON_PERIOD_TRANSITION) {
                    _needNextSubj.onNext(new Object());
                    getMetadata();
                    _controller.getTransportControls().play();
                }
                super.onPositionDiscontinuity(reason);
            }
        };
        _player.addListener(eventListener);

        _mediaSession = new MediaSessionCompat(_context, "Samba Music Player");
        _mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        _mediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                if(_songMData != null) {
                    MediaMetadataCompat.Builder metadataBuilder = new MediaMetadataCompat.Builder();
                    metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, _songMData.getTrackName());
                    metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, _songMData.getArtist());
                    if(_songMData.getAlbum() != null){
                        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, _songMData.getAlbum());
                    }
                    metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, _songMData.getDuration());
                    _mediaSession.setMetadata(metadataBuilder.build());
                }
                _mediaSession.setActive(true);
                _mediaSession.setPlaybackState(
                        _stateBuilder.setState(PlaybackStateCompat.STATE_PLAYING, _player.getCurrentPosition(), 1).build()
                );
            }

            @Override
            public void onStop() {
                _mediaSession.setActive(false);
                _mediaSession.setPlaybackState(_stateBuilder.setState(PlaybackStateCompat.STATE_STOPPED, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1).build());
            }
        });
        try {
            _controller = new MediaControllerCompat(_context, _mediaSession.getSessionToken());
        } catch (RemoteException ignored) {
        }

        _audioManager = (AudioManager) _context.getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public void stop() {
        _controller.getTransportControls().stop();
        _player.setPlayWhenReady(false);
    }

    @Override
    public boolean play() {
        if(_audioManager.requestAudioFocus(audioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
                != AUDIOFOCUS_REQUEST_GRANTED
        ){
            return false;
        } else {
            _player.setPlayWhenReady(true);
            return true;
        }
    }

    private void getMetadata(){
        TrackGroupArray trackGroups = _player.getCurrentTrackGroups();
        Metadata mdata = null;
        for (int i = 0; i < trackGroups.length; i++) {
            TrackGroup group = trackGroups.get(i);
            for (int j = 0; j < group.length; j++) {
                mdata = group.getFormat(j).metadata;
                if (mdata != null) {
                    break;
                }
            }
            if (mdata != null) {
                break;
            }
        }
        if (mdata != null) {
            String artist = null;
            String trackName = null;
            String album = null;
            for (int i = 0; i < mdata.length(); i++) {
                String str = mdata.get(i).toString();
                if (str.startsWith("TIT2:")) {
                    trackName = str.substring(str.indexOf('=') + 1);
                } else if (str.startsWith("TPE1:")) {
                    artist = str.substring(str.indexOf('=') + 1);
                } else if (str.startsWith("TALB:")){
                    album = str.substring(str.indexOf('=') + 1);
                }
            }
            if (artist != null && trackName != null) {
                long dur = _player.getDuration();
                _songMData = new SongMData(artist, trackName, album, dur);
            }
        }
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
        boolean needStop = _player.getPlayWhenReady();
        if(needStop) {
            _player.setPlayWhenReady(false);
        }
        _concatSrc = new ConcatenatingMediaSource();
        _firstStopHandled = false;
        _player.prepare(_concatSrc, true, true);
        if(needStop) {
            _player.setPlayWhenReady(true);
        }
    }

    @Override
    public Observable<Object> onStop() {
        return _onStopSubj;
    }

    @Override
    public void onExit() {
        _controller.getTransportControls().stop();
    }
}
