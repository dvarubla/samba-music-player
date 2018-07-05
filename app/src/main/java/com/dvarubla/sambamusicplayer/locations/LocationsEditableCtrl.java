package com.dvarubla.sambamusicplayer.locations;

import android.support.v7.widget.RecyclerView;

import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;

public class LocationsEditableCtrl implements ILocationsEditableCtrl {
    private LocationsEditableAdapter _adapter;
    LocationsEditableCtrl(){
        _adapter = new LocationsEditableAdapter();
    }

    public void onCreated(RecyclerView v){
        //noinspection ConstantConditions
        initRecyclerView(v);
    }

    private void initRecyclerView(RecyclerView v){
        RecyclerViewDragDropManager dragMgr = new RecyclerViewDragDropManager();
        RecyclerViewSwipeManager swipeMgr = new RecyclerViewSwipeManager();
        dragMgr.setInitiateOnMove(false);
        dragMgr.setInitiateOnLongPress(true);
        v.setAdapter(swipeMgr.createWrappedAdapter(dragMgr.createWrappedAdapter(_adapter)));
        dragMgr.attachRecyclerView(v);
        swipeMgr.attachRecyclerView(v);
    }

    @Override
    public String[] getStrings() {
        return _adapter.getStrings();
    }

    @Override
    public void setStrings(String[] strings) {
        _adapter.setStrings(strings);
    }

    @Override
    public void addNewString(String location) {
        _adapter.addNewString(location);
    }
}
