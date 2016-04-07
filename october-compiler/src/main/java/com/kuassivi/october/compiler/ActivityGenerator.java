package com.kuassivi.october.compiler;

import com.google.auto.common.SuperficialValidation;

import com.kuassivi.october.Config;
import com.kuassivi.october.annotation.ActivityComponent;
import com.kuassivi.october.annotation.FragmentComponent;
import com.kuassivi.october.annotation.PerActivity;
import com.kuassivi.october.di.OctoberPresenterActivityInjectable;
import com.kuassivi.october.di.component.BaseActivityComponent;
import com.kuassivi.october.di.component.internal.BaseHelperActivityComponent;
import com.kuassivi.october.di.component.internal.BaseHelperFragmentComponent;
import com.kuassivi.october.di.module.BaseActivityModule;
import com.kuassivi.october.mvp.contract.Presentable;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.inject.Inject;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import dagger.Lazy;
import dagger.Subcomponent;

public class ActivityGenerator {

    Map<String, DefaultAnnotatedClass> activityMap;
    Map<String, String>                presenterMap;
    private Types                     typeUtils;
    private Elements                  elementUtils;
    private Filer                     filer;
    private ApplicationAnnotatedClass applicationAnnotatedClass;
    private String pkg_di;
    private String pkg_di_component;
    private String pkg_di_component_helper;

    public ActivityGenerator(Types typeUtils,
                             Elements elementUtils,
                             Filer filer,
                             ApplicationAnnotatedClass applicationAnnotatedClass) {
        this.applicationAnnotatedClass = applicationAnnotatedClass;
        this.elementUtils = elementUtils;
        this.typeUtils = typeUtils;
        this.filer = filer;

        PackageElement pkg = elementUtils.getPackageOf(
                applicationAnnotatedClass.getAnnotatedClassElement());
        String packageName = pkg.isUnnamed()
                             ? null
                             : pkg.getQualifiedName().toString();

        pkg_di = packageName + ".internal.october.di";
        pkg_di_component = pkg_di + ".component";
        pkg_di_component_helper = pkg_di_component + ".helper";

        activityMap = new LinkedHashMap<>();
        presenterMap = new LinkedHashMap<>();
    }

    public void process(Set<? extends Element> activityComponentSet,
                        Set<? extends Element> perActivitySet)
            throws ProcessingException, IllegalArgumentException, IOException {

        findActivities(activityComponentSet);

        findPresenters(perActivitySet);

        generateCode();
    }

    private void findActivities(Set<? extends Element> activityComponentSet)
            throws ProcessingException {
        for (Element annotatedElement : activityComponentSet) {
            // Check if a Class has been annotated with @OctoberActivity
            if (annotatedElement.getKind() != ElementKind.CLASS) {
                throw new ProcessingException(annotatedElement,
                                              "Only classes can be annotated with @%s",
                                              ActivityComponent.class.getSimpleName());
            }
            if (!SuperficialValidation.validateElement(annotatedElement)) {
                continue;
            }
            // We can cast it, because we know that it's of ElementKind.CLASS
            TypeElement currentClass = (TypeElement) annotatedElement;

            if (Utils.containsTypeParameters(currentClass)) {
                throw new ProcessingException(annotatedElement,
                                              "Classes with Type Parameters cannot be annotated "
                                              + "with @%s",
                                              ActivityComponent.class.getSimpleName());
            }

            DefaultAnnotatedClass annotatedClass = new DefaultAnnotatedClass(currentClass);
            String qualifiedClassName = annotatedClass.getQualifiedName();
            while (true) {
                TypeMirror superClassType = currentClass.getSuperclass();
                if (superClassType.getKind() == TypeKind.NONE) {
                    throw new ProcessingException(annotatedClass.getAnnotatedClassElement(),
                                                  "%s must inherits from %s",
                                                  annotatedClass.getSimpleName(),
                                                  Config.OCTOBER_ACTIVITY_CLASS_SIMPLE_NAME);
                }
                TypeElement superClassElement =
                        (TypeElement) typeUtils.asElement(superClassType);
                String className = superClassElement.getQualifiedName().toString();
                if (className.equals(Config.OCTOBER_ACTIVITY_CLASS)
                    || className.equals(Config.OCTOBER_APPCOMPAT_CLASS)) {
                    DefaultAnnotatedClass storedClass = activityMap.get(qualifiedClassName);
                    if (storedClass == null) {
                        activityMap.put(qualifiedClassName, annotatedClass);
                        Utils.note("Processing class " + annotatedClass.getSimpleName());
                    }
                    break;
                } else if (className.equals(Config.OCTOBER_FRAGMENT_CLASS)
                           || className.equals(Config.OCTOBER_PRESENTER_CLASS)) {

                    String correctAnnotation = className.equals(Config.OCTOBER_FRAGMENT_CLASS)
                                               ? FragmentComponent.class.getSimpleName()
                                               : null;

                    throw new ProcessingException(annotatedClass.getAnnotatedClassElement(),
                                                  "%1$s class is annotated with @%2$s "
                                                  + "but does not inherits from %3$s. "
                                                  + (correctAnnotation != null
                                                     ? "%1$s class must be annotated with @%4$s"
                                                     : ""),
                                                  annotatedClass.getSimpleName(),
                                                  ActivityComponent.class.getSimpleName(),
                                                  Config.OCTOBER_ACTIVITY_CLASS_SIMPLE_NAME,
                                                  correctAnnotation);
                }
                // Moving up in inheritance tree
                currentClass = (TypeElement) typeUtils.asElement(superClassType);
            }
            if (activityMap.get(qualifiedClassName) == null) {
                Utils.warning(annotatedClass.getAnnotatedClassElement(),
                              String.format("Skipped %s.class because is annotated with @%s "
                                            + "but it doesn't inherits from %s or %s",
                                            annotatedClass.getSimpleName(),
                                            ActivityComponent.class.getSimpleName(),
                                            Config.OCTOBER_ACTIVITY_CLASS_SIMPLE_NAME,
                                            Config.OCTOBER_APPCOMPAT_CLASS_SIMPLE_NAME));
            }
        }
    }

