package com.dvarubla.sambamusicplayer.locations;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dvarubla.sambamusicplayer.R;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionDefault;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionRemoveItem;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.annotation.SwipeableItemResults;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableSwipeableItemViewHolder;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.ArrayList;

import io.reactivex.subjects.PublishSubject;

public class LocationsEditableAdapter extends RecyclerView.Adapter<LocationsEditableAdapter.ViewHolder>
        implements DraggableItemAdapter<LocationsEditableAdapter.ViewHolder>,
        SwipeableItemAdapter<LocationsEditableAdapter.ViewHolder> {

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

    public void addNewString(String location) {
        _lastId++;
        _dataset.add(new StringAndId(_lastId, location));
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

    static class ViewHolder extends AbstractDraggableSwipeableItemViewHolder {
        LinearLayout item;
        LinearLayout container;
        EditText edit;
        ImageView dragHanlde;
        ViewHolder(LinearLayout v) {
            super(v);
            item = v;
            container = v.findViewById(R.id.location_edit_item_wrap);
            dragHanlde = v.findViewById(R.id.location_drag_handle);
            edit = v.findViewById(R.id.location_edit_text);
        }

        @Override
        public View getSwipeableContainerView() {
            return container;
        }
    }

    private PublishSubject<StringAndId> _subj;
    private ArrayList<StringAndId> _dataset;
    private int _lastId;

    private boolean hitTest(View v, int x, int y) {
        final int tx = (int) (v.getTranslationX() + 0.5f);
        final int ty = (int) (v.getTranslationY() + 0.5f);
        final int left = v.getLeft() + tx;
        final int right = v.getRight() + tx;
        final int top = v.getTop() + ty;
        final int bottom = v.getBottom() + ty;

        return (x >= left) && (x <= right) && (y >= top) && (y <= bottom);
    }

    @Override
    public boolean onCheckCanStartDrag(ViewHolder holder, int position, int x, int y) {
        View containerView = holder.container;
        View dragHandleView = holder.dragHanlde;

        int offsetX = containerView.getLeft() + (int) (containerView.getTranslationX() + 0.5f);
        int offsetY = containerView.getTop() + (int) (containerView.getTranslationY() + 0.5f);

        return hitTest(dragHandleView, x - offsetX, y - offsetY);
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
        _subj.subscribe(stringAndId -> _dataset.get((int) stringAndId.id).text = stringAndId.text);
    }

    @Override
    public LocationsEditableAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                  int viewType) {
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.editable_location_item, parent, false);
        final ViewHolder holder= new ViewHolder(v);
        RxTextView.textChanges(holder.edit).skipInitialValue().map(
                seq -> new StringAndId(holder.getAdapterPosition(), seq.toString())
        ).subscribe(_subj);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if(position % 2 == 1) {
            holder.item.setBackgroundColor(holder.item.getResources().getColor(R.color.rec_view_alt));
        } else {
            holder.item.setBackgroundColor(holder.item.getResources().getColor(R.color.rec_view_main));
        }
        String text = _dataset.get(position).text;
        holder.edit.setText(text);
    }

    @Override
    public int getItemCount() {
        return _dataset.size();
    }

    @Override
    public long getItemId(int position) {
        return _dataset.get(position).id;
    }

    static class RemoveAction extends SwipeResultActionRemoveItem {
        private LocationsEditableAdapter _adapter;
        private int _position;

        RemoveAction(LocationsEditableAdapter adapter, int position) {
            this._adapter = adapter;
            this._position = position;
        }

        @Override
        protected void onPerformAction() {
            _adapter._dataset.remove(_position);
            _adapter.notifyItemRemoved(_position);
        }
    }

    @Override
    public int onGetSwipeReactionType(ViewHolder holder, int position, int x, int y) {
        if(holder.edit.isFocused()){
            return SwipeableItemConstants.REACTION_CAN_NOT_SWIPE_ANY;
        }
        return SwipeableItemConstants.REACTION_CAN_SWIPE_LEFT;
    }

    @Override
    public void onSwipeItemStarted(ViewHolder holder, int position) {
        notifyDataSetChanged();
    }

    @Override
    public void onSetSwipeBackground(ViewHolder holder, int position, int type) {

    }

    @Override
    public SwipeResultAction onSwipeItem(ViewHolder holder, int position, @SwipeableItemResults int result) {
        if (result == SwipeableItemConstants.RESULT_CANCELED) {
            return new SwipeResultActionDefault();
        } else {
            return new RemoveAction(this, position);
        }
    }
}
