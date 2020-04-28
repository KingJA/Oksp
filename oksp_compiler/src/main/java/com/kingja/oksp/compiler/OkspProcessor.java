package com.kingja.oksp.compiler;


import com.kingja.oksp.annotations.SpParam;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;


import static com.kingja.oksp.compiler.Util.getTypeName;
import static javax.lang.model.element.Modifier.PRIVATE;

public class OkspProcessor extends AbstractProcessor {

    private Messager mMessager;
    private Map<TypeElement, GeneratedFile> generatedBodys = new HashMap<>();
    private Filer mFiler;
    private Elements mElementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mMessager = processingEnvironment.getMessager();
        mFiler = processingEnvironment.getFiler();
        mElementUtils = processingEnvironment.getElementUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotationTypes = new LinkedHashSet<>();
        annotationTypes.add(SpParam.class.getCanonicalName());
        return annotationTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
//        generatedBodys.clear();
        mMessager.printMessage(Diagnostic.Kind.NOTE, "process>>>>>>>>");
        for (Element element : roundEnvironment.getElementsAnnotatedWith(SpParam.class)) {
            if (!validateField(element, SpParam.class)) {
                return true;
            }
            VariableElement variableElement = (VariableElement) element;
            String paramName = variableElement.getSimpleName().toString();
            TypeName typeName = ClassName.get(element.asType());

            TypeElement typeElement = (TypeElement) variableElement.getEnclosingElement();
            mMessager.printMessage(Diagnostic.Kind.NOTE, "typeElement "+typeElement.getClass().toString());

            String className = typeElement.getQualifiedName().toString();

            mMessager.printMessage(Diagnostic.Kind.NOTE, String.format("paramName %s className %s",paramName,className));

            GeneratedFile generatedFile = generatedBodys.get(typeElement);
            if (generatedFile == null) {
                generatedFile = getTargetBody(typeElement);
            }
            generatedFile.addRequestParam(variableElement);
            generatedBodys.put(typeElement, generatedFile);

        }
        for (TypeElement key : generatedBodys.keySet()) {
            GeneratedFile generatedFile = generatedBodys.get(key);
            generatedFile.createFile(mFiler);
        }
        return true;
    }

    private boolean validateField(Element element, Class<? extends Annotation> clazz) {
        if (element.getKind() != ElementKind.FIELD) {
            printError("%s must be eclared on field ", clazz.getClass().getSimpleName());
            return false;
        }

        if (element.getModifiers().contains(PRIVATE)) {
            printError("the modifier of %s() must not be private", element.getSimpleName());
            return false;
        }
        return true;
    }

    private void printError(String message, Object... args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }
        mMessager.printMessage(Diagnostic.Kind.NOTE, message);
    }


    private GeneratedFile getTargetBody(TypeElement typeElement) {
        return new GeneratedFile(mMessager, mElementUtils, typeElement);
    }
}
