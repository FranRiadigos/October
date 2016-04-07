package com.kuassivi.compiler;

import com.google.auto.common.SuperficialValidation;
import com.google.auto.service.AutoService;

import com.kuassivi.october.Config;
import com.kuassivi.october.annotation.ActivityComponent;
import com.kuassivi.october.annotation.ApplicationComponent;
import com.kuassivi.october.annotation.FragmentComponent;
import com.kuassivi.october.annotation.PerActivity;
import com.kuassivi.october.annotation.PerFragment;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static javax.lang.model.SourceVersion.latestSupported;

@AutoService(Processor.class)
public class OctoberProcessor extends AbstractProcessor {

    private Types    typeUtils;
    private Elements elementUtils;
    private Filer    filer;

    private Set<String> firstRound;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        Utils.initialize(processingEnv);
        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        firstRound = new LinkedHashSet<>(1);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new LinkedHashSet<>(
                Arrays.asList(
                        ApplicationComponent.class.getCanonicalName(),
                        ActivityComponent.class.getCanonicalName(),
                        FragmentComponent.class.getCanonicalName(),
                        PerActivity.class.getCanonicalName(),
                        PerFragment.class.getCanonicalName()));
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {

            if(!annotations.isEmpty() && firstRound.isEmpty()) {

                ApplicationAnnotatedClass applicationAnnotatedClass =
                        retrieveApplicationAnnotatedClass(roundEnv);

                // check for OctoberFragment
                FragmentGenerator fragmentGenerator =
                        new FragmentGenerator(typeUtils, elementUtils, filer, applicationAnnotatedClass);
                fragmentGenerator.process(roundEnv.getElementsAnnotatedWith(FragmentComponent.class),
                                          roundEnv.getElementsAnnotatedWith(PerFragment.class));

                // check for OctoberActivity
                ActivityGenerator activityGenerator =
                        new ActivityGenerator(typeUtils, elementUtils, filer, applicationAnnotatedClass);
                activityGenerator.process(roundEnv.getElementsAnnotatedWith(ActivityComponent.class),
                                          roundEnv.getElementsAnnotatedWith(PerActivity.class));

                // check for OctoberApplication
                ApplicationGenerator applicationGenerator =
                        new ApplicationGenerator(typeUtils, elementUtils, filer, applicationAnnotatedClass);
                applicationGenerator.process(fragmentGenerator, activityGenerator);

                firstRound.add(applicationAnnotatedClass.getQualifiedName());
            }

        } catch (ProcessingException e) {
            Utils.error(e.getElement(), e.getMessage());
        } catch (ValidationException e) {
            Utils.errors(e.getElement(), e.getMessages());
        } catch (Exception e) {
            Utils.error(null, e.getMessage());
        }

        return true;
    }

    private ApplicationAnnotatedClass retrieveApplicationAnnotatedClass(RoundEnvironment roundEnv)
            throws ProcessingException, IllegalArgumentException, ValidationException {

        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(
                ApplicationComponent.class);

        if (elements.isEmpty() || elements.size() > 1) {
            if (elements.isEmpty()) {
                throw new ProcessingException(null,
                                              "You must annotate your Application class with @%s",
                                              ApplicationComponent.class.getSimpleName());
            } else {
                throw new ProcessingException((Element)elements.toArray()[1],
                                              "Only one Application class can be annotated with @%s. "
                                              + "Found following classes: %s",
                                              ApplicationComponent.class.getSimpleName(),
                                              elements.toString());
            }
        }

        Element applicationElement = (Element) elements.toArray()[0];

        // Check if a Class has been annotated with @OctoberApplication
        if (applicationElement.getKind() != ElementKind.CLASS) {
            throw new ProcessingException(applicationElement,
                                          "Only classes can be annotated with @%s",
                                          ApplicationComponent.class.getSimpleName());
        }

        if (!SuperficialValidation.validateElement(applicationElement)) {
            throw new ProcessingException(applicationElement,
                                          "The element class is not valid or inherits "
                                          + "form a non valid class, interface or element kind. "
                                          + "Might it be inheriting from a generated class?");
        }

        // We can cast it, because we know that it's of ElementKind.CLASS
        TypeElement typeElement = (TypeElement) applicationElement;

        // Validates if inherits from Application
        Set<String> errors = AnnotationValidator
                .with(typeUtils, typeElement, ApplicationComponent.class)
                .inheritsFromClass(Config.ANDROID_APPLICATION_QUALIFIED_NAME)
                .getErrors();

        // Checks for errors
        if (!errors.isEmpty()) {
            throw new ValidationException(typeElement, errors);
        }

        return new ApplicationAnnotatedClass(typeElement);
    }
}
