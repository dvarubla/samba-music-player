package com.dvarubla.sambamusicplayer.toastman;

import android.support.v7.app.AppCompatActivity;

public interface IToastManActivity extends IToastMan{
    void setActivity(AppCompatActivity activity);

    void clearActivity(AppCompatActivity activity);
}
