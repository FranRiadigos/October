package com.kuassivi.compiler;

import com.google.auto.common.SuperficialValidation;

import com.kuassivi.october.Config;
import com.kuassivi.october.annotation.ActivityComponent;
import com.kuassivi.october.annotation.FragmentComponent;
import com.kuassivi.october.annotation.PerFragment;
import com.kuassivi.october.di.OctoberPresenterFragmentInjectable;
import com.kuassivi.october.di.component.BaseFragmentComponent;
import com.kuassivi.october.di.component.internal.BaseHelperFragmentComponent;
import com.kuassivi.october.di.module.BaseFragmentModule;
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

public class FragmentGenerator {

    private Types                     typeUtils;
    private Elements                  elementUtils;
    private Filer                     filer;
    private ApplicationAnnotatedClass applicationAnnotatedClass;

    private String pkg_di;
    private String pkg_di_component;
    private String pkg_di_component_helper;

    Map<String, DefaultAnnotatedClass> fragmentMap;
    Map<String, String> presenterMap;

    public FragmentGenerator(Types typeUtils,
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

        fragmentMap = new LinkedHashMap<>();
        presenterMap = new LinkedHashMap<>();
    }

    public void process(Set<? extends Element> fragments,
                        Set<? extends Element> presenterInjectableSet)
            throws ProcessingException, IllegalArgumentException, IOException {

        findFragments(fragments);

        findPresenters(presenterInjectableSet);

        generateCode();
    }

    private void findFragments(Set<? extends Element> fragments)
            throws ProcessingException {
        for (Element annotatedElement : fragments) {
            // Check if a Class has been annotated with @OctoberFragment
            if (annotatedElement.getKind() != ElementKind.CLASS) {
                throw new ProcessingException(annotatedElement,
                                              "Only classes can be annotated with @%s",
                                              FragmentComponent.class.getSimpleName());
            }
            if (!SuperficialValidation.validateElement(annotatedElement)) {
                continue;
            }
            // We can cast it, because we know that it's of ElementKind.CLASS
            TypeElement currentClass = (TypeElement) annotatedElement;

            if(Utils.containsTypeParameters(currentClass)) {
                throw new ProcessingException(annotatedElement,
                                              "Classes with Type Parameters cannot be annotated "
                                              + "with @%s",
                                              FragmentComponent.class.getSimpleName());
            }

            DefaultAnnotatedClass annotatedClass = new DefaultAnnotatedClass(currentClass);
            String qualifiedClassName = annotatedClass.getQualifiedName();
            while (true) {
                TypeMirror superClassType = currentClass.getSuperclass();
                if (superClassType.getKind() == TypeKind.NONE) {
                    throw new ProcessingException(annotatedClass.getAnnotatedClassElement(),
                                                  "%s must inherits from %s",
                                                  annotatedClass.getSimpleName(),
                                                  Config.OCTOBER_FRAGMENT_CLASS_SIMPLE_NAME);
                }
                TypeElement superClassElement =
                        (TypeElement) typeUtils.asElement(superClassType);
                String className = superClassElement.getQualifiedName().toString();
                if (className.equals(Config.OCTOBER_FRAGMENT_CLASS)) {
                    DefaultAnnotatedClass storedClass = fragmentMap.get(qualifiedClassName);
                    if (storedClass == null) {
                        fragmentMap.put(qualifiedClassName, annotatedClass);
                        Utils.note("Processing class " + annotatedClass.getSimpleName());
                    }
                    break;
                } else if(className.equals(Config.OCTOBER_ACTIVITY_CLASS)
                          || className.equals(Config.OCTOBER_APPCOMPAT_CLASS)
                          || className.equals(Config.OCTOBER_PRESENTER_CLASS)) {

                    String correctAnnotation = className.equals(Config.OCTOBER_ACTIVITY_CLASS)
                                               || className.equals(Config.OCTOBER_APPCOMPAT_CLASS)
                                               ? ActivityComponent.class.getSimpleName()
                                               : null;

                    throw new ProcessingException(annotatedClass.getAnnotatedClassElement(),
                                                  "%1$s class is annotated with @%2$s "
                                                  + "but does not inherits from %3$s. "
                                                  + (correctAnnotation != null
                                                     ? "%1$s class must be annotated with @%4$s"
                                                     : ""),
                                                  annotatedClass.getSimpleName(),
                                                  FragmentComponent.class.getSimpleName(),
                                                  Config.OCTOBER_FRAGMENT_CLASS_SIMPLE_NAME,
                                                  correctAnnotation);
                }
                // Moving up in inheritance tree
                currentClass = (TypeElement) typeUtils.asElement(superClassType);
            }
            if(fragmentMap.get(qualifiedClassName) == null) {
                Utils.warning(annotatedClass.getAnnotatedClassElement(),
                              String.format("Skipped %s.class because is annotated with @%s "
                                            + "but it doesn't inherits from %s",
                                            annotatedClass.getSimpleName(),
                                            FragmentComponent.class.getSimpleName(),
                                            Config.OCTOBER_FRAGMENT_CLASS_SIMPLE_NAME));
            }
        }
    }

