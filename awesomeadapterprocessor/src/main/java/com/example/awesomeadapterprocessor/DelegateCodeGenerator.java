package com.example.awesomeadapterprocessor;

import com.example.awesomeadapterannotation.AwesomeAdapter;
import com.example.awesomeadapterannotation.AwesomeDelegates;
import com.example.awesomeadapterannotation.Delegate;
import com.example.awesomeadapterannotation.DelegateBinder;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static com.squareup.javapoet.JavaFile.builder;
import static com.squareup.javapoet.TypeSpec.classBuilder;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PROTECTED;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.tools.Diagnostic.Kind.ERROR;

/**
 * Created by nazmuddinmavliwala on 02/08/16.
 *
 * class responsible for generating adapter delegate related classes
 */
public class DelegateCodeGenerator {

    private static DelegateCodeGenerator instance;
    private final Messager messager;
    private final Types typeUtils;
    private final Elements elementUtils;
    private static TypeElement VIEWHOLDER_TYPE;

    public static synchronized DelegateCodeGenerator newInstance(
            ProcessingEnvironment processingEnvironment) {
        if (instance == null) {
            instance = new DelegateCodeGenerator(
                    processingEnvironment);
        }
        return instance;
    }

    private DelegateCodeGenerator(ProcessingEnvironment processingEnvironment) {
        this.messager = processingEnvironment.getMessager();
        this.typeUtils = processingEnvironment.getTypeUtils();
        this.elementUtils = processingEnvironment.getElementUtils();
        VIEWHOLDER_TYPE = elementUtils.getTypeElement("android.support.v7.widget.RecyclerView.ViewHolder");
    }

