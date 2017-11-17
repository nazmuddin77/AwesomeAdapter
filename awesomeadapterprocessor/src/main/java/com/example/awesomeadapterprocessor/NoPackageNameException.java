package com.example.awesomeadapterprocessor;

import javax.lang.model.element.TypeElement;

public class NoPackageNameException extends Exception {

  public NoPackageNameException(TypeElement typeElement) {
    super("The package of " + typeElement.getSimpleName() + " has no name");
  }
}
