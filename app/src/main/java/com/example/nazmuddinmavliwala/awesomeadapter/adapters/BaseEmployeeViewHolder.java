package com.example.nazmuddinmavliwala.awesomeadapter.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by nazmuddinmavliwala on 17/12/15.
 */
public abstract class BaseEmployeeViewHolder<T> extends RecyclerView.ViewHolder  {

    private final View view;

    public View getView() {
        return view;
    }


    public BaseEmployeeViewHolder(View itemView) {
        super(itemView);
        this.view = itemView;
    }

    public abstract void bindViews(T t);

}
