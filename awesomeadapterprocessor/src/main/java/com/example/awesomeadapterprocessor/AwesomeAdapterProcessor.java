package com.example.awesomeadapterprocessor;

import com.example.awesomeadapterannotation.AwesomeDelegates;
import com.google.auto.service.AutoService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Types;

import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.Diagnostic.Kind.NOTE;

/**
 * Created by nazmuddinmavliwala on 02/08/16.
 */

@AutoService(Processor.class)
public class AwesomeAdapterProcessor extends AbstractProcessor {

    private static final String ANNOTATION = "@" + AwesomeDelegates.class.getSimpleName();
    private static TypeElement ADAPTER_TYPE;
    private Messager messager;
    private DelegateCodeGenerator delegateCodeGenerator;
    private Types typeUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        typeUtils = processingEnv.getTypeUtils();
        delegateCodeGenerator = DelegateCodeGenerator.newInstance(processingEnv);
        ADAPTER_TYPE = processingEnv.getElementUtils()
                .getTypeElement("com.example.nazmuddinmavliwala.awesomeadapter.adapters.BaseAwesomeAdapter");
        messager.printMessage(NOTE,"inside init");

    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(AwesomeDelegates.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        ArrayList<AnnotatedClass> annotatedClasses = new ArrayList<>();
        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(AwesomeDelegates.class)) {
            TypeElement annotatedClass = (TypeElement) annotatedElement;
            if (!isValidClass(annotatedClass)) {
                return true;
            }
            try {
                annotatedClasses.add(buildAnnotatedClass(annotatedClass));
            } catch (NoPackageNameException | IOException e) {
                String message = String.format("Couldn't process class %s: %s", annotatedClass,
                        e.getMessage());
                messager.printMessage(ERROR, message, annotatedElement);
            }
        }
        try {
            generate(annotatedClasses);
        } catch (NoPackageNameException | IOException | ProcessingException e) {
            messager.printMessage(ERROR, "Couldn't generate class");
        }
        return true;
    }

    private boolean isValidClass(TypeElement annotatedClass) {

        if (!ClassValidator.isPublic(annotatedClass)) {
            String message = String.format("Classes annotated with %s must be public.",
                    ANNOTATION);
            messager.printMessage(ERROR, message, annotatedClass);
            return false;
        }

        if (ClassValidator.isAbstract(annotatedClass)) {
            String message = String.format("Classes annotated with %s must not be abstract.",
                    ANNOTATION);
            messager.printMessage(ERROR, message, annotatedClass);
            return false;
        }

        if (ClassValidator.isInterface(annotatedClass)) {
            String message = String.format("Classes annotated with %s must not be an interface.",
                    ANNOTATION);
            messager.printMessage(ERROR, message, annotatedClass);
            return false;
        }

        if (!ClassValidator.isAdapterClass(this.typeUtils,annotatedClass,ADAPTER_TYPE)) {
            String message = String.format("Classes annotated with %s must extend BaseEmployeeRecyclerViewAdapter.",
                    ANNOTATION);
            messager.printMessage(ERROR, message, annotatedClass);
            return false;
        }

        return true;
    }

    private AnnotatedClass buildAnnotatedClass(TypeElement annotatedClass)
            throws NoPackageNameException, IOException {
        ArrayList<String> variableNames = new ArrayList<>();
        for (Element element : annotatedClass.getEnclosedElements()) {
            if (!(element instanceof VariableElement)) {
                continue;
            }
            VariableElement variableElement = (VariableElement) element;
            variableNames.add(variableElement.getSimpleName().toString());
        }

        return new AnnotatedClass(annotatedClass , variableNames);
    }

    private void generate(List<AnnotatedClass> annos) throws NoPackageNameException, IOException, ProcessingException {
        if (annos.size() == 0) {
            return;
        }
        //generate delegates
        try {
            delegateCodeGenerator.generateClasses(processingEnv,annos);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}
