package com.dvarubla.sambamusicplayer.locations;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dvarubla.sambamusicplayer.R;

public class LocationsEditableFragment extends Fragment {
    private ILocationsEditableCtrl _ctrl;

    public void setCtrl(ILocationsEditableCtrl ctrl){
        _ctrl = ctrl;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView v = (RecyclerView) inflater.inflate(R.layout.locations_fragment, container, false);
        //noinspection ConstantConditions
        v.setLayoutManager(
                new LinearLayoutManager(getActivity().getApplicationContext())
        );
        _ctrl.onCreated(v);
        return v;
    }

    @Override
    public void onDestroyView() {
        //noinspection ConstantConditions
        ((RecyclerView) getView()).setAdapter(null);
        super.onDestroyView();
    }
}
