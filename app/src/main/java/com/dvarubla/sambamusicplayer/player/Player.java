package com.dvarubla.sambamusicplayer.player;

import android.content.Context;
import android.net.Uri;

import com.dvarubla.sambamusicplayer.smbutils.IFileStrm;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;

import javax.inject.Inject;

public class Player implements IPlayer {
    Context _context;

    private ExoPlayer _player;
    private boolean _isPlaying;

    @Inject
    Player(Context context){
        _isPlaying = false;
        _context = context;
        DefaultTrackSelector trackSelector =
                new DefaultTrackSelector();
        _player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
    }

    @Override
    public void play(String ext, IFileStrm strm) {
        if(_isPlaying){
            _player.stop();
        }
        _isPlaying = true;
        SambaDataSource.Factory fact = new SambaDataSource.Factory(strm);
        ExtractorMediaSource.Factory srcFact = new ExtractorMediaSource.Factory(fact);
        MediaSource src = srcFact.createMediaSource(Uri.parse("file." + ext));

        _player.prepare(src);
        _player.setPlayWhenReady(true);
    }

    @Override
    public void stop() {
        if(_isPlaying){
            _player.stop();
        }
    }
}
