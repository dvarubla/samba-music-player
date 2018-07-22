package com.dvarubla.sambamusicplayer.filelist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.dvarubla.sambamusicplayer.Application;
import com.dvarubla.sambamusicplayer.ItemSingleton;
import com.dvarubla.sambamusicplayer.R;
import com.dvarubla.sambamusicplayer.smbutils.LoginPass;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.subjects.PublishSubject;

import static com.dvarubla.sambamusicplayer.Common.LOCATION_NAME;

public class FileListActivity extends AppCompatActivity implements IFileListView{
    private abstract class OnSwipeTouchListener implements View.OnTouchListener {

        private final GestureDetectorCompat gestureDetector;

        OnSwipeTouchListener(Context context) {
            gestureDetector = new GestureDetectorCompat(context, new GestureListener());
        }

        abstract void onSwipeLeft();

        abstract void onSwipeRight();

        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }

        private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

            private static final int SWIPE_DISTANCE_THRESHOLD = 80;
            private static final int SWIPE_VELOCITY_THRESHOLD = 80;

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if(e1 != null) {
                    float distanceX = e2.getX() - e1.getX();
                    float distanceY = e2.getY() - e1.getY();
                    if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD &&
                            Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (distanceX > 0)
                            onSwipeRight();
                        else
                            onSwipeLeft();
                        return true;
                    }
                }
                return false;
            }
        }
    }

    private PublishSubject<Object> _flingLeftSubj;
    private PublishSubject<Object> _flingRightSubj;
    private PublishSubject<Object> _stopClickedSubj;
    private PublishSubject<Object> _playClickedSubj;
    private boolean _isPlaying;

    private boolean _needSave;
    @Inject
    IFileListPresenter _presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filelist);
        Application.processAppActivity(this);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onStart() {
        _flingLeftSubj = PublishSubject.create();
        _flingRightSubj = PublishSubject.create();
        _stopClickedSubj = PublishSubject.create();
        _playClickedSubj = PublishSubject.create();
        _isPlaying = false;
        _needSave = false;
        ItemSingleton<FileListComponent> s = ItemSingleton.getInstance(FileListComponent.class);
        FileListComponent comp;
        boolean firstTime = true;
        RecyclerView list = findViewById(R.id.filelist_view);
        if(s.hasItem()){
            firstTime = false;
            comp = s.getItem();
            comp.inject(this);
        } else {
            comp = DaggerFileListComponent.builder().applicationComponent(Application.getComponent()).build();
            comp.inject(this);
            _presenter.init(this, getIntent().getStringExtra(LOCATION_NAME));
            s.setItem(comp);
            OnSwipeTouchListener listener = new OnSwipeTouchListener(this){
                @Override
                void onSwipeLeft() {
                    _flingLeftSubj.onNext(new Object());
                }

                @Override
                void onSwipeRight() {
                    _flingRightSubj.onNext(new Object());
                }
            };
            list.setOnTouchListener(listener);
            list.addOnItemTouchListener(new RecyclerView.OnItemTouchListener(){
                @Override
                public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                    listener.onTouch(rv, e);
                    return false;
                }

                @Override
                public void onTouchEvent(RecyclerView rv, MotionEvent e) {
                }

                @Override
                public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
                }
            });
        }
        list.setLayoutManager(
                new LinearLayoutManager(getApplicationContext())
        );
        comp.getFileListCtrl().setView(list);
        if(!firstTime){
            _presenter.setView(this);
        }
        comp.getToastManActivity().setActivity(this);
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
        _flingLeftSubj.onComplete();
        _flingRightSubj.onComplete();
        _playClickedSubj.onComplete();
        _stopClickedSubj.onComplete();
        ItemSingleton.getInstance(FileListComponent.class).getItem().getToastManActivity().clearActivity(this);
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

    @Override
    public void onBackPressed() {
        if(_presenter.onBackClicked()) {
            super.onBackPressed();
        }
    }

    @Override
    public Observable<Object> onFlingLeft() {
        return _flingLeftSubj;
    }

    @Override
    public Observable<Object> onFlingRight() {
        return _flingRightSubj;
    }

    @Override
    public Observable<Object> onMusicStop() {
        return _stopClickedSubj;
    }

    @Override
    public Observable<Object> onMusicPlay() {
        return _playClickedSubj;
    }

    @Override
    public void setTitle(String title) {
        Observable.fromCallable( () -> {
            ActionBar bar = getSupportActionBar();
            if (bar != null) {
                bar.setTitle(title);
            }
            return new Object();
        }).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if(_isPlaying) {
            inflater.inflate(R.menu.stop_button, menu);
        } else {
            inflater.inflate(R.menu.play_button, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.play: _playClickedSubj.onNext(new Object()); break;
            case R.id.stop: _stopClickedSubj.onNext(new Object()); break;
        }
        return true;
    }

    @Override
    public void setPlaying(boolean playing) {
        _isPlaying = playing;
        invalidateOptionsMenu();
    }
}
