package com.dvarubla.sambamusicplayer.playlist;

import com.dvarubla.sambamusicplayer.player.IPlayer;
import com.dvarubla.sambamusicplayer.settings.ILoginPassMan;
import com.dvarubla.sambamusicplayer.smbutils.IFileStrm;
import com.dvarubla.sambamusicplayer.smbutils.ISmbUtils;
import com.dvarubla.sambamusicplayer.smbutils.LocationData;
import com.dvarubla.sambamusicplayer.smbutils.LoginPass;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PlaylistBasicT {
    private Playlist _playlist;

    @Inject
    ISmbUtils _smbUtils;

    @Inject
    IPlayer _player;

    @Inject
    ILoginPassMan _lpman;

    private IFileStrm _strm;

    private PlaylistComponentT _comp;

    @Before
    public void before(){
        _comp = DaggerPlaylistComponentT.builder().build();
        _comp.inject(this);
        _strm = Mockito.mock(IFileStrm.class);
        RxJavaPlugins.setIoSchedulerHandler(o -> Schedulers.trampoline());
    }

    @Test
    public void testAddFile() {
        LoginPass testLoginPass = new LoginPass("Login", "Pass");
        LocationData fileLoc = new LocationData("TEST/test2/file.mp3");
        when(_player.onNeedNext()).thenReturn(Observable.empty());
        when(_player.onStop()).thenReturn(Observable.empty());
        when(_player.canPlay()).thenReturn(true);
        when(_player.addEnd(any(), any())).thenReturn(Single.just(new Object()));
        when(_lpman.getLoginPass(fileLoc)).thenReturn(testLoginPass);
        when(_smbUtils.getFileStream(fileLoc, testLoginPass)).thenReturn(Maybe.just(_strm));

        _playlist = _comp.getPlaylist();
        _playlist.setPlaying(true);
        _playlist.addFile(fileLoc);
        verify(_player, times(1)).addEnd("mp3", _strm);
    }

    @Test
    public void testAddFileTwice() {
        LoginPass testLoginPass = new LoginPass("Login", "Pass");
        LocationData fileLoc = new LocationData("TEST/test2/file.mp3");
        LocationData fileLoc2 = new LocationData("TEST/test2/file2.mp3");
        when(_player.onNeedNext()).thenReturn(Observable.empty());
        when(_player.onStop()).thenReturn(Observable.empty());
        when(_player.canPlay()).thenReturn(true);
        when(_player.addEnd(any(), any())).thenReturn(Single.just(new Object()));
        when(_lpman.getLoginPass(fileLoc)).thenReturn(testLoginPass);
        when(_lpman.getLoginPass(fileLoc2)).thenReturn(testLoginPass);
        when(_smbUtils.getFileStream(fileLoc, testLoginPass)).thenReturn(Maybe.just(_strm));
        when(_smbUtils.getFileStream(fileLoc2, testLoginPass)).thenReturn(Maybe.just(_strm));

        _playlist = _comp.getPlaylist();
        _playlist.setPlaying(true);
        _playlist.addFile(fileLoc);
        _playlist.addFile(fileLoc2);
        verify(_player, times(2)).addEnd("mp3", _strm);
    }

    @Test
    public void testPlayNext() {
        LoginPass testLoginPass = new LoginPass("Login", "Pass");
        LocationData fileLoc = new LocationData("TEST/test2/file.mp3");
        LocationData fileLoc2 = new LocationData("TEST/test2/file2.wav");
        when(_player.onNeedNext()).thenReturn(Observable.empty());
        when(_player.onStop()).thenReturn(Observable.empty());
        when(_player.canPlay()).thenReturn(true);
        when(_player.addEnd(any(), any())).thenReturn(Single.just(new Object()));
        when(_lpman.getLoginPass(fileLoc)).thenReturn(testLoginPass);
        when(_lpman.getLoginPass(fileLoc2)).thenReturn(testLoginPass);
        when(_smbUtils.getFileStream(fileLoc, testLoginPass)).thenReturn(Maybe.just(_strm));
        when(_smbUtils.getFileStream(fileLoc2, testLoginPass)).thenReturn(Maybe.just(_strm));

        _playlist = _comp.getPlaylist();
        _playlist.setPlaying(true);
        _playlist.addFile(fileLoc);
        _playlist.addFile(fileLoc2);
        _playlist.playNext();

        InOrder inOrder = inOrder(_player);
        inOrder.verify(_player, times(1)).addEnd("mp3", _strm);
        inOrder.verify(_player, times(1)).addEnd("wav", _strm);
        inOrder.verify(_player, times(1)).clear();
        inOrder.verify(_player, times(1)).addEnd("wav", _strm);
    }

    @Test
    public void testPlayNextPrev() {
        LoginPass testLoginPass = new LoginPass("Login", "Pass");
        LocationData fileLoc = new LocationData("TEST/test2/file.mp3");
        LocationData fileLoc2 = new LocationData("TEST/test2/file2.wav");
        when(_player.onNeedNext()).thenReturn(Observable.empty());
        when(_player.onStop()).thenReturn(Observable.empty());
        when(_player.canPlay()).thenReturn(true);
        when(_player.addEnd(any(), any())).thenReturn(Single.just(new Object()));
        when(_lpman.getLoginPass(fileLoc)).thenReturn(testLoginPass);
        when(_lpman.getLoginPass(fileLoc2)).thenReturn(testLoginPass);
        when(_smbUtils.getFileStream(fileLoc, testLoginPass)).thenReturn(Maybe.just(_strm));
        when(_smbUtils.getFileStream(fileLoc2, testLoginPass)).thenReturn(Maybe.just(_strm));

        _playlist = _comp.getPlaylist();
        _playlist.setPlaying(true);
        _playlist.addFile(fileLoc);
        _playlist.addFile(fileLoc2);
        _playlist.playNext();
        _playlist.playPrev();

        InOrder inOrder = inOrder(_player);
        inOrder.verify(_player, times(1)).addEnd("mp3", _strm);
        inOrder.verify(_player, times(1)).addEnd("wav", _strm);
        inOrder.verify(_player, times(1)).clear();
        inOrder.verify(_player, times(1)).addEnd("wav", _strm);
        inOrder.verify(_player, times(1)).clear();
        inOrder.verify(_player, times(1)).addEnd("mp3", _strm);
    }

    @Test
    public void testPlayAutoNext() {
        LoginPass testLoginPass = new LoginPass("Login", "Pass");
        LocationData fileLoc = new LocationData("TEST/test2/file.mp3");
        LocationData fileLoc2 = new LocationData("TEST/test2/file2.wav");
        PublishSubject<Object> _stopSubj = PublishSubject.create();
        when(_player.onNeedNext()).thenReturn(_stopSubj);
        when(_player.onStop()).thenReturn(Observable.empty());
        when(_player.canPlay()).thenReturn(true);
        when(_player.addEnd(any(), any())).thenReturn(Single.just(new Object()));
        when(_player.removeFirst()).thenReturn(Single.just(new Object()));
        when(_lpman.getLoginPass(fileLoc)).thenReturn(testLoginPass);
        when(_lpman.getLoginPass(fileLoc2)).thenReturn(testLoginPass);
        when(_smbUtils.getFileStream(fileLoc, testLoginPass)).thenReturn(Maybe.just(_strm));
        when(_smbUtils.getFileStream(fileLoc2, testLoginPass)).thenReturn(Maybe.just(_strm));

        _playlist = _comp.getPlaylist();
        _playlist.setPlaying(true);
        _playlist.addFile(fileLoc);
        _playlist.addFile(fileLoc2);
        _stopSubj.onNext(new Object());
        InOrder inOrder = inOrder(_player);
        inOrder.verify(_player, times(1)).addEnd("mp3", _strm);
        inOrder.verify(_player, times(1)).addEnd("wav", _strm);
        inOrder.verify(_player, times(1)).removeFirst();
    }
}
