package me.liliandev.ensure.transformerutils;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.VariableTree;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TransformerUtil {
    private static final Set<String> PRIMITIVES = Stream.of(byte.class, short.class, char.class, int.class, long.class, float.class, double.class).map(Class::getName).collect(Collectors.toUnmodifiableSet());

    private static final Map<String, Transformer> TRANSFORMERS = findTransformers();

    private static Context context = null;

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        TransformerUtil.context = context;
    }

    public static boolean isPrimitive(VariableTree parameter) {
        return PRIMITIVES.contains(parameter.getType().toString());
    }

    public static boolean isObject(VariableTree parameter) {
        return !isPrimitive(parameter);
    }

    /**
     *
     * @param method the method to add the check to
     * @param argument the argument to add the check for
     * @param condition the condition
     * @param errorMessage the errorMessage, the value of the thing will be printed after the error
     */
    public static void addCheck(MethodTree method, VariableTree argument, JCTree.JCExpression condition, String errorMessage) {
        JCTree.JCIf check = createCheck(argument, condition, getPos(argument, method), errorMessage);
        JCTree.JCBlock body = (JCTree.JCBlock) method.getBody();
        body.stats = body.stats.prepend(check);
    }
    private static JCTree.JCIf createCheck(VariableTree argument, JCTree.JCExpression condition, int pos, String errorMessage) {
        Context context = TransformerUtil.getContext();
        TreeMaker factory = TreeMaker.instance(context);
        Names symbolsTable = Names.instance(context);
        return factory.at(pos)
                .If(factory.Parens(condition),
                        buildErrorBlock(factory, symbolsTable, argument, errorMessage),
                        null);
    }

    public static int getPos(VariableTree argument, MethodTree method) {
        return argument == null ? ((JCTree)method).pos : ((JCTree)argument).pos;
    }

    public static JCTree.JCBlock buildErrorBlock(TreeMaker factory, Names symbolsTable, VariableTree argument, String errorMessage) {
        List<JCTree.JCExpression> content;
        JCTree.JCLiteral message = factory.Literal(TypeTag.CLASS, errorMessage);
        if (argument == null) {
            content = List.of(message);
        } else {
            content = List.of(
                    factory.Binary(
                            JCTree.Tag.PLUS,
                            factory.Literal(TypeTag.CLASS, errorMessage),
                            factory.Ident(getParameterId(symbolsTable, argument))
                    ));
        }

        return factory.Block(0,
                List.of(factory.Throw(factory.NewClass(null,
                        List.nil(),
                        factory.Ident(symbolsTable.fromString(IllegalArgumentException.class.getSimpleName())),
                        content,
                        null
                        ))
                ));
    }



    public static void handle(VariableTree argument, MethodTree tree, String className) {
        argument.getModifiers().getAnnotations()
                .stream()
                .map(TransformerUtil::matchTransformer)
                .filter(tuple -> tuple.b() != null)
                .forEachOrdered(tuple -> tuple.b().transform(argument, tree, tuple.a(), className));
    }
    public static void handle(MethodTree tree, String className) {
        tree.getModifiers().getAnnotations()
                .stream()
                .map(TransformerUtil::matchTransformer)
                .filter(tuple -> tuple.b() != null)
                .forEachOrdered(tuple -> tuple.b().transform(null, tree, tuple.a(), className));
    }
    public static String getName(AnnotationTree annotation) {

        if (annotation.getAnnotationType() instanceof JCTree.JCIdent ident) {
            if (ident.sym == null) {
                //some java.lang annotations don't get a symbol as they are in java.lang
                return null;
            }
            return ident.sym.flatName().toString();
        }
        return null;
    }

    public static Name getParameterId(Names symbolsTable, VariableTree parameter) {
        String parameterName = parameter.getName().toString();
        return symbolsTable.fromString(parameterName);
    }

    private static Tuple<AnnotationTree, Transformer> matchTransformer(AnnotationTree annotation) {
        return new Tuple<>(annotation, TRANSFORMERS.get(getName(annotation)));
    }


    public static Map<String, Transformer> findTransformers() {
        return ServiceLoader.load(Transformer.class, TransformerUtil.class.getClassLoader())
                .stream()
                .map(transformer -> findMarker(transformer.type()).map(annotation -> new Tuple<>(annotation, transformer.get())))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toMap(tuple -> tuple.a.getName(), tuple -> tuple.b));
    }

    private static  Optional<Class<? extends Annotation>> findMarker(Class<? extends Transformer> clazz) {
        Handler annotation = clazz.getAnnotation(Handler.class);
        if (annotation == null) {
            return Optional.empty();
        }
        return Optional.of(annotation.annotation());
    }

    private record Tuple<A, B>(A a, B b) {

    }
}
