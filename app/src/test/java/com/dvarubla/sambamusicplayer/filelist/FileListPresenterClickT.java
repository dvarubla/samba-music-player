package com.dvarubla.sambamusicplayer.filelist;

import com.dvarubla.sambamusicplayer.smbutils.FileItem;
import com.dvarubla.sambamusicplayer.smbutils.FolderItem;
import com.dvarubla.sambamusicplayer.smbutils.IFileOrFolderItem;
import com.dvarubla.sambamusicplayer.smbutils.LoginPass;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.Observable;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FileListPresenterClickT {
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
        when(_view.onFlingLeft()).thenReturn(Observable.empty());
        when(_view.onFlingRight()).thenReturn(Observable.empty());
        _presenter.init(_view, "TEST/test/dir");
    }

    private void checkItems(IFileOrFolderItem[] items1, IFileOrFolderItem[] items2){
        doAnswer(invocation -> {
            Assert.assertArrayEquals(
                    items1,
                    ((Observable<IFileOrFolderItem[]>)invocation.getArgument(0)).test().values().get(0)
            );
            return null;
        }).doAnswer(invocation -> {
            Assert.assertArrayEquals(
                    items2,
                    ((Observable<IFileOrFolderItem[]>)invocation.getArgument(0)).test().values().get(0)
            );
            return null;
        }).when(_fileListCtrl).setItemsObs(any());
    }

    @Test
    public void testDirClick(){
        when(_fileListCtrl.itemClicked()).thenReturn(Observable.just(new FolderItem("folder")));
        IFileOrFolderItem[] items = {
                new FileItem("a") , new FileItem("b"), new FileItem("c")
        };
        IFileOrFolderItem[] items2 = {
                new FileItem("c") , new FileItem("d"), new FileItem("e")
        };
        when(_model.getFiles()).thenReturn(Observable.create(emitter -> {
            if(i == 0){
                emitter.onNext(items);
                emitter.onComplete();
            } else {
                emitter.onNext(items2);
                emitter.onComplete();
            }
            i++;
        }));
        checkItems(items, items2);
        prepare();
        verify(_model).addPath("folder");
    }

    @Test
    public void testDirClickLoginReq(){
        when(_fileListCtrl.itemClicked()).thenReturn(Observable.just(new FolderItem("folder")));
        IFileOrFolderItem[] items = {
                new FileItem("a") , new FileItem("b"), new FileItem("c")
        };
        IFileOrFolderItem[] items2 = {
                new FileItem("c") , new FileItem("d"), new FileItem("e")
        };
        when(_model.getFiles()).thenReturn(Observable.create(emitter -> {
            if(i == 0){
                emitter.onNext(items);
                emitter.onComplete();
            } else if(i == 1 || i == 2){
                emitter.onComplete();
            } else {
                emitter.onNext(items2);
                emitter.onComplete();
            }
            i++;
        }));
        when(_view.showLoginPassDialog(anyString())).thenReturn(Maybe.just(new LoginPass("e", "f")));
        checkItems(items, items2);
        prepare();
        verify(_model).addPath("folder");
    }

    @Test
    public void testBackClickOnRoot(){
        IFileOrFolderItem[] items = {
                new FileItem("a") , new FileItem("b"), new FileItem("c")
        };
        IFileOrFolderItem[] items2 = {
                new FileItem("c") , new FileItem("d"), new FileItem("e")
        };
        when(_model.getFiles()).thenReturn(Observable.create(emitter -> {
            if(i == 0){
                emitter.onNext(items);
                emitter.onComplete();
            } else {
                emitter.onNext(items2);
                emitter.onComplete();
            }
            i++;
        }));
        when(_fileListCtrl.itemClicked()).thenReturn(Observable.empty());
        checkItems(items, items2);
        prepare();
        _presenter.onBackClicked();
        verify(_model, never()).removeFromPath();
    }

    @Test
    public void testBackClickNotOnRoot(){
        IFileOrFolderItem[] items = {
                new FileItem("a") , new FileItem("b"), new FileItem("c")
        };
        IFileOrFolderItem[] items2 = {
                new FileItem("c") , new FileItem("d"), new FileItem("e")
        };
        when(_model.getFiles()).thenReturn(Observable.create(emitter -> {
            if(i == 0 || i == 2){
                emitter.onNext(items);
                emitter.onComplete();
            } else if(i == 1){
                emitter.onNext(items2);
                emitter.onComplete();
            }
            i++;
        }));
        when(_fileListCtrl.itemClicked()).thenReturn(Observable.just(new FolderItem("Folder")));
        when(_model.addPath("Folder")).thenReturn("test/dir/Folder");
        checkItems(items, items2);
        prepare();
        _presenter.onBackClicked();
        verify(_model, times(1)).removeFromPath();
    }
}
