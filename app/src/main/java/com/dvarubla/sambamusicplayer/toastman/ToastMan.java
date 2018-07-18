package com.dvarubla.sambamusicplayer.toastman;

import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.Toast;

import com.dvarubla.sambamusicplayer.R;

import javax.inject.Inject;

public class ToastMan implements IToastMan{
    @Inject
    Context _context;

    @Inject
    ToastMan(){}

    private void showShortToast(@StringRes int id){
        Toast.makeText(_context, id, Toast.LENGTH_SHORT).show();
    }
    private void showShortToast(String str){
        Toast.makeText(_context, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showSettingsSaved() {
        showShortToast(R.string.settings_saved);
    }

    @Override
    public void showFileAdded(String str) {
        showShortToast(_context.getString(R.string.file_was_added, str));
    }
}
