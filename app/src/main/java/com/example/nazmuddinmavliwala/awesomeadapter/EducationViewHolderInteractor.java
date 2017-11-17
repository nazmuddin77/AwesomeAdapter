package com.example.nazmuddinmavliwala.awesomeadapter;

/**
 * Created by nazmuddinmavliwala on 18/10/16.
 */
public interface EducationViewHolderInteractor {
    void onEditEducationClick(int adapterPosition);

    void onDeleteEducationClick(int adapterPosition);

    int getInfoObjectCount();
}
