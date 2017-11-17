package com.example.nazmuddinmavliwala.awesomeadapter;

import android.content.Context;

import com.example.awesomeadapterannotation.AwesomeDelegates;
import com.example.awesomeadapterannotation.Delegate;
import com.example.nazmuddinmavliwala.awesomeadapter.adapters.BaseAwesomeAdapter;

import java.util.List;

/**
 * Created by nazmuddinmavliwala on 18/10/16.
 */

@AwesomeDelegates({
        @Delegate(
                viewHolder = EducationViewHolder.class,
                layout = R.layout.education_row,
                viewHolderInteractor = EducationViewHolderInteractor.class,
                dataObject = EducationObject.class
        ),
        @Delegate(
                viewHolder = AddEducationViewHolder.class,
                layout = R.layout.add_education_row,
                viewHolderInteractor = AddEducationViewHolderInteractor.class,
                dataObject = AddEducationObject.class
        )
})
public class EducationAdapter extends BaseAwesomeAdapter
        implements EducationViewHolderInteractor, AddEducationViewHolderInteractor {


    public EducationAdapter(Context context, List<Object> items) {
        super(context, items);
    }

    @Override
    public void onEditEducationClick(int adapterPosition) {}

    @Override
    public void onDeleteEducationClick(int adapterPosition) {}

    @Override
    public int getInfoObjectCount() {
        int i = 0;
        for (Object o : items) {
            if(o instanceof EducationObject) {
                i++;
            }
        }
        return i;
    }

    @Override
    public void onAddEducationClick(int adapterPosition) {}
}