    private void findPresenters(Set<? extends Element> presenterInjectableSet)
            throws ProcessingException {
        for (Element annotatedElement : presenterInjectableSet) {
            if (annotatedElement.getKind() == ElementKind.CLASS) {
                if (!SuperficialValidation.validateElement(annotatedElement)) {
                    continue;
                }
                // We can cast it, because we know that it's of ElementKind.CLASS
                TypeElement currentPresenter = (TypeElement) annotatedElement;
                TypeElement currentClass = currentPresenter;
                if(Utils.inheritsFromClass(currentClass, Config.OCTOBER_PRESENTER_CLASS)) {
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

        createPresenterFragmentInjector();

        createFragmentComponent();

        createHelperFragmentComponent();
    }

    private void createPresenterFragmentInjector()
            throws IOException {

        TypeSpec.Builder presenterFragmentInjectorClassBuilder =
                TypeSpec.classBuilder(Config.PRESENTER_FRAGMENT_INJECTOR)
                        .addModifiers(Modifier.PUBLIC)
                        .superclass(OctoberPresenterFragmentInjectable.class);

        //add Presenter members
        MethodSpec.Builder provideMethod = MethodSpec.overriding(Utils.findFirstElement(
                OctoberPresenterFragmentInjectable.class, Config.PRESENTER_INJECTOR_METHOD));

        if (!presenterMap.isEmpty()) {

            int round = 1;

            for (Map.Entry<String, String> element : presenterMap.entrySet()) {

                ParameterizedTypeName memberType =
                        ParameterizedTypeName.get(
                                ClassName.get(Lazy.class),
                                ClassName.bestGuess(element.getValue()));
                String member = "presenterLazy" + round;
                presenterFragmentInjectorClassBuilder
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

        presenterFragmentInjectorClassBuilder.addMethod(provideMethod.build());

        TypeSpec typeSpec = presenterFragmentInjectorClassBuilder.build();
        JavaFile.builder(pkg_di, typeSpec).build().writeTo(filer);
    }

    private void createFragmentComponent() throws IOException {

        AnnotationSpec daggerSubComponentAnnotation =
                AnnotationSpec
                        .builder(Subcomponent.class)
                        .addMember("modules",
                                   "{$T.class, $T.class}",
                                   ClassName.bestGuess(
                                           applicationAnnotatedClass.getFragmentModuleQualifiedClassName()
                                   ),
                                   ClassName.get(BaseFragmentModule.class))
                        .build();

        Collection<DefaultAnnotatedClass> collection = fragmentMap.values();

        ParameterizedTypeName baseInherits =
                ParameterizedTypeName.get(
                        ClassName.get(BaseFragmentComponent.class),
                        ClassName.get(pkg_di, Config.PRESENTER_FRAGMENT_INJECTOR));

        MethodSpec.Builder injectMethod =
                MethodSpec.methodBuilder(Config.COMPONENT_INJECTOR_METHOD)
                          .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                          .addParameter(ParameterSpec.builder(
                                  ClassName.get(pkg_di,
                                                Config.PRESENTER_FRAGMENT_INJECTOR),
                                  "injector").build())
                          .returns(TypeName.VOID);

        TypeSpec.Builder classBuilder =
                TypeSpec.interfaceBuilder(Config.FRAGMENT_COMPONENT)
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(PerFragment.class)
                        .addAnnotation(daggerSubComponentAnnotation)
                        .addSuperinterface(baseInherits)

                        .addMethod(injectMethod.build());

        //add rest of injected fragments views
        for (DefaultAnnotatedClass annotatedClass : collection) {
            MethodSpec.Builder method =
                    MethodSpec.methodBuilder(Config.COMPONENT_INJECTOR_METHOD)
                              .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                              .addParameter(ParameterSpec.builder(
                                      ClassName.bestGuess(annotatedClass.getQualifiedName()),
                                      "view").build())
                              .returns(TypeName.VOID);
            classBuilder.addMethod(method.build());
        }

        TypeSpec typeSpec = classBuilder.build();
        JavaFile.builder(pkg_di_component, typeSpec).build().writeTo(filer);
    }

    private void createHelperFragmentComponent() throws IOException {

        ParameterizedTypeName baseInterface =
                ParameterizedTypeName.get(
                        ClassName.get(BaseHelperFragmentComponent.class),
                        ClassName.get(BaseFragmentComponent.class));

        MethodSpec.Builder applyMethod =
                MethodSpec.methodBuilder(Config.HELPER_COMPONENT_METHOD)
                          .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                          .addParameter(ParameterSpec.builder(
                                  ClassName.get(BaseFragmentModule.class), "module").build())
                          .returns(ClassName.get(pkg_di_component,
                                                 Config.FRAGMENT_COMPONENT));

        TypeSpec.Builder classBuilder =
                TypeSpec.interfaceBuilder(Config.HELPER_FRAGMENT_COMPONENT)
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(PerFragment.class)
                        .addAnnotation(AnnotationSpec
                                               .builder(Subcomponent.class)
                                               .build())
                        .addSuperinterface(baseInterface)

                        .addMethod(applyMethod.build());

        TypeSpec typeSpec = classBuilder.build();
        JavaFile.builder(pkg_di_component_helper, typeSpec).build().writeTo(filer);
    }

    public Map<String, DefaultAnnotatedClass> getFragmentMap() {
        return fragmentMap;
    }

    public Map<String, String> getPresenterMap() {
        return presenterMap;
    }
}
