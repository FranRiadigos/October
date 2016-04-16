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

import java.util.List;
import java.util.Set;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * @author Francisco Gonzalez-Armijo
 */
public class Utils {

    public static  Types    typeUtils;
    private static Elements elementUtils;
    private static Messager messager;

    public static void initialize(ProcessingEnvironment env) {
        typeUtils = env.getTypeUtils();
        messager = env.getMessager();
        elementUtils = env.getElementUtils();
    }

    /**
     * Prints a Note message.
     *
     * @param message The note message
     */
    public static void note(String message) {
        messager.printMessage(Diagnostic.Kind.NOTE, message);
    }

    /**
     * Prints a Note message.
     *
     * @param e       The element which has caused the note. Can be null
     * @param message The note message
     */
    public static void note(Element e, String message) {
        messager.printMessage(Diagnostic.Kind.NOTE, message, e);
    }

    /**
     * Prints a Warning message.
     *
     * @param message The warning message
     */
    public static void warning(String message) {
        messager.printMessage(Diagnostic.Kind.WARNING, message);
    }

    /**
     * Prints a Warning message.
     *
     * @param e       The element which has caused the warning. Can be null
     * @param message The warning message
     */
    public static void warning(Element e, String message) {
        messager.printMessage(Diagnostic.Kind.WARNING, message, e);
    }

    /**
     * Prints an Error message.
     *
     * @param e       The element which has caused the error. Can be null
     * @param message The error message
     */
    public static void error(Element e, String message) {
        messager.printMessage(Diagnostic.Kind.ERROR, message, e);
    }

    /**
     * Prints multiple Error messages.
     *
     * @param e        The element which has caused the error. Can be null
     * @param messages The error messages
     */
    public static void errors(Element e, Set<String> messages) {
        for (String msg : messages) {
            messager.printMessage(Diagnostic.Kind.ERROR, msg, e);
        }
    }

    /**
     * Retrieves an {@link ExecutableElement} from a Class by the provided Method Name.
     * <p>
     * This may be useful for overriding methods.
     *
     * @param clazz      The class to look for the Method Name
     * @param methodName The method name to be retrieved from the class
     * @return The method element
     */
    public static ExecutableElement findFirstElement(Class<?> clazz, String methodName) {
        TypeElement classElement = elementUtils.getTypeElement(clazz.getCanonicalName());
        List<ExecutableElement> methods = ElementFilter.methodsIn(
                elementUtils.getAllMembers(classElement));
        for (ExecutableElement executableElement : methods) {
            if (executableElement.getSimpleName().toString().equals(methodName)) {
                return executableElement;
            }
        }
        throw new IllegalArgumentException(methodName + " not found in " + methods);
    }

    /**
     * Retrieves the TypeElement found in a list of Interfaces based on the provided className.
     *
     * @param interfaces List of interfaces to look on
     * @param className  The specific interface class to look for
     * @return The TypeElement based based on the class Name
     */
    public static TypeElement containInterface(List<? extends TypeMirror> interfaces,
                                               String className) {
        for (TypeMirror type : interfaces) {
            TypeElement typeElement = (TypeElement) typeUtils.asElement(type);
            if (typeElement.getQualifiedName().toString().equals(className)) {
                return typeElement;
            } else if (Utils.containInterface(typeElement.getInterfaces(), className) != null) {
                return typeElement;
            }
        }
        return null;
    }

    /**
     * Checks whether an Element inherits form a Class.
     *
     * @param currentClass   The element to inspect
     * @param superClassName The class to inherits from
     * @return true if inherits from that className, false otherwise
     */
    public static boolean inheritsFromClass(TypeElement currentClass, String superClassName) {
        while (true) {
            TypeMirror superClassType = currentClass.getSuperclass();
            if (superClassType.getKind() == TypeKind.NONE) {
                return false;
            }
            TypeElement superClassElement =
                    (TypeElement) typeUtils.asElement(superClassType);
            if (superClassElement.getQualifiedName().toString()
                                 .equals(superClassName)) {
                return true;
            }
            // Moving up in inheritance tree
            currentClass = (TypeElement) typeUtils.asElement(superClassType);
        }
    }

    /**
     * Checks for a TypeElement whether it contains Type Parameters.
     *
     * @param currentClass The element to inspect
     * @return true if contains Type Parameters, false otherwise
     */
    public static boolean containsTypeParameters(TypeElement currentClass) {
        return currentClass.getTypeParameters().size() > 0;
    }
}
