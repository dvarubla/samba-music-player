package com.dvarubla.sambamusicplayer.filelist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.dvarubla.sambamusicplayer.ItemSingleton;
import com.dvarubla.sambamusicplayer.R;
import com.dvarubla.sambamusicplayer.smbutils.DaggerSmbUtilsComponent;
import com.dvarubla.sambamusicplayer.smbutils.LoginPass;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static com.dvarubla.sambamusicplayer.Common.LOCATION_NAME;

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
            comp = DaggerFileListComponent.builder().smbUtilsComponent(DaggerSmbUtilsComponent.builder().build()).build();
            comp.inject(this);
            _presenter.setLocation(getIntent().getStringExtra(LOCATION_NAME));
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

    @Override
    public Maybe<LoginPass> showLoginPassDialog(String server) {
        return Maybe.<LoginPass>create(emitter -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.enter_server_login_pass, server));
            View viewInflated = LayoutInflater.from(this).inflate(
                    R.layout.auth_dialog,
                    findViewById(android.R.id.content),
                    false
            );
            final EditText inputLogin = viewInflated.findViewById(R.id.login);
            final EditText inputPass = viewInflated.findViewById(R.id.password);
            builder.setView(viewInflated);
            builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                LoginPass loginPass = new LoginPass(inputLogin.getText().toString(), inputPass.getText().toString());
                dialog.dismiss();
                emitter.onSuccess(loginPass);
            });
            builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                dialog.cancel();
                emitter.onComplete();
            });
            builder.show();
        }).subscribeOn(AndroidSchedulers.mainThread());
    }
}
