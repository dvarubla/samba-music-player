package com.dvarubla.sambamusicplayer.locations;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.dvarubla.sambamusicplayer.ItemSingleton;
import com.dvarubla.sambamusicplayer.R;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);
        _editClickedSubj = PublishSubject.create();
        _backClickedSubj = PublishSubject.create();
        _saveClickedSubj = PublishSubject.create();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if(_presenter.isEditPressed()) {
            inflater.inflate(R.menu.save_button, menu);
        } else {
            inflater.inflate(R.menu.edit_button, menu);
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
        ItemSingleton<LocationsComponent> s = ItemSingleton.getInstance(LocationsComponent.class);
        if(s.hasItem()){
            _locComp = s.getItem();
            _locComp.inject(this);
            if(_presenter.isEditPressed()) {
                _locEdFragment = (LocationsEditableFragment) getSupportFragmentManager().findFragmentByTag("edit_loc");
                _locEdFragment.setCtrl(_presenter.getLocEdComp());
            }
            _locFixedFragment = (LocationsFixedFragment) getSupportFragmentManager().findFragmentByTag("fixed_loc");
            _locFixedFragment.setCtrl(_presenter.getLocFixComp());
        } else {
            _locComp = DaggerLocationsComponent.builder().build();
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

    private void goBack() {
        _backClickedSubj.onNext(new Object());
        invalidateOptionsMenu();
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void showSettingsSaved() {
        Toast.makeText(this, R.string.settings_saved, Toast.LENGTH_SHORT).show();
    }
}