    /**
     * iterates through all the annotated adapter classes and generates the boilerplate code
     * for the annotated classes.
     *
     * @param processingEnv
     * @param classes
     * @throws NoPackageNameException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("unchecked")
    public void generateClasses(
            ProcessingEnvironment processingEnv
            , List<AnnotatedClass> classes)
            throws NoPackageNameException
            , IOException
            , ClassNotFoundException, ProcessingException {

        //iterate through all the annotated classes to get the delegate specific data
        //consider the following example :
        for (AnnotatedClass annotatedClass : classes) {
            TypeElement typeElement = annotatedClass.typeElement;
            AwesomeDelegates awesomeDelegate = typeElement
                    .getAnnotation(AwesomeDelegates.class);
            Delegate[] delegates = awesomeDelegate.value();

            //if the @AwesomeDelegate doesn't contain any delegates then return
            if (delegates.length == 0) {
                return;
            } else {
                //retrieve data mentioned in the @Delegate annotation and
                // store it in DelegateModel object for further processing
                List<DelegateModel> delegateModels = constructDelegateModels(
                        delegates
                        ,typeElement);
                //generate the concerned adapter delegates
                generateAdapterDelegates(processingEnv
                        , delegateModels);

                //generate adapter builder of the
                // annotated adapter class
                generateAdapterBuilder(processingEnv
                        , annotatedClass
                        , delegateModels);
            }
        }
        //generate/update delegate binder class
        generateAutoBinderClass(processingEnv, classes);
    }

    private List<DelegateModel> constructDelegateModels(
            Delegate[] delegates
            , TypeElement typeElement) throws ProcessingException {
        List<DelegateModel> delegateModels = new ArrayList<>();
        for (Delegate delegate : delegates) {
            DelegateModel delegateModel = new DelegateModel();
            delegateModel.setTypeElement(typeElement);
            //get view holder
            processViewHolder(
                    delegate
                    ,delegateModel);
            //get view holder interactor
            processViewHolderInteractor(
                    delegate
                    ,delegateModel);

            //get data object
            processDataObject(
                    delegate
                    ,delegateModel);

            //get layout
            delegateModel.setLayout(
                    delegate.layout());

            //get context
            delegateModel.useContext(
                    delegate.useContext());
            delegateModels.add(
                    delegateModel);
        }
        return delegateModels;
    }

    private void processDataObject(
            Delegate delegate
            , DelegateModel delegateModel)
            throws ProcessingException {
        try {
            delegate.dataObject();
        } catch (MirroredTypeException e) {
            TypeMirror typeMirror = e.getTypeMirror();
            if(isValidDataObject(typeMirror)) {
                delegateModel.setDataObject(getDataObject(typeMirror));
                delegateModel.setDataObjectPackageName
                        (getDataObjectPackageName(typeMirror));
            } else {
                throw new ProcessingException();
            }
        }
    }

    private boolean isValidDataObject(TypeMirror typeMirror) {
        Element element = typeUtils.asElement(typeMirror);
        if (ClassValidator.isInterface(element)) {
            messager.printMessage(ERROR,"dataObject cannot be an interface. "
                    +element.getSimpleName().toString()
                    +" is an interface");
            return false;
        }
        if (ClassValidator.isAbstract(element)) {
            messager.printMessage(ERROR,"dataObject cannot be an abstract class. "
                    +element.getSimpleName().toString()
                    +" is an abstract class");
            return false;
        }
        if(!ClassValidator.isPublic(element)) {
            messager.printMessage(ERROR,"dataObject should have PUBLIC visibility access. "
                    +element.getSimpleName().toString()
                    +" is not PUBLIC");
        }
        return true;
    }

    private void processViewHolderInteractor(
            Delegate delegate
            , DelegateModel delegateModel)
            throws ProcessingException {
        try {
            delegate.viewHolderInteractor();
        } catch (MirroredTypeException e) {
            TypeMirror typeMirror = e.getTypeMirror();
            if(isValidViewHolderInteractor(typeMirror)) {
                delegateModel.setViewHolderInteractor
                        (getViewHolderInteractor(typeMirror));
                delegateModel.setViewHolderInteractorPackageName
                        (getViewHolderInteractorPackageName(typeMirror));
            } else {
                throw new ProcessingException();
            }
        }
    }

    private boolean isValidViewHolderInteractor(TypeMirror typeMirror) {
        Element element = typeUtils.asElement(typeMirror);
        if(!ClassValidator.isInterface(element)) {
            messager.printMessage(ERROR, "viewHolderInteractor can only be an interface. "
                    +element.getSimpleName().toString()
                    +" is not an interface");
            return false;
        }
        if(!ClassValidator.isPublic(element)) {
            messager.printMessage(ERROR,"viewHolderInteractor should have PUBLIC visibility access. "
                    +element.getSimpleName().toString()
                    +" is not PUBLIC");
        }
        return true;
    }

    private void processViewHolder(
            Delegate delegate
            , DelegateModel delegateModel)
            throws ProcessingException {
        try {
            delegate.viewHolder();
        } catch (MirroredTypeException e) {
            TypeMirror typeMirror = e.getTypeMirror();
            if(isValidViewHolder(typeMirror)) {
                delegateModel.setClassName(getDelegateClassName(typeMirror));
                delegateModel.setViewHolder(getViewHolder(typeMirror));
                delegateModel.setViewHolderPackageName
                        (getViewHolderPackageName(typeMirror));
            } else {
                throw new ProcessingException();
            }
        }
    }

    private boolean isValidViewHolder(TypeMirror typeMirror) {
        Element element = typeUtils.asElement(typeMirror);
        if (ClassValidator.isInterface(element)) {
            messager.printMessage(ERROR,"viewHolder cannot be an interface. "
                    +element.getSimpleName().toString()
                    +" is an interface");
            return false;
        }
        if(ClassValidator.isAbstract(element)) {
            messager.printMessage(ERROR,"viewHolder cannot be an abstract class. "
                    +element.getSimpleName().toString()
                    +" is an abstract class");
        }
        if(!ClassValidator.isPublic(element)) {
            messager.printMessage(ERROR,"viewHolder should have PUBLIC visibility access. "
                    +element.getSimpleName()
                    +" is not PUBLIC");
        }
        if(!ClassValidator.isViewHolderClass(typeUtils,typeMirror, VIEWHOLDER_TYPE)) {
            messager.printMessage(ERROR,element.getSimpleName().toString()
                    +" is not a valid viewHolder class. It must be "
                    + "a subClass of RecyclerView.ViewHolder");
        }

        return true;
    }

    /**
     *generates an autobinder class, which processes the annotated adapter
     * and calls the appropriate adaterbuilder class to inject the adapter delegates
     * in the annotated adapter.
     *
     * @param processingEnv
     * @param classes
     * @throws IOException
     * @throws NoPackageNameException
     *
     */
    private void generateAutoBinderClass(
            ProcessingEnvironment processingEnv
            , List<AnnotatedClass> classes)
            throws IOException
            , NoPackageNameException {

        String paramName = "target";
        ParameterSpec paramSpec = ParameterSpec
                .builder(TypeName.get(Object.class), paramName)
                .build();

        String statement1 = "Class<?> targetClass = target.getClass()";
        String statement2 = "String targetName = targetClass.getCanonicalName()";

        int i = 0;
        CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();
        while (i < classes.size()) {
            AnnotatedClass annotatedClass = classes.get(i);
            String packageName = Utils
                    .getPackageName(processingEnv.getElementUtils()
                    , annotatedClass.typeElement);
            String condition = "targetName.equals($T.class.getName())";
            String result = "new $T().bind(($T)target)";
            ClassName annotatedClassName
                    = ClassName.get(packageName
                    , annotatedClass.annotatedClassName);
            ClassName builderClassName
                    = ClassName.get(packageName
                    , annotatedClass.annotatedClassName + "Builder");

            if (i == 0) {
                codeBlockBuilder.beginControlFlow(String.format("if (%s)"
                        , condition)
                        , annotatedClassName)
                        .addStatement(result
                        , builderClassName
                        , annotatedClassName)
                        .endControlFlow();
            } else {
                codeBlockBuilder.beginControlFlow(String.format("else if (%s)"
                        , condition)
                        , annotatedClassName);
                codeBlockBuilder.addStatement(result
                        , builderClassName
                        , annotatedClassName)
                        .endControlFlow();
            }
            i++;
        }
        String methodName = "bind";
        MethodSpec.Builder bindBuilder = MethodSpec
                .methodBuilder(methodName)
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .addParameter(paramSpec)
                .addStatement(statement1)
                .addStatement(statement2)
                .addCode(codeBlockBuilder.build());

        TypeSpec.Builder builder
                = classBuilder(
                AwesomeAdapter.AUTO_MAPPING_CLASS_NAME)
                .addModifiers(PUBLIC, FINAL)
                .addSuperinterface(TypeName.get(DelegateBinder.class))
                .addMethod(bindBuilder.build());

        JavaFile javaFile = builder(AwesomeAdapter.AUTO_MAPPING_PACKAGE
                , builder.build()).build();
        javaFile.writeTo(processingEnv.getFiler());
    }

