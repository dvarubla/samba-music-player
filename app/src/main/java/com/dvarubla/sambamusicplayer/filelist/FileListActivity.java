package com.dvarubla.sambamusicplayer.filelist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.dvarubla.sambamusicplayer.ItemSingleton;
import com.dvarubla.sambamusicplayer.R;

import javax.inject.Inject;

public class FileListActivity extends AppCompatActivity implements IFileListView{
    private boolean _needSave;
    @Inject
    IFileListPresenter _presenter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filelist);
    }

    @Override
    protected void onStart() {
        _needSave = false;
        ItemSingleton<FileListComponent> s = ItemSingleton.getInstance(FileListComponent.class);
        FileListComponent comp;
        if(s.hasItem()){
             comp = s.getItem();
            comp.inject(this);
        } else {
            comp = DaggerFileListComponent.builder().build();
            comp.inject(this);
            s.setItem(comp);
        }
        RecyclerView list = findViewById(R.id.filelist_view);
        list.setLayoutManager(
                new LinearLayoutManager(getApplicationContext())
        );
        comp.getFileListCtrl().setView(list);
        _presenter.setView(this);
        super.onStart();
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        _needSave = true;
    }

    @Override
    public void onStop(){
        super.onStop();
        if(!_needSave){
            ItemSingleton.getInstance(FileListComponent.class).removeItem();
        }
    }
}
