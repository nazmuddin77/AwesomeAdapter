package com.example.nazmuddinmavliwala.awesomeadapter;

import android.content.Context;
import android.view.View;

import com.example.nazmuddinmavliwala.awesomeadapter.adapters.BaseEmployeeViewHolder;

/**
 * Created by nazmuddinmavliwala on 18/10/16.
 */
public class AddEducationViewHolder extends BaseEmployeeViewHolder<AddEducationObject> {

    private final Context context;
    private final AddEducationViewHolderInteractor interactor;

    public AddEducationViewHolder(View itemView, Context context
            , AddEducationViewHolderInteractor interactor) {
        super(itemView);
        this.context = context;
        this.interactor = interactor;
    }

    @Override
    public void bindViews(AddEducationObject addEducationObject) {

    }
}