    private void findPresenters(Set<? extends Element> perActivitySet)
            throws ProcessingException {
        for (Element annotatedElement : perActivitySet) {
            if (annotatedElement.getKind() == ElementKind.CLASS) {
                if (!SuperficialValidation.validateElement(annotatedElement)) {
                    continue;
                }
                // We can cast it, because we know that it's of ElementKind.CLASS
                TypeElement currentPresenter = (TypeElement) annotatedElement;
                TypeElement currentClass = currentPresenter;
                if (Utils.inheritsFromClass(currentClass, Config.OCTOBER_PRESENTER_CLASS)) {
                    while (true) {
                        TypeMirror superClassType = currentClass.getSuperclass();
                        TypeElement superClassElement =
                                (TypeElement) typeUtils.asElement(superClassType);
                        if (superClassElement.getQualifiedName().toString()
                                             .equals(Config.OCTOBER_PRESENTER_CLASS)) {
                            // check for Interface of the presenter!
                            List<? extends TypeMirror> interfaces = currentPresenter
                                    .getInterfaces();
                            TypeElement found =
                                    Utils.containInterface(interfaces,
                                                           Presentable.class.getCanonicalName());
                            if (found == null) {
                                throw new ProcessingException(currentPresenter,
                                                              "%s must contain an interface "
                                                              + "that inherits from %s",
                                                              currentPresenter.getSimpleName(),
                                                              Presentable.class.getSimpleName());
                            } else {
                                presenterMap.put(
                                        found.getQualifiedName().toString(),
                                        currentPresenter.getQualifiedName().toString());
                                Utils.note("Processing class " + currentPresenter.getSimpleName());
                            }
                            break;
                        }
                        // Moving up in inheritance tree
                        currentClass = (TypeElement) typeUtils.asElement(superClassType);
                    }
                }
            }
        }
    }

    private void generateCode() throws IOException {

        createPresenterActivityInjector();

        createActivityComponent();

        createHelperActivityComponent();
    }

    private void createPresenterActivityInjector()
            throws IOException {

        TypeSpec.Builder presenterActivityInjectorClassBuilder =
                TypeSpec.classBuilder(Config.PRESENTER_ACTIVITY_INJECTOR)
                        .addModifiers(Modifier.PUBLIC)
                        .superclass(OctoberPresenterActivityInjectable.class);

        //add Presenter members
        MethodSpec.Builder provideMethod = MethodSpec.overriding(Utils.findFirstElement(
                OctoberPresenterActivityInjectable.class, Config.PRESENTER_INJECTOR_METHOD));

        if (!presenterMap.isEmpty()) {

            int round = 1;

            for (Map.Entry<String, String> element : presenterMap.entrySet()) {

                ParameterizedTypeName memberType =
                        ParameterizedTypeName.get(
                                ClassName.get(Lazy.class),
                                ClassName.bestGuess(element.getValue()));
                String member = "presenterLazy" + round;
                presenterActivityInjectorClassBuilder
                        .addField(
                                FieldSpec.builder(memberType, member)
                                         .addAnnotation(Inject.class)
                                         .build());
                provideMethod.beginControlFlow("if (arg0.equals($T.class))",
                                               ClassName.bestGuess(element.getKey()))
                             .addStatement("return $L", member)
                             .endControlFlow();
                round++;
            }

            provideMethod = provideMethod.addStatement("return null");
        } else {
            provideMethod = provideMethod.addStatement("return null");
        }

        presenterActivityInjectorClassBuilder.addMethod(provideMethod.build());

        TypeSpec typeSpec = presenterActivityInjectorClassBuilder.build();
        JavaFile.builder(pkg_di, typeSpec).build().writeTo(filer);
    }

