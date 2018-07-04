package com.dvarubla.sambamusicplayer.locations;

import android.support.v7.widget.RecyclerView;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class LocationsFixedCtrl implements ILocationsFixedCtrl {
    private LocationsFixedAdapter _adapter;
    private Disposable _clickDisp;
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

        _adapter.getSubject().subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                _clickDisp = d;
            }

            @Override
            public void onNext(String s) {
                System.out.println(s);
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });
    }

    @Override
    public void onDetach(){
        _clickDisp.dispose();
    }

    @Override
    public void setStrings(String[] strings) {
        _adapter.setStrings(strings);
    }

    @Override
    public String[] getStrings() {
        return _adapter.getStrings();
    }
}
