package com.kuassivi.compiler;

import javax.lang.model.element.TypeElement;

public class DefaultAnnotatedClass {

    private TypeElement annotatedClassElement;

    public DefaultAnnotatedClass(TypeElement classElement) throws IllegalArgumentException {
        annotatedClassElement = classElement;
    }

    public String getQualifiedName() {
        return annotatedClassElement.getQualifiedName().toString();
    }

    public String getSimpleName() {
        return annotatedClassElement.getSimpleName().toString();
    }

    public TypeElement getAnnotatedClassElement() {
        return annotatedClassElement;
    }
}
