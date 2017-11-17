package com.example.nazmuddinmavliwala.awesomeadapter.adapters;

import android.content.Context;

import java.util.List;

/**
 * Created by nazmuddinmavliwala on 01/12/16.
 */
public abstract class BaseAwesomeAdapter extends BaseEmployeeRecyclerViewAdapter<Object> {

    public BaseAwesomeAdapter(Context context, List<Object> items) {
        super(context, items);
    }
}
