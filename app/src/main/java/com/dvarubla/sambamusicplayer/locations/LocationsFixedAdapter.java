package com.dvarubla.sambamusicplayer.locations;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dvarubla.sambamusicplayer.R;
import com.jakewharton.rxbinding2.view.RxView;

import io.reactivex.subjects.PublishSubject;

public class LocationsFixedAdapter extends RecyclerView.Adapter<LocationsFixedAdapter.ViewHolder> {
    public void setStrings(String[] strings) {
        _dataset = strings.clone();
        notifyDataSetChanged();
    }

    public String[] getStrings() {
        return _dataset.clone();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView item;
        ViewHolder(TextView v) {
            super(v);
            item = v;
        }
    }

    private String [] _dataset;

    private PublishSubject<String> _clickedButtonsSubj;

    public PublishSubject<String> getSubject(){
        return _clickedButtonsSubj;
    }

    LocationsFixedAdapter(String [] arr){
        _dataset = arr;
        _clickedButtonsSubj = PublishSubject.create();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final TextView tv = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.location_item, parent, false);

        RxView.clicks(tv).map(o -> tv.getText().toString()).subscribe(_clickedButtonsSubj);
        return new ViewHolder(tv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.item.setText(_dataset[position]);
    }

    @Override
    public int getItemCount() {
        return _dataset.length;
    }
}
