package com.example.awesomeadapterprocessor;

import javax.lang.model.element.TypeElement;

/**
 * Created by nazmuddinmavliwala on 02/08/16.
 */
public class DelegateModel {
    private String className;
    private String viewHolderInteractor;
    private String dataObject;
    private int layout;
    private TypeElement typeElement;
    private String viewHolder;
    private String viewHolderPackageName;
    private String viewHolderInteractorPackageName;
    private String dataObjectPackageName;
    private boolean useContext;

    public void setClassName(String className) {
        this.className = className;
    }

    public void setViewHolderInteractor(String viewHolderInteractor) {
        this.viewHolderInteractor = viewHolderInteractor;
    }

    public void setDataObject(String dataObject) {
        this.dataObject = dataObject;
    }

    public void setLayout(int layout) {
        this.layout = layout;
    }

    public String getClassName() {
        return className;
    }

    public String getViewHolderInteractor() {
        return viewHolderInteractor;
    }

    public String getDataObject() {
        return dataObject;
    }

    public TypeElement getTypeElement() {
        return typeElement;
    }

    public void setTypeElement(TypeElement typeElement) {
        this.typeElement = typeElement;
    }

    public void setViewHolder(String viewHolder) {
        this.viewHolder = viewHolder;
    }

    public String getViewHolder() {
        return viewHolder;
    }

    public void setViewHolderPackageName(String viewHolderPackageName) {
        this.viewHolderPackageName = viewHolderPackageName;
    }

    public void setViewHolderInteractorPackageName(String viewHolderInteractorPackageName) {
        this.viewHolderInteractorPackageName = viewHolderInteractorPackageName;
    }

    public void setDataObjectPackageName(String dataObjectPackageName) {
        this.dataObjectPackageName = dataObjectPackageName;
    }

    public String getViewHolderPackageName() {
        return viewHolderPackageName;
    }

    public String getViewHolderInteractorPackageName() {
        return viewHolderInteractorPackageName;
    }

    public int getLayout() {
        return layout;
    }

    public String getDataObjectPackageName() {
        return dataObjectPackageName;
    }

    public void useContext(boolean useContext) {
        this.useContext = useContext;
    }

    public boolean isUseContext() {
        return useContext;
    }
}
