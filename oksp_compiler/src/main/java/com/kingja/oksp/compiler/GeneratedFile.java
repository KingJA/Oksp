package com.kingja.oksp.compiler;

import com.kingja.oksp.annotations.SpParam;
import com.kingja.oksp.annotations.SpService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

import static com.kingja.oksp.compiler.Util.getTypeName;

/**
 * Description:TODO
 * Create Time:2017/7/19 11:21
 * Author:KingJA
 * Email:kingjavip@gmail.com
 */
public class GeneratedFile {
    private ClassName contextClass = ClassName.get("android.content", "Context");
    private ClassName spClass = ClassName.get("android.content", "SharedPreferences");
    private ClassName ClassName_String = ClassName.get("java.lang", "String");
    protected Set<VariableElement> params = new LinkedHashSet<>();
    protected Messager mMessager;
    protected TypeElement typeElement;
    private final String SUFFIX = "Service";
    private final String packageName;
    private final String fullName;


    public GeneratedFile(Messager mMessager, Elements mElementUtils, TypeElement typeElement) {
        this.mMessager = mMessager;
        this.typeElement = typeElement;
        PackageElement packageElement = mElementUtils.getPackageOf(typeElement);
        String className = typeElement.getSimpleName().toString();
        packageName = packageElement.getQualifiedName().toString();
        fullName = className + SUFFIX;
    }

    public void addRequestParam(VariableElement element) {
        params.add(element);
    }


    public void createFile(Filer filer) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, packageName + "." + fullName);
        TypeSpec.Builder typeSpec = TypeSpec.classBuilder(fullName)
                .addModifiers(Modifier.FINAL);
        typeSpec = createSingleMethod(typeSpec);
        for (VariableElement variableElement : params) {
            typeSpec.addMethod(createPutMethod(variableElement));
            typeSpec.addMethod(createGetMethod(variableElement));
        }

        try {
            JavaFile.builder(packageName, typeSpec.build()).addFileComment("Generated code from Oksp. Do not " +
                    "modify!")
                    .build().writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private TypeSpec.Builder createSingleMethod(TypeSpec.Builder typeSpec) {
        ClassName className = ClassName.get(packageName, fullName);
        typeSpec.addField(className, "instance", Modifier.PRIVATE, Modifier.STATIC);
        typeSpec.addField(spClass, "sp", Modifier.PRIVATE);


        SpService SpServiceAnnotation = typeElement.getAnnotation(SpService.class);

        MethodSpec constructorMethod = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(contextClass, "context")
                .addStatement("sp = context.getSharedPreferences($S, Context.MODE_PRIVATE)",SpServiceAnnotation.spName())
                .build();

        MethodSpec singleMethod = MethodSpec.methodBuilder("getDetault")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(contextClass, "context")
                .beginControlFlow("if (instance==null)")
                .addStatement("instance=new $T(context)", className)
                .endControlFlow()
                .addStatement("return instance")
                .returns(className).build();
        typeSpec.addMethod(singleMethod);
        typeSpec.addMethod(constructorMethod);

        return typeSpec;
    }

    private MethodSpec createPutMethod(VariableElement variable) {
        String variableName = variable.getSimpleName().toString();
        String method = "put" + getVariableName(variableName);
        MethodSpec.Builder builder = MethodSpec.methodBuilder(method)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class);
        builder.addParameter(getTypeName(variable), variable.getSimpleName().toString());
        builder.addStatement("sp.edit().put$N($S,$N).apply()", getTypeString(variable), variableName, variableName);
        return builder.build();
    }

    private MethodSpec createGetMethod(VariableElement variable) {
        String variableName = variable.getSimpleName().toString();
        TypeName typeClassName = getTypeName(variable);
        String method = "get" + getVariableName(variableName);
        MethodSpec.Builder builder = MethodSpec.methodBuilder(method)
                .addModifiers(Modifier.PUBLIC)
                .returns(typeClassName);
        if (ClassName_String.equals(typeClassName)) {
            builder.addStatement("return sp.get$N($S,$S)", getTypeString(variable), variableName,getDefValueString( variable));
        }else{
            builder.addStatement("return sp.get$N($S,$N)", getTypeString(variable), variableName,getDefValueString( variable));
        }
        return builder.build();
    }

    private String getDefValueString(VariableElement variable) {
        SpParam spParamAnnotation = variable.getAnnotation(SpParam.class);
        String result = "";
        TypeName typeClassName = getTypeName(variable);
        if (ClassName_String.equals(typeClassName)) {
            result = ""+spParamAnnotation.defString();
        } else if (TypeName.BOOLEAN.equals(typeClassName)) {
            result = ""+spParamAnnotation.defBoolean();
        } else if (TypeName.INT.equals(typeClassName)) {
            result = ""+spParamAnnotation.defInt();
        } else if (TypeName.LONG.equals(typeClassName)) {
            result = ""+spParamAnnotation.defLong();
        } else if (TypeName.FLOAT.equals(typeClassName)) {
            result = ""+spParamAnnotation.defFloat()+"f";
        }
        return result;
    }

    private String getVariableName(String variableName) {
        String result = variableName;
        result = result.substring(0, 1).toUpperCase() + result.substring(1);
        return result;

    }

    private String getTypeString(VariableElement variable) {
        String result = "String";
        TypeName typeClassName = getTypeName(variable);
        if (ClassName_String.equals(typeClassName)) {
            result = "String";
        } else if (TypeName.BOOLEAN.equals(typeClassName)) {
            result = "Boolean";
        } else if (TypeName.INT.equals(typeClassName)) {
            result = "Int";
        } else if (TypeName.LONG.equals(typeClassName)) {
            result = "Long";
        } else if (TypeName.FLOAT.equals(typeClassName)) {
            result = "Float";
        }
        return result;
    }
}