    /**
     *
     * @param processingEnv
     * @param annotatedClass stores class info of the classes annotated with
     *                       @AwesomeDelegate annotation
     * @param delegateModels store data extracted from
     *                      @Delegate annotation
     * @throws NoPackageNameException
     * @throws IOException
     */
    private void generateAdapterBuilder(
            ProcessingEnvironment processingEnv
            , AnnotatedClass annotatedClass
            , List<DelegateModel> delegateModels)
            throws NoPackageNameException
            , IOException {

        String packageName
                = Utils.getPackageName(
                processingEnv.getElementUtils()
                , annotatedClass.typeElement);

        ParameterSpec paramSpec = ParameterSpec
                .builder(ClassName.get(
                        packageName
                        , annotatedClass.annotatedClassName)
                        , "target")
                .build();

        MethodSpec.Builder bindBuilder = MethodSpec.methodBuilder("bind")
                .addModifiers(PUBLIC)
                .addParameter(paramSpec);
        String statement;
        int i = 0;
        for (DelegateModel delegateModel : delegateModels) {
            if (delegateModel.getViewHolderInteractor().equals("NONE")) {
                statement = String.format("target.addDelegate(new %s(%s,%s))"
                        , delegateModel.getClassName()
                        , String.valueOf(i)
                        , "target.context");
            } else {
                statement = String.format("target.addDelegate(new %s(%s,%s,target))"
                        , delegateModel.getClassName()
                        , String.valueOf(i)
                        , "target.context");
            }
            i++;
            bindBuilder.addStatement(statement);
        }

        TypeSpec.Builder classBuilder
                = classBuilder(
                annotatedClass.annotatedClassName + "Builder")
                .addModifiers(PUBLIC, FINAL)
                .addMethod(bindBuilder.build());

        JavaFile javaFile = builder(packageName
                , classBuilder.build()).build();
        javaFile.writeTo(processingEnv.getFiler());
    }

