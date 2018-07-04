package com.dvarubla.sambamusicplayer.locations;

import android.support.v7.widget.RecyclerView;

import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;

public class LocationsEditableCtrl implements ILocationsEditableCtrl {
    private LocationsEditableAdapter _adapter;
    LocationsEditableCtrl(){
        _adapter = new LocationsEditableAdapter();
    }

    @Override
    public void onCreated(RecyclerView v){
        //noinspection ConstantConditions
        initRecyclerView(v);
    }

    private void initRecyclerView(RecyclerView v){
        RecyclerViewDragDropManager dragMgr = new RecyclerViewDragDropManager();
        dragMgr.setInitiateOnMove(false);
        dragMgr.setInitiateOnLongPress(true);
        v.setAdapter(dragMgr.createWrappedAdapter(_adapter));
        dragMgr.attachRecyclerView(v);
    }

    @Override
    public String[] getStrings() {
        return _adapter.getStrings();
    }

    @Override
    public void setStrings(String[] strings) {
        _adapter.setStrings(strings);
    }
}
