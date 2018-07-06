package com.dvarubla.sambamusicplayer.filelist;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dvarubla.sambamusicplayer.R;
import com.dvarubla.sambamusicplayer.smbutils.IFileOrFolderItem;

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.ViewHolder> {
    public void setItems(IFileOrFolderItem[] items) {
        _dataset = items.clone();
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout item;
        ViewHolder(LinearLayout v) {
            super(v);
            item = v;
        }
    }

    private IFileOrFolderItem [] _dataset;

    FileListAdapter(){
        _dataset = new IFileOrFolderItem[0];
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LinearLayout tv = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.file_or_dir_item, parent, false);
        return new ViewHolder(tv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ((TextView) holder.item.findViewById(R.id.file_or_dir_name)).setText(_dataset[position].getName());
    }

    @Override
    public int getItemCount() {
        return _dataset.length;
    }
}
