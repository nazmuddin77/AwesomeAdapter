package com.example.awesomeadapterprocessor;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * Created by nazmuddinmavliwala on 02/08/16.
 */
public class ClassValidator {

    public static boolean isPublic(Element element) {
        return element.getModifiers().contains(PUBLIC);
    }

    public static boolean isAbstract(Element element) {
        return element.getModifiers().contains(ABSTRACT);
    }

    public static boolean isInterface(Element element) {
        return element.getKind().isInterface();
    }

    public static boolean isAdapterClass(Types typeUtils, TypeElement classElement
            , TypeElement superClassTypeElement) {
        if (superClassTypeElement != null) {
            if (typeUtils.isSubtype(classElement.asType(),
                    superClassTypeElement.asType())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isViewHolderClass(Types typeUtils, TypeMirror typeMirror
            , TypeElement viewHolderType) {
        if (viewHolderType != null) {
            if (typeUtils.isSubtype(typeMirror,
                    viewHolderType.asType())) {
                return true;
            }
        }
        return false;
    }
}
