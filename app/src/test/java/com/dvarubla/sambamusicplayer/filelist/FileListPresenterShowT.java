package com.dvarubla.sambamusicplayer.filelist;

import com.dvarubla.sambamusicplayer.smbutils.FileItem;
import com.dvarubla.sambamusicplayer.smbutils.IFileOrFolderItem;
import com.dvarubla.sambamusicplayer.smbutils.LocationData;
import com.dvarubla.sambamusicplayer.smbutils.LoginPass;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.Observable;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FileListPresenterShowT {
    @Inject
    FileListPresenter _presenter;

    @Inject
    IFileListView _view;

    @Inject
    IFileListCtrl _fileListCtrl;

    @Inject
    IFileListModel _model;

    private int i = 0;

    @Before
    public void before(){
        DaggerFileListComponentPresenterT.builder().build().inject(this);
    }

    private void prepare(){
        when(_model.onFileAdded()).thenReturn(Observable.empty());
        when(_model.onFilePlaying()).thenReturn(Observable.empty());
        when(_fileListCtrl.itemClicked()).thenReturn(Observable.empty());
        when(_view.onFlingLeft()).thenReturn(Observable.empty());
        when(_view.onFlingRight()).thenReturn(Observable.empty());
        when(_view.onMusicPlay()).thenReturn(Observable.empty());
        when(_view.onMusicStop()).thenReturn(Observable.empty());
        when(_model.onPlaylistStop()).thenReturn(Observable.empty());
        _presenter.init(_view, "TEST/test/dir");
    }

    private void check(IFileOrFolderItem[] items){
        doAnswer(invocation -> {
            Assert.assertArrayEquals(
                    items,
                    ((Observable<IFileOrFolderItem[]>)invocation.getArgument(0)).test().values().get(0)
            );
            return null;
        }).when(_fileListCtrl).setItemsObs(any());
    }

    @Test
    public void testSuccess(){
        when(_fileListCtrl.itemClicked()).thenReturn(Observable.empty());
        LoginPass lp = new LoginPass("e", "f");
        when(_view.showLoginPassDialog(anyString())).thenReturn(Maybe.just(lp));
        IFileOrFolderItem[] items = {
                new FileItem("a") , new FileItem("b"), new FileItem("c")
        };
        when(_model.getFiles()).thenReturn(Observable.just(items));
        check(items);
        prepare();
        verify(_model, times(1)).setLocationData(any());
    }

    @Test
    public void test1Retry(){
        LoginPass lp = new LoginPass("e", "f");
        when(_view.showLoginPassDialog(anyString())).thenReturn(Maybe.just(lp));
        IFileOrFolderItem[] items = {
                new FileItem("a") , new FileItem("b"), new FileItem("c")
        };
        when(_model.getFiles()).thenReturn(Observable.create(emitter -> {
            if(i == 1){
                emitter.onNext(items);
                emitter.onComplete();
            } else {
                emitter.onComplete();
            }
            i++;
        }));
        check(items);
        prepare();
        verify(_model, times(1)).setLoginPassForServer(new LocationData("TEST/test/dir"), lp);
    }

    @Test
    public void test3Retries(){
        LoginPass lp = new LoginPass("e", "f");
        LoginPass lp2 = new LoginPass("x", "y");
        when(_view.showLoginPassDialog(anyString())).thenReturn(Maybe.create(emitter -> {
            if(i == 0){
                emitter.onSuccess(lp);
            } else {
                emitter.onSuccess(lp2);
            }
        }));
        IFileOrFolderItem[] items = {
                new FileItem("a") , new FileItem("b"), new FileItem("c")
        };
        when(_model.getFiles()).thenReturn(Observable.create(emitter -> {
            if(i == 3){
                emitter.onNext(items);
                emitter.onComplete();
            } else {
                emitter.onComplete();
            }
            i++;
        }));
        check(items);
        prepare();
        InOrder inOrder = inOrder(_model);
        inOrder.verify(_model).setLoginPassForServer(new LocationData("TEST/test/dir"), lp);
        inOrder.verify(_model, times(2)).setLoginPassForServer(new LocationData("TEST/test/dir"), lp2);
    }
}
