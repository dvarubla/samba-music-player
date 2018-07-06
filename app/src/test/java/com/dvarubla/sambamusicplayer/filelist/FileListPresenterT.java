package com.dvarubla.sambamusicplayer.filelist;

import com.dvarubla.sambamusicplayer.smbutils.LocationData;
import com.dvarubla.sambamusicplayer.smbutils.LoginPass;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import javax.inject.Inject;

import io.reactivex.Maybe;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FileListPresenterT {
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
        _presenter.setLocation("TEST/test/dir");
        _presenter.setView(_view);
    }

    @Test
    public void testSuccess(){
        LoginPass lp = new LoginPass("e", "f");
        when(_view.showLoginPassDialog(anyString())).thenReturn(Maybe.just(lp));
        String [] strs = {
                "a" , "b", "c"
        };
        when(_model.getFiles(any(LocationData.class))).thenReturn(Maybe.just(strs));
        prepare();
        verify(_fileListCtrl, times(1)).setItems(strs);
    }

    @Test
    public void test1Retry(){
        LoginPass lp = new LoginPass("e", "f");
        when(_view.showLoginPassDialog(anyString())).thenReturn(Maybe.just(lp));
        final String [] strs = {
                "a" , "b", "c"
        };
        when(_model.getFiles(any(LocationData.class))).thenReturn(Maybe.create(emitter -> {
            if(i == 1){
                emitter.onSuccess(strs);
            } else {
                emitter.onComplete();
            }
            i++;
        }));
        prepare();
        verify(_model, times(1)).setLoginPassForServer("TEST", lp);
        verify(_fileListCtrl, times(1)).setItems(strs);
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
        final String [] strs = {
                "a" , "b", "c"
        };
        when(_model.getFiles(any(LocationData.class))).thenReturn(Maybe.create(emitter -> {
            if(i == 3){
                emitter.onSuccess(strs);
            } else {
                emitter.onComplete();
            }
            i++;
        }));
        prepare();
        InOrder inOrder = inOrder(_model);
        inOrder.verify(_model).setLoginPassForServer("TEST", lp);
        inOrder.verify(_model, times(2)).setLoginPassForServer("TEST", lp2);
        verify(_fileListCtrl, times(1)).setItems(strs);
    }
}