    /**
     * Generates the adapter delegates which will then be injected into the
     * annotated adapter.
     *
     * @param processingEnv
     * @param delegateModels store data extracted from @Delegate anotation
     * @throws NoPackageNameException
     * @throws IOException
     */
    private void generateAdapterDelegates(
            ProcessingEnvironment processingEnv
            , List<DelegateModel> delegateModels)
            throws NoPackageNameException
            , IOException {

        for (DelegateModel delegateModel : delegateModels) {

            String packageName = Utils
                    .getPackageName(processingEnv.getElementUtils()
                    , delegateModel.getTypeElement());
            TypeSpec.Builder adapterDelegateBuilder
                    = buildAdapterDelegateClass(delegateModel);

            //constructor
            generateAdapterDelegateConstructor(
                    adapterDelegateBuilder
                    ,delegateModel);
            //view holder
            generateAdapterDelegateGetViewHolderMethod(
                    adapterDelegateBuilder
                    ,delegateModel);
            //view type
            generateAdapterDelegateGetViewTypeMethod(
                    adapterDelegateBuilder
                    ,delegateModel);
            //is for view type?
            generateAdapterDelegateIsForViewTypeMethod(
                    adapterDelegateBuilder
                    ,delegateModel);
            //bind view holder
            generateAdapterDelegateBindViewHolderMethod(
                    adapterDelegateBuilder
                    ,delegateModel);

            JavaFile javaFile = builder(
                    packageName
                    , adapterDelegateBuilder.build())
                    .build();
            javaFile.writeTo(processingEnv.getFiler());
        }
    }

    private void generateAdapterDelegateBindViewHolderMethod(
            TypeSpec.Builder adapterDelegateBuilder
            , DelegateModel delegateModel) {

        String bindViewHolderVar = "(($T)holder).bindViews(($T)items.get(position))";
        ParameterSpec viewHolderParam = ParameterSpec
                .builder(ClassName.get("android.support.v7.widget.RecyclerView", "ViewHolder"), "holder")
                .build();
        String itemsParamName = "items";

        TypeName itemParamTypeName = ParameterizedTypeName.get(ClassName.get("java.util", "List")
                , ClassName.get(Object.class));
        ParameterSpec itemsParam = ParameterSpec.builder(
                itemParamTypeName
                , itemsParamName)
                .build();
        ParameterSpec positionParam = ParameterSpec
                .builder(TypeName.INT, "position")
                .build();

        String viewHolderPackageName = delegateModel
                .getViewHolderPackageName();
        String viewHolderClassName = delegateModel
                .getViewHolder();
        ClassName viewHolderTypeName = ClassName.get(
                viewHolderPackageName
                , viewHolderClassName);

        String dataObjectPackageName = delegateModel
                .getDataObjectPackageName();
        String dataObjectClassName = delegateModel
                .getDataObject();
        ClassName dataObjectTypeName = ClassName.get(
                dataObjectPackageName
                , dataObjectClassName);

        MethodSpec bindMethod = MethodSpec
                .methodBuilder("onBindViewHolder")
                .addModifiers(PUBLIC)
                .addParameter(itemsParam)
                .addParameter(positionParam)
                .addParameter(viewHolderParam)
                .addStatement(bindViewHolderVar, viewHolderTypeName, dataObjectTypeName)
                .build();
        adapterDelegateBuilder.addMethod(bindMethod);
    }

