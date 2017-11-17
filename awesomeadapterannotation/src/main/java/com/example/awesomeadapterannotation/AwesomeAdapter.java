package com.example.awesomeadapterannotation;

/**
 * Created by nazmuddinmavliwala on 03/08/16.
 */
public class AwesomeAdapter {

    public static final String AUTO_MAPPING_CLASS_NAME = "AutoDelegateBinder";
    public static final String AUTO_MAPPING_PACKAGE = "com.example.nazmuddinmavliwala.awesomeadapter";
    public static final String AUTO_MAPPING_QUALIFIED_CLASS =
            AUTO_MAPPING_PACKAGE + "." + AUTO_MAPPING_CLASS_NAME;

    private static DelegateBinder autoMappingBinder;

    public static void bind(Object target) {

        if(autoMappingBinder == null) {
            try {
                Class<?> c = Class.forName(AUTO_MAPPING_QUALIFIED_CLASS);
                autoMappingBinder = (DelegateBinder) c.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(autoMappingBinder != null) {
            autoMappingBinder.bind(target);
        }
    }

}
