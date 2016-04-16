package com.kuassivi.october.compiler;

import com.kuassivi.october.annotation.ApplicationComponent;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;

public class ApplicationAnnotatedClass extends DefaultAnnotatedClass {

    private String applicationModuleQualifiedClassName;
    private String applicationModuleSimpleTypeName;
    private String activityModuleQualifiedClassName;
    private String activityModuleSimpleTypeName;
    private String fragmentModuleQualifiedClassName;
    private String fragmentModuleSimpleTypeName;

    public ApplicationAnnotatedClass(TypeElement classElement) throws IllegalArgumentException {
        super(classElement);
        ApplicationComponent annotation = classElement.getAnnotation(ApplicationComponent.class);

        // Get the full QualifiedTypeName
        try {
            Class<?> applicationClass = annotation.application();
            applicationModuleQualifiedClassName = applicationClass.getCanonicalName();
            applicationModuleSimpleTypeName = applicationClass.getSimpleName();
        } catch (MirroredTypeException mte) {
            DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirror();
            TypeElement classTypeElement = (TypeElement) classTypeMirror.asElement();
            applicationModuleQualifiedClassName = classTypeElement.getQualifiedName().toString();
            applicationModuleSimpleTypeName = classTypeElement.getSimpleName().toString();
        }

        // Get the full QualifiedTypeName
        try {
            Class<?> activityClass = annotation.activity();
            activityModuleQualifiedClassName = activityClass.getCanonicalName();
            activityModuleSimpleTypeName = activityClass.getSimpleName();
        } catch (MirroredTypeException mte) {
            DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirror();
            TypeElement classTypeElement = (TypeElement) classTypeMirror.asElement();
            activityModuleQualifiedClassName = classTypeElement.getQualifiedName().toString();
            activityModuleSimpleTypeName = classTypeElement.getSimpleName().toString();
        }

        // Get the full QualifiedTypeName
        try {
            Class<?> fragmentClass = annotation.fragment();
            fragmentModuleQualifiedClassName = fragmentClass.getCanonicalName();
            fragmentModuleSimpleTypeName = fragmentClass.getSimpleName();
        } catch (MirroredTypeException mte) {
            DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirror();
            TypeElement classTypeElement = (TypeElement) classTypeMirror.asElement();
            fragmentModuleQualifiedClassName = classTypeElement.getQualifiedName().toString();
            fragmentModuleSimpleTypeName = classTypeElement.getSimpleName().toString();
        }

        if (applicationModuleQualifiedClassName.isEmpty()
            || activityModuleQualifiedClassName.isEmpty()
            || fragmentModuleQualifiedClassName.isEmpty()) {
            throw new IllegalArgumentException(
                    String.format("Some arguments in @%s for class %s are null or empty! "
                                  + "You must provide Application, Activity and Fragment modules.",
                                  ApplicationComponent.class.getSimpleName(),
                                  classElement.getQualifiedName().toString()));
        }
    }

    public String getApplicationModuleQualifiedClassName() {
        return applicationModuleQualifiedClassName;
    }

    public String getApplicationModuleSimpleTypeName() {
        return applicationModuleSimpleTypeName;
    }

    public String getActivityModuleQualifiedClassName() {
        return activityModuleQualifiedClassName;
    }

    public String getActivityModuleSimpleTypeName() {
        return activityModuleSimpleTypeName;
    }

    public String getFragmentModuleQualifiedClassName() {
        return fragmentModuleQualifiedClassName;
    }

    public String getFragmentModuleSimpleTypeName() {
        return fragmentModuleSimpleTypeName;
    }
}