    private void generateAdapterDelegateIsForViewTypeMethod(
            TypeSpec.Builder adapterDelegateBuilder
            , DelegateModel delegateModel) {
        String viewTypeVar = "return items.get(position) instanceof $T";
        String dataObjectPackageName = delegateModel
                .getDataObjectPackageName();
        String dataObjectClassName = delegateModel
                .getDataObject();
        ClassName viewTypeVal = ClassName.get(
                dataObjectPackageName
                , dataObjectClassName);
        String itemsParamName = "items";
        TypeName itemParamTypeName = ParameterizedTypeName.get(ClassName.get("java.util", "List")
                , ClassName.get(Object.class));
        ParameterSpec itemsParam = ParameterSpec.builder(itemParamTypeName
                , itemsParamName).build();
        ParameterSpec positionParam = ParameterSpec
                .builder(TypeName.INT, "position")
                .build();
        MethodSpec isForViewTypeMethod = MethodSpec
                .methodBuilder("isForViewType")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(TypeName.BOOLEAN)
                .addParameter(itemsParam)
                .addParameter(positionParam)
                .addStatement(viewTypeVar, viewTypeVal)
                .build();
        adapterDelegateBuilder.addMethod(isForViewTypeMethod);
    }

    private void generateAdapterDelegateGetViewTypeMethod(
            TypeSpec.Builder adapterDelegateBuilder
            , DelegateModel delegateModel) {
        MethodSpec viewHolderLayoutMethod = MethodSpec
                .methodBuilder("getViewHolderLayout")
                .addAnnotation(Override.class)
                .addModifiers(PROTECTED)
                .returns(TypeName.INT)
                .addStatement("return " + delegateModel.getLayout())
                .build();
        adapterDelegateBuilder.addMethod(viewHolderLayoutMethod);
    }

    private void generateAdapterDelegateGetViewHolderMethod(
            TypeSpec.Builder adapterDelegateBuilder
            , DelegateModel delegateModel) {
        StringBuilder viewTypeStatement = new StringBuilder();
        viewTypeStatement.append("return new $T(itemView");
        if (delegateModel.isUseContext()) {
            viewTypeStatement.append(",context");
        }
        if (!delegateModel.getViewHolderInteractor().equals("NONE")) {
            viewTypeStatement.append(",interactor");
        }
        viewTypeStatement.append(")");
        String viewHolderVar = viewTypeStatement.toString();
        String VIEW_HOLDER_CLASS_NAME = delegateModel.getViewHolderPackageName();
        String VIEW_HOLDER_CLASS = delegateModel.getViewHolder();
        ClassName viewHolderVal = ClassName.get(VIEW_HOLDER_CLASS_NAME, VIEW_HOLDER_CLASS);
        String VIEW_TYPE_METHOD_NAME = "getViewHolder";
        TypeName viewTypeReturnTypeName = ClassName
                .get("android.support.v7.widget.RecyclerView"
                        , "ViewHolder");
        ParameterSpec viewTypeMethodParam = ParameterSpec
                .builder(ClassName.get("android.view", "View"), "itemView")
                .build();
        MethodSpec viewTypeMethod = MethodSpec.methodBuilder(VIEW_TYPE_METHOD_NAME)
                .addAnnotation(Override.class)
                .addModifiers(PROTECTED)
                .returns(viewTypeReturnTypeName)
                .addParameter(viewTypeMethodParam)
                .addStatement(viewHolderVar, viewHolderVal).build();
        adapterDelegateBuilder.addMethod(viewTypeMethod);
    }

