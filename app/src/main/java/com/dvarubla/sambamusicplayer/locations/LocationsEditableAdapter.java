package com.dvarubla.sambamusicplayer.locations;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.dvarubla.sambamusicplayer.R;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.ArrayList;

import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.subjects.PublishSubject;

public class LocationsEditableAdapter extends RecyclerView.Adapter<LocationsEditableAdapter.ViewHolder> implements DraggableItemAdapter<LocationsEditableAdapter.ViewHolder> {
    public String[] getStrings() {
        String arr[] = new String[_dataset.size()];
        int i = 0;
        for(StringAndId sid : _dataset){
            arr[i] = sid.text;
            i++;
        }
        return arr;
    }

    public void setStrings(String[] strings) {
        _dataset = new ArrayList<>();
        for(String str : strings){
            _lastId++;
            _dataset.add(new StringAndId(_lastId, str));
        }
        notifyDataSetChanged();
    }

    class StringAndId{
        final long id;
        String text;

        StringAndId(long id, String text) {
            this.id = id;
            this.text = text;
        }
    }

    static class ViewHolder extends AbstractDraggableItemViewHolder {
        LinearLayout item;
        ViewHolder(LinearLayout v) {
            super(v);
            item = v;
        }
    }

    private PublishSubject<StringAndId> _subj;
    private ArrayList<StringAndId> _dataset;
    private int _lastId;


    @Override
    public boolean onCheckCanStartDrag(ViewHolder holder, int position, int x, int y) {
        return true;
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(ViewHolder holder, int position) {
        return null;
    }

    @Override
    public void onMoveItem(int fromPosition, int toPosition) {
        _dataset.add(toPosition, _dataset.remove(fromPosition));
    }

    @Override
    public boolean onCheckCanDrop(int draggingPosition, int dropPosition) {
        return true;
    }

    @Override
    public void onItemDragStarted(int position) {
        notifyDataSetChanged();
    }

    @Override
    public void onItemDragFinished(int fromPosition, int toPosition, boolean result) {
        notifyDataSetChanged();
    }

    @SuppressLint("CheckResult")
    LocationsEditableAdapter(){
        _dataset = new ArrayList<>();
        setHasStableIds(true);
        _lastId = 0;
        _subj = PublishSubject.create();
        _subj.subscribe(new Consumer<StringAndId>() {
            @Override
            public void accept(StringAndId stringAndId) {
                _dataset.get((int) stringAndId.id).text = stringAndId.text;
            }
        });
    }

    @Override
    public LocationsEditableAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                  int viewType) {
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.editable_location_item, parent, false);
        EditText ed = v.findViewById(R.id.location_edit_text);
        final ViewHolder holder= new ViewHolder(v);
        RxTextView.textChanges(ed).skipInitialValue().map(new Function<CharSequence, StringAndId>() {
            @Override
            public StringAndId apply(CharSequence charSequence){
                return new StringAndId(holder.getAdapterPosition(), charSequence.toString());
            }
        }).subscribe(_subj);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String text = _dataset.get(position).text;
        EditText ed = holder.item.findViewById(R.id.location_edit_text);
        ed.setText(text);
        ed.clearFocus();
    }

    @Override
    public int getItemCount() {
        return _dataset.size();
    }

    @Override
    public long getItemId(int position) {
        return _dataset.get(position).id;
    }
}
