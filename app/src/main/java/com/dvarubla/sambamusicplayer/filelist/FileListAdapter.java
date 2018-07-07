package com.dvarubla.sambamusicplayer.filelist;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dvarubla.sambamusicplayer.R;
import com.dvarubla.sambamusicplayer.smbutils.FolderItem;
import com.dvarubla.sambamusicplayer.smbutils.IFileOrFolderItem;
import com.jakewharton.rxbinding2.view.RxView;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.ViewHolder> {
    private PublishSubject<IFileOrFolderItem> _itemClickedSubj;

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
        _itemClickedSubj = PublishSubject.create();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LinearLayout tv = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.file_or_dir_item, parent, false);
        ViewHolder vh = new ViewHolder(tv);
        RxView.clicks(tv).map(nothing -> _dataset[vh.getAdapterPosition()]).subscribe(_itemClickedSubj);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageView iv = holder.item.findViewById(R.id.folder_icon);
        if(_dataset[position] instanceof FolderItem){
            iv.setVisibility(View.VISIBLE);
        } else {
            iv.setVisibility(View.GONE);
        }
        if(position % 2 == 1) {
            holder.item.setBackgroundColor(holder.item.getResources().getColor(R.color.rec_view_alt));
        } else {
            holder.item.setBackgroundColor(holder.item.getResources().getColor(R.color.rec_view_main));
        }
        ((TextView) holder.item.findViewById(R.id.file_or_dir_name)).setText(_dataset[position].getName());
    }

    @Override
    public int getItemCount() {
        return _dataset.length;
    }

    public Observable<IFileOrFolderItem> itemClicked() {
        return _itemClickedSubj;
    }
}
