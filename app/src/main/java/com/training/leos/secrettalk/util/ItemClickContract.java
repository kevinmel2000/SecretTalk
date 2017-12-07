package com.training.leos.secrettalk.util;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Leo on 03/12/2017.
 */

public interface ItemClickContract {
    interface OnItemClickListener {
        void onItemClicked(RecyclerView recyclerView, int position, View v);
    }
    interface OnItemLongClickListener {
        boolean onItemLongClicked(RecyclerView recyclerView, int position, View v);
    }
}
