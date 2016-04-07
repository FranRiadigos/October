package com.kuassivi.compiler;

import com.kuassivi.october.Config;
import com.kuassivi.october.OctoberComponent;
import com.kuassivi.october.OctoberComponentInitializer;
import com.kuassivi.october.di.component.BaseApplicationComponent;
import com.kuassivi.october.di.module.BaseActivityModule;
import com.kuassivi.october.di.module.BaseApplicationModule;
import com.kuassivi.october.di.module.BaseFragmentModule;
import com.kuassivi.october.mvp.OctoberActivityInterface;
import com.kuassivi.october.mvp.OctoberFragmentInterface;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Collection;

import javax.annotation.processing.Filer;
import javax.inject.Singleton;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import dagger.Component;

public class ApplicationGenerator {

    private Types                     typeUtils;
    private Elements                  elementUtils;
    private Filer                     filer;
    private ApplicationAnnotatedClass applicationAnnotatedClass;
    private FragmentGenerator         fragmentGenerator;
    private ActivityGenerator         activityGenerator;

    private String pkg_di;
    private String pkg_di_component;
    private String pkg_di_component_helper;

    public ApplicationGenerator(Types typeUtils,
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
    }

    public void process(FragmentGenerator fragmentGenerator, ActivityGenerator activityGenerator)
            throws IOException {

        this.fragmentGenerator = fragmentGenerator;
        this.activityGenerator = activityGenerator;

        Utils.note("Processing class " + applicationAnnotatedClass.getSimpleName());

        generateCode();
    }

    private void generateCode() throws IOException {
        generateApplicationComponent();
        generateDaggerApplicationComponent();
    }

    private void generateApplicationComponent()
            throws IOException {

        AnnotationSpec daggerComponentAnnotation =
                AnnotationSpec
                        .builder(Component.class)
                        .addMember("modules",
                                   "{$T.class, $T.class}",
                                   ClassName.bestGuess(
                                           applicationAnnotatedClass
                                                   .getApplicationModuleQualifiedClassName()),
                                   ClassName.get(BaseApplicationModule.class))
                        .build();

        MethodSpec.Builder helper =
                MethodSpec.methodBuilder(Config.HELPER_ACTIVITY_COMPONENT_METHOD)
                          .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                          .returns(ClassName.get(pkg_di_component_helper,
                                                 Config.HELPER_ACTIVITY_COMPONENT));

        MethodSpec.Builder injectMethod =
                MethodSpec.methodBuilder(Config.COMPONENT_INJECTOR_METHOD)
                          .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                          .addParameter(ParameterSpec.builder(
                                  ClassName.bestGuess(applicationAnnotatedClass.getQualifiedName()),
                                  "injector").build())
                          .returns(TypeName.VOID);

        TypeSpec.Builder classBuilder =
                TypeSpec.interfaceBuilder(Config.APPLICATION_COMPONENT)
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(Singleton.class)
                        .addAnnotation(daggerComponentAnnotation)
                        .addSuperinterface(BaseApplicationComponent.class)
                        .addMethod(helper.build())
                        .addMethod(injectMethod.build());

        TypeSpec typeSpec = classBuilder.build();
        JavaFile.builder(pkg_di_component, typeSpec).build().writeTo(filer);
    }

