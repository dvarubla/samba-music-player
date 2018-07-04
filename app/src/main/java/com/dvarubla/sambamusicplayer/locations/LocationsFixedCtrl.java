package com.dvarubla.sambamusicplayer.locations;

import android.support.v7.widget.RecyclerView;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class LocationsFixedCtrl implements ILocationsFixedCtrl {
    private LocationsFixedAdapter _adapter;
    LocationsFixedCtrl(String [] names){
        _adapter = new LocationsFixedAdapter(names);
    }

    @Override
    public void onCreated(RecyclerView v){
        //noinspection ConstantConditions
        initRecyclerView(v);
    }

    private void initRecyclerView(RecyclerView v){
        v.setAdapter(_adapter);
    }

    @Override
    public void setStrings(String[] strings) {
        _adapter.setStrings(strings);
    }

    @Override
    public String[] getStrings() {
        return _adapter.getStrings();
    }

    @Override
    public Observable<String> locationClicked() {
        return _adapter.getSubject();
    }
}
