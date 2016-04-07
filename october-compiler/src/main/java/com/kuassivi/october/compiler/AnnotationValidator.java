/*******************************************************************************
 * Copyright (c) 2016 Francisco Gonzalez-Armijo Riádigos
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/

package com.kuassivi.october.compiler;

import java.lang.annotation.ElementType;
import java.util.HashSet;
import java.util.Set;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;

import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * Fluent validator object that checks for valid elements and returns a {@link Set} of error Strings
 * or an empty {@link Set}.
 *
 * @author Francisco Gonzalez-Armijo
 */
final class AnnotationValidator {

    private static AnnotationValidator instance;
    private Types       typeUtils;
    private TypeElement annotatedElement;
    private Class<?>    annotationClass;
    private Set<String> errors;

    private AnnotationValidator(Types typeUtils,
                                TypeElement annotatedElement,
                                Class<?> annotationClass) {
        this.errors = new HashSet<String>();
        this.annotatedElement = annotatedElement;
        this.annotationClass = annotationClass;
        this.typeUtils = typeUtils;
    }

    static AnnotationValidator with(Types typeUtils,
                                    TypeElement annotatedElement,
                                    Class<?> annotationClass) {
        return (instance == null)
               ? instance = new AnnotationValidator(typeUtils, annotatedElement, annotationClass)
               : instance;
    }

    /**
     * Checks whether is an accessible {@link ElementType}.
     *
     * Accessible means: public, default.
     *
     * @return AnnotationValidator
     */
    AnnotationValidator isAccessible(ElementKind elementType) {
        if (ElementKind.METHOD.equals(elementType)
            && !annotatedElement.getModifiers().contains(PUBLIC)) {
            addError("%s method is not accessible. "
                     + "It must have public, default or native modifier.",
                     annotatedElement.getSimpleName().toString());
        }
        return this;
    }

    /**
     * Checks whether is an abstract {@link ElementType}.
     *
     * @return AnnotationValidator
     */
    AnnotationValidator isAbstractAllowed(ElementKind elementType, boolean assertion) {
        if (ElementKind.METHOD.equals(elementType)
            && annotatedElement.getModifiers().contains(ABSTRACT) == assertion) {
            addError(
                    "%s method is abstract. "
                    + (assertion
                       ? "Only abstract methods can be annotated with @%s"
                       : "You can't annotate abstract methods with @%s"),
                    annotatedElement.getSimpleName().toString(),
                    annotationClass.getSimpleName());
        }
        return this;
    }

    /**
     * Checks whether is not an abstract {@link ElementType}.
     *
     * @return AnnotationValidator
     */
    AnnotationValidator isInterfaceAllowed(ElementKind elementType, boolean assertion) {
        if (ElementKind.METHOD.equals(elementType)
            && (annotatedElement.getEnclosingElement()
                                .getKind() == ElementKind.INTERFACE) != assertion) {
            addError(
                    "%s class is an Interface. "
                    + (assertion
                       ? "Only Interface classes can have methods annotated with @%s"
                       : "You can't annotate Interface methods with @%s"),
                    annotatedElement.getEnclosingElement().getSimpleName().toString(),
                    annotationClass.getSimpleName());
        }
        return this;
    }

    AnnotationValidator inheritsFromClass(String superClassName) {
        if (annotatedElement.getKind() == ElementKind.CLASS) {
            TypeElement currentClass = annotatedElement;
            if(!Utils.inheritsFromClass(currentClass, superClassName)) {
                addError("%s class is annotated with @%s but doesn't inherits from %s",
                         annotatedElement.getSimpleName(),
                         annotationClass.getSimpleName(),
                         superClassName);
            }
        } else {
            addError("Only classes can be annotated with @%s",
                     annotationClass.getSimpleName());
        }
        return this;
    }

    void addError(String msg, Object... args) {
        errors.add(String.format(msg, args));
    }

    public Set<String> getErrors() {
        return errors;
    }
}
