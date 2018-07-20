package com.dvarubla.sambamusicplayer.toastman;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.dvarubla.sambamusicplayer.R;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class ToastMan implements IToastManActivity {
    @Inject
    Context _context;
    private ArrayList<AppCompatActivity> _activities;

    @Inject
    ToastMan(){
        _activities = new ArrayList<>();
    }

    private void showShortToast(@StringRes int id){
        showShortToast(_context.getString(id));
    }
    private void showShortToast(String str){
        Observable.fromCallable( () -> {
            for (AppCompatActivity activity : _activities) {
                TextView tv = activity.findViewById(R.id.toast);
                tv.setVisibility(View.VISIBLE);
                tv.setText(str);
                AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
                anim.setDuration(1000);
                anim.setStartOffset(2000);
                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        tv.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                tv.startAnimation(anim);
            }
            return new Object();
        }).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
    }

    @Override
    public void showSettingsSaved() {
        showShortToast(R.string.settings_saved);
    }

    @Override
    public void showFileAdded(String str) {
        showShortToast(_context.getString(R.string.file_was_added, str));
    }

    @Override
    public void showFilePlaying(String str) {
        showShortToast(_context.getString(R.string.file_is_playing, str));
    }

    public void setActivity(AppCompatActivity activity){
        _activities.add(activity);
        TextView tv = activity.findViewById(R.id.toast);
        tv.setVisibility(View.GONE);
    }

    public void clearActivity(AppCompatActivity activity){
        _activities.remove(activity);
    }
}
