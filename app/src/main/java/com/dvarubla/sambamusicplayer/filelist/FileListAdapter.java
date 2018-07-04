package com.dvarubla.sambamusicplayer.filelist;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dvarubla.sambamusicplayer.R;

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.ViewHolder> {
    public void setStrings(String[] strings) {
        _dataset = strings.clone();
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout item;
        ViewHolder(LinearLayout v) {
            super(v);
            item = v;
        }
    }

    private String [] _dataset;

    FileListAdapter(){
        _dataset = new String[0];
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LinearLayout tv = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.file_or_dir_item, parent, false);
        return new ViewHolder(tv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ((TextView) holder.item.findViewById(R.id.file_or_dir_name)).setText(_dataset[position]);
    }

    @Override
    public int getItemCount() {
        return _dataset.length;
    }
}