    private void generateAdapterDelegateConstructor(
            TypeSpec.Builder adapterDelegateBuilder
            , DelegateModel delegateModel) {
        MethodSpec.Builder constructorSpec = MethodSpec.constructorBuilder();
        constructorSpec.addModifiers(PUBLIC);
        ParameterSpec constructorViewTypeParamSpec = ParameterSpec
                .builder(TypeName.INT, "viewType")
                .build();
        ParameterSpec contextSpec = ParameterSpec
                .builder(ClassName.get("android.content", "Context"), "context")
                .build();
        constructorSpec.addParameter(constructorViewTypeParamSpec);
        constructorSpec.addParameter(contextSpec);
        constructorSpec.addStatement("super(viewType,context)");
        if (!delegateModel.getViewHolderInteractor().equals("NONE")) {

            String FIELD_NAME = "interactor";
            String vhInteractorPackageName = delegateModel
                    .getViewHolderInteractorPackageName();
            String vhInteractorClassName = delegateModel
                    .getViewHolderInteractor();
            TypeName vhInteractorTypeName = ClassName
                    .get(vhInteractorPackageName, vhInteractorClassName);
            FieldSpec delegateFieldSpec = FieldSpec
                    .builder(vhInteractorTypeName
                            , FIELD_NAME
                            , PRIVATE
                            , FINAL)
                    .build();
            adapterDelegateBuilder.addField(delegateFieldSpec);
            String INTERACTOR_PARAM_NAME = "interactor";
            ParameterSpec interactorSpec = ParameterSpec
                    .builder(vhInteractorTypeName
                            , INTERACTOR_PARAM_NAME)
                    .build();
            constructorSpec.addParameter(interactorSpec);
            constructorSpec.addStatement("this.interactor = interactor");
        }
        adapterDelegateBuilder.addMethod(constructorSpec.build());
    }

    private TypeSpec.Builder buildAdapterDelegateClass(DelegateModel delegateModel) {
        TypeSpec.Builder adapterDelegateBuilder = classBuilder(delegateModel.getClassName());
        adapterDelegateBuilder.addModifiers(PUBLIC, FINAL);


        String SUPER_CLASS_PACKAGE_NAME = "com.example.nazmuddinmavliwala.awesomeadapter.adapters";
        String SUPER_CLASS_NAME = "AbstractAdapterDelegate";
        ClassName superClassName = ClassName.get(SUPER_CLASS_PACKAGE_NAME, SUPER_CLASS_NAME);


        String PARAM_PACKAGE_NAME = "java.util";
        String PARAM_CLASS_NAME = "List";
        TypeName paramTypeArg = ClassName.get(Object.class);
        ClassName paramType  = ClassName.get(PARAM_PACKAGE_NAME, PARAM_CLASS_NAME);
        TypeName paramTypeName = ParameterizedTypeName.get(paramType, paramTypeArg);

        ParameterizedTypeName superClassSpec = ParameterizedTypeName
                .get(superClassName,paramTypeName);
        adapterDelegateBuilder.superclass(superClassSpec);
        return adapterDelegateBuilder;
    }

    private String getDataObjectPackageName(TypeMirror typeMirror) {
        return typeMirror.toString().substring(0, typeMirror.toString().lastIndexOf('.'));
    }

    private String getViewHolderInteractorPackageName(TypeMirror typeMirror) {
        return typeMirror.toString().substring(0, typeMirror.toString().lastIndexOf('.'));
    }

    private String getViewHolderPackageName(TypeMirror typeMirror) {
        return typeMirror.toString().substring(0, typeMirror.toString().lastIndexOf('.'));
    }

    private String getViewHolder(TypeMirror typeMirror) {
        String x[] = typeMirror.toString().split("\\.");
        return x[x.length - 1];
    }

    private String getDataObject(TypeMirror typeMirror) {
        String x[] = typeMirror.toString().split("\\.");
        return x[x.length - 1];
    }

    private String getViewHolderInteractor(TypeMirror typeMirror) {
        String x[] = typeMirror.toString().split("\\.");
        return x[x.length - 1];
    }

    private String getDelegateClassName(TypeMirror typeMirror) {
        String x[] = typeMirror.toString().split("\\.");
        return x[x.length - 1].replace("ViewHolder", "AwesomeDelegate");
    }

}