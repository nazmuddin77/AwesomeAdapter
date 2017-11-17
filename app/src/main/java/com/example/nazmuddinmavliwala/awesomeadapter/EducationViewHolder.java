package com.example.nazmuddinmavliwala.awesomeadapter;

import android.content.Context;
import android.view.View;

import com.example.nazmuddinmavliwala.awesomeadapter.adapters.BaseEmployeeViewHolder;

/**
 * Created by nazmuddinmavliwala on 18/10/16.
 */
public class EducationViewHolder extends BaseEmployeeViewHolder<EducationObject> {

    private final Context context;
    private final EducationViewHolderInteractor interactor;
    private static final String LINE_SEPARATOR = "<br>";

    public EducationViewHolder(View itemView, Context context
            , EducationViewHolderInteractor interactor) {
        super(itemView);
        this.context = context;
        this.interactor = interactor;
    }

    @Override
    public void bindViews(EducationObject educationObject) {

    }
}
