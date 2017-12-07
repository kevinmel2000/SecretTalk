package com.training.leos.secrettalk.util;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.training.leos.secrettalk.R;

public class ItemClickSupport {
    private final RecyclerView recyclerView;
    private ItemClickContract.OnItemClickListener onItemClickListener;

    public static ItemClickSupport addTo(RecyclerView recyclerView) {
        ItemClickSupport clickSupport = (ItemClickSupport) recyclerView.getTag(R.id.item_click_support);
        if (clickSupport == null) {
            clickSupport = new ItemClickSupport(recyclerView);
        }
        return clickSupport;
    }

    public ItemClickSupport removeFrom(RecyclerView recyclerView) {
        ItemClickSupport clickSupport = (ItemClickSupport) recyclerView.getTag(R.id.item_click_support);
        if (clickSupport != null) {
            recyclerView.removeOnChildAttachStateChangeListener(onChildAttachStateChangeListener);
            recyclerView.setTag(R.id.item_click_support, null);
        }
        return clickSupport;
    }

    public ItemClickSupport setOnItemClickListener(ItemClickContract.OnItemClickListener listener) {
        onItemClickListener = listener;
        return this;
    }


    RecyclerView.OnChildAttachStateChangeListener onChildAttachStateChangeListener =
            new RecyclerView.OnChildAttachStateChangeListener() {
                @Override
                public void onChildViewAttachedToWindow(View view) {
                    if (onItemClickListener != null) {
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (onItemClickListener != null) {
                                    RecyclerView.ViewHolder viewHolder = recyclerView.getChildViewHolder(v);
                                    onItemClickListener.onItemClicked(
                                            recyclerView, viewHolder.getAdapterPosition(), v);
                                }
                            }
                        });
                    }
                }

                @Override
                public void onChildViewDetachedFromWindow(View view) {

                }
            };

    public ItemClickSupport(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        this.recyclerView.setTag(R.id.item_click_support, this);
        this.recyclerView.addOnChildAttachStateChangeListener(onChildAttachStateChangeListener);
    }
}