    private void generateDaggerApplicationComponent()
            throws IOException {

        MethodSpec.Builder initialize =
                MethodSpec.methodBuilder(Config.DAGGER_APPLICATION_COMPONENT_METHOD)
                          .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                          .addAnnotation(Override.class)
                          .addParameter(ParameterSpec.builder(
                                  ClassName.bestGuess(applicationAnnotatedClass.getQualifiedName()),
                                  "application").build())
                          .returns(OctoberComponent.class);

        initialize.addStatement("final $T component = $T.builder()\n"
                                + "$>.$L(new $T(application))\n"
                                + ".$L(new $T()).build()$<",
                                ClassName.get(pkg_di_component,
                                              Config.APPLICATION_COMPONENT),
                                ClassName.get(pkg_di_component,
                                              Config.OCTOBER_DI_PREFIX
                                              + Config.APPLICATION_COMPONENT),
                                Utils.uncapitalize(BaseApplicationModule
                                                           .class.getSimpleName()),
                                ClassName.get(BaseApplicationModule.class),
                                Utils.uncapitalize(
                                        applicationAnnotatedClass
                                                .getApplicationModuleSimpleTypeName()),
                                ClassName.bestGuess(
                                        applicationAnnotatedClass
                                                .getApplicationModuleQualifiedClassName()));

        initialize.addStatement("component.$L(application)", Config.COMPONENT_INJECTOR_METHOD);

        CodeBlock.Builder fragmentInjections = CodeBlock.builder();
        Collection<DefaultAnnotatedClass> fragmentMap = fragmentGenerator.getFragmentMap().values();
        if (!fragmentMap.isEmpty()) {
            fragmentInjections.indent();
        }
        for (DefaultAnnotatedClass annotatedClass : fragmentMap) {
            fragmentInjections.beginControlFlow("if(o instanceof $T)",
                                                ClassName.bestGuess(
                                                        annotatedClass.getQualifiedName()))
                              .addStatement("get$L().$L(($T)o)",
                                            Config.FRAGMENT_COMPONENT_SIMPLE_NAME,
                                            Config.COMPONENT_INJECTOR_METHOD,
                                            ClassName.bestGuess(
                                                    annotatedClass.getQualifiedName()))
                              .endControlFlow();
        }

        CodeBlock.Builder activityInjections = CodeBlock.builder();
        Collection<DefaultAnnotatedClass> activityMap = activityGenerator.getActivityMap().values();
        if (!activityMap.isEmpty()) {
            activityInjections.indent();
        }
        for (DefaultAnnotatedClass annotatedClass : activityMap) {
            activityInjections.beginControlFlow("if(o instanceof $T)",
                                                ClassName.bestGuess(
                                                        annotatedClass.getQualifiedName()))
                              .addStatement("get$L().$L(($T)o)",
                                            Config.ACTIVITY_COMPONENT_SIMPLE_NAME,
                                            Config.COMPONENT_INJECTOR_METHOD,
                                            ClassName.bestGuess(
                                                    annotatedClass.getQualifiedName()))
                              .endControlFlow();
        }

        initialize.addCode("\nreturn new $T() {\n"
                           + "\n"
                           + "$>private $T aComponent;\n"
                           + "private $T fComponent;\n"
                           + "\n"
                           + "@Override\n"
                           + "public $T get$L() {\n"
                           + "$>return component;\n"
                           + "$<}\n"
                           + "\n"
                           + "@Override\n"
                           + "public $T get$L() {\n"
                           + "$>return component.$L();\n"
                           + "$<}\n"
                           + "\n"
                           + "@Override\n"
                           + "public $T get$L() {\n"
                           + "$>return new $T();\n"
                           + "$<}\n"
                           + "\n"
                           + "@Override\n"
                           + "public $T get$L() {\n"
                           + "$>return new $T();\n"
                           + "$<}\n"
                           + "\n"
                           + "@Override\n"
                           + "public void $L($T m) {\n"
                           + "$>aComponent = get$L().$L(m);\n"
                           + "$<}\n"
                           + "\n"
                           + "@Override\n"
                           + "public void $L($T m) {\n"
                           + "$>fComponent = get$L().$L().$L(m);\n"
                           + "$<}\n"
                           + "\n"
                           + "@Override\n"
                           + "public $T get$L() {\n"
                           + "$>return aComponent;\n"
                           + "$<}\n"
                           + "\n"
                           + "@Override\n"
                           + "public $T get$L() {\n"
                           + "$>return fComponent;\n"
                           + "$<}\n"
                           + "\n"
                           + "@Override\n"
                           + "public void $L($T o) {\n",
                           ClassName.get(OctoberComponent.class),
                           ClassName.get(pkg_di_component, Config.ACTIVITY_COMPONENT),
                           ClassName.get(pkg_di_component, Config.FRAGMENT_COMPONENT),
                           ClassName.get(pkg_di_component, Config.APPLICATION_COMPONENT),
                           Config.APPLICATION_COMPONENT,
                           ClassName.get(pkg_di_component_helper,
                                         Config.HELPER_ACTIVITY_COMPONENT),
                           Config.HELPER_ACTIVITY_COMPONENT,
                           Config.HELPER_ACTIVITY_COMPONENT_METHOD,
                           ClassName.get(pkg_di, Config.PRESENTER_ACTIVITY_INJECTOR),
                           Config.PRESENTER_ACTIVITY_INJECTOR,
                           ClassName.get(pkg_di, Config.PRESENTER_ACTIVITY_INJECTOR),
                           ClassName.get(pkg_di, Config.PRESENTER_FRAGMENT_INJECTOR),
                           Config.PRESENTER_FRAGMENT_INJECTOR,
                           ClassName.get(pkg_di, Config.PRESENTER_FRAGMENT_INJECTOR),
                           Config.HELPER_COMPONENT_METHOD,
                           ClassName.get(BaseActivityModule.class),
                           Config.HELPER_ACTIVITY_COMPONENT,
                           Config.HELPER_COMPONENT_METHOD,
                           Config.HELPER_COMPONENT_METHOD,
                           ClassName.get(BaseFragmentModule.class),
                           Config.ACTIVITY_COMPONENT_SIMPLE_NAME,
                           Config.HELPER_FRAGMENT_COMPONENT_METHOD,
                           Config.HELPER_COMPONENT_METHOD,
                           ClassName.get(pkg_di_component, Config.ACTIVITY_COMPONENT),
                           Config.ACTIVITY_COMPONENT_SIMPLE_NAME,
                           ClassName.get(pkg_di_component, Config.FRAGMENT_COMPONENT),
                           Config.FRAGMENT_COMPONENT_SIMPLE_NAME,
                           Config.COMPONENT_INJECTOR_METHOD,
                           ClassName.get(OctoberActivityInterface.class)
        );

        initialize.addCode(activityInjections.build())
                  .endControlFlow()
                  .addCode("\n@Override\n")
                  .beginControlFlow("public void $L($T o)",
                                    Config.COMPONENT_INJECTOR_METHOD,
                                    ClassName.get(OctoberFragmentInterface.class))
                  .addCode(fragmentInjections.build())
                  .endControlFlow()
                  .addCode("$<};\n");

        TypeSpec.Builder classBuilder =
                TypeSpec.classBuilder(Config.OCTOBER_CLASS_NAME)
                        .addSuperinterface(
                                ClassName.get(OctoberComponentInitializer.class))
                        .addModifiers(Modifier.PUBLIC)
                        .addMethod(initialize.build());

        TypeSpec typeSpec = classBuilder.build();
        JavaFile.builder(pkg_di_component, typeSpec).build().writeTo(filer);
    }
}