    private void createActivityComponent() throws IOException {

        AnnotationSpec daggerSubComponentAnnotation =
                AnnotationSpec
                        .builder(Subcomponent.class)
                        .addMember("modules",
                                   "{$T.class, $T.class}",
                                   ClassName.bestGuess(
                                           applicationAnnotatedClass
                                                   .getActivityModuleQualifiedClassName()
                                   ),
                                   ClassName.get(BaseActivityModule.class))
                        .build();

        Collection<DefaultAnnotatedClass> collection = activityMap.values();

        ParameterizedTypeName baseInherits =
                ParameterizedTypeName.get(
                        ClassName.get(BaseActivityComponent.class),
                        ClassName.get(BaseHelperFragmentComponent.class),
                        ClassName.get(pkg_di, Config.PRESENTER_ACTIVITY_INJECTOR));

        // helper method
        MethodSpec.Builder helper =
                MethodSpec.methodBuilder(Config.HELPER_FRAGMENT_COMPONENT_METHOD)
                          .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                          .returns(ClassName.get(pkg_di_component_helper,
                                                 Config.HELPER_FRAGMENT_COMPONENT));

        MethodSpec.Builder injectMethod =
                MethodSpec.methodBuilder(Config.COMPONENT_INJECTOR_METHOD)
                          .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                          .addParameter(ParameterSpec.builder(
                                  ClassName.get(pkg_di,
                                                Config.PRESENTER_ACTIVITY_INJECTOR),
                                  "injector").build())
                          .returns(TypeName.VOID);

        TypeSpec.Builder classBuilder =
                TypeSpec.interfaceBuilder(Config.ACTIVITY_COMPONENT)
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(PerActivity.class)
                        .addAnnotation(daggerSubComponentAnnotation)
                        .addSuperinterface(baseInherits)

                        .addMethod(helper.build())
                        .addMethod(injectMethod.build());

        //add rest of injected activities views
        for (DefaultAnnotatedClass annotatedClass : collection) {
            MethodSpec method =
                    MethodSpec.methodBuilder(Config.COMPONENT_INJECTOR_METHOD)
                              .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                              .addParameter(ParameterSpec.builder(
                                      ClassName.bestGuess(annotatedClass.getQualifiedName()),
                                      "view").build())
                              .returns(TypeName.VOID).build();
            classBuilder.addMethod(method);
        }

        TypeSpec typeSpec = classBuilder.build();
        JavaFile.builder(pkg_di_component, typeSpec).build().writeTo(filer);
    }

    private void createHelperActivityComponent() throws IOException {

        ParameterizedTypeName baseInterface =
                ParameterizedTypeName.get(
                        ClassName.get(BaseHelperActivityComponent.class),
                        ClassName.get(BaseActivityComponent.class));

        MethodSpec.Builder applyMethod =
                MethodSpec.methodBuilder(Config.HELPER_COMPONENT_METHOD)
                          .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                          .addParameter(ParameterSpec.builder(
                                  ClassName.get(BaseActivityModule.class), "module").build())
                          .returns(ClassName.get(pkg_di_component,
                                                 Config.ACTIVITY_COMPONENT));

        TypeSpec.Builder classBuilder =
                TypeSpec.interfaceBuilder(Config.HELPER_ACTIVITY_COMPONENT)
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(PerActivity.class)
                        .addAnnotation(AnnotationSpec
                                               .builder(Subcomponent.class)
                                               .build())
                        .addSuperinterface(baseInterface)

                        .addMethod(applyMethod.build());

        TypeSpec typeSpec = classBuilder.build();
        JavaFile.builder(pkg_di_component_helper, typeSpec).build().writeTo(filer);
    }

    public Map<String, DefaultAnnotatedClass> getActivityMap() {
        return activityMap;
    }

    public Map<String, String> getPresenterMap() {
        return presenterMap;
    }
}
