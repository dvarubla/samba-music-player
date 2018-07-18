package com.dvarubla.sambamusicplayer.locations;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.dvarubla.sambamusicplayer.Application;
import com.dvarubla.sambamusicplayer.ItemSingleton;
import com.dvarubla.sambamusicplayer.R;
import com.dvarubla.sambamusicplayer.filelist.FileListActivity;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

import static com.dvarubla.sambamusicplayer.Common.LOCATION_NAME;

public class LocationsActivity extends AppCompatActivity implements ILocationsView{
    @Inject
    ILocationsPresenter _presenter;
    private LocationsComponent _locComp;
    private LocationsEditableFragment _locEdFragment;
    private LocationsFixedFragment _locFixedFragment;

    private boolean _needSave;
    private PublishSubject<Object> _editClickedSubj;
    private PublishSubject<Object> _saveClickedSubj;
    private PublishSubject<Object> _backClickedSubj;
    private PublishSubject<Object> _addClickedSubj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if(_presenter.isEditPressed()) {
            setTitle(R.string.edit);
            inflater.inflate(R.menu.add_button, menu);
            inflater.inflate(R.menu.save_button, menu);
        } else {
            inflater.inflate(R.menu.edit_button, menu);
            setTitle(R.string.locations);
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        _needSave = true;
    }

    @Override
    public void onStart() {
        _needSave = false;
        _editClickedSubj = PublishSubject.create();
        _backClickedSubj = PublishSubject.create();
        _saveClickedSubj = PublishSubject.create();
        _addClickedSubj = PublishSubject.create();
        ItemSingleton<LocationsComponent> s = ItemSingleton.getInstance(LocationsComponent.class);
        if(s.hasItem()){
            _locComp = s.getItem();
            _locComp.inject(this);
            if(_presenter.isEditPressed()) {
                _locEdFragment = (LocationsEditableFragment) getSupportFragmentManager().findFragmentByTag("edit_loc");
                _locEdFragment.setCtrl(_locComp.getEditableCtrl());
            }
            _locFixedFragment = (LocationsFixedFragment) getSupportFragmentManager().findFragmentByTag("fixed_loc");
            _locFixedFragment.setCtrl(_locComp.getFixedCtrl());
        } else {
            _locComp = DaggerLocationsComponent.builder().applicationComponent(Application.getComponent()).build();
            _locComp.inject(this);
            _locFixedFragment = _locComp.getFixedFragment();

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.locations_layout, _locFixedFragment, "fixed_loc");
            fragmentTransaction.commit();
            s.setItem(_locComp);
        }
        _presenter.setView(this);
        super.onStart();
    }

    @Override
    public void onStop(){
        super.onStop();
        _editClickedSubj.onComplete();
        _backClickedSubj.onComplete();
        _saveClickedSubj.onComplete();
        _addClickedSubj.onComplete();
        if(!_needSave){
            ItemSingleton.getInstance(LocationsComponent.class).removeItem();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_locations:
                _editClickedSubj.onNext(new Object());
                break;
            case R.id.save_locations:
                _saveClickedSubj.onNext(new Object());
                break;
            case R.id.add_location:
                _addClickedSubj.onNext(new Object());
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        FragmentManager mgr = getSupportFragmentManager();
        if (mgr.getBackStackEntryCount() == 0) {
            super.onBackPressed();
        } else {
            goBack();
        }
    }

    @Override
    public void editLocations() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.remove(_locFixedFragment);
        _locEdFragment = _locComp.getEditableFragment();
        fragmentTransaction.add(R.id.locations_layout, _locEdFragment, "edit_loc");
        fragmentTransaction.addToBackStack("edit");
        fragmentTransaction.commit();
        invalidateOptionsMenu();
    }

    @Override
    public Observable<Object> editClicked() {
        return _editClickedSubj;
    }

    @Override
    public Observable<Object> saveClicked() {
        return _saveClickedSubj;
    }

    @Override
    public Observable<Object> backClicked() {
        return _backClickedSubj;
    }

    @Override
    public Observable<Object> addClicked() {
        return _addClickedSubj;
    }

    @Override
    public void showFileList(String str) {
        Intent intent = new Intent(this, FileListActivity.class);
        intent.putExtra(LOCATION_NAME, str);
        startActivity(intent);
    }

    private void goBack() {
        _backClickedSubj.onNext(new Object());
        invalidateOptionsMenu();
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                Rect rootRect = new Rect();
                findViewById(R.id.locations_layout).getGlobalVisibleRect(rootRect);
                if (
                        !outRect.contains((int)event.getRawX(), (int)event.getRawY()) &&
                        rootRect.contains((int)event.getRawX(), (int)event.getRawY())
                ) {
                    v.clearFocus();
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }
}
