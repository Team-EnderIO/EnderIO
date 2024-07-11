package me.liliandev.ensure;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.VariableTree;
import com.sun.tools.javac.tree.JCTree;
import me.liliandev.ensure.transformerutils.Handler;
import me.liliandev.ensure.transformerutils.Transformer;
import me.liliandev.ensure.transformerutils.TransformerUtil;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public class TransformerManager {
    private static final Map<String, Transformer> TRANSFORMERS = findTransformers();

    public static void handle(VariableTree argument, MethodTree tree, String className) {
        argument.getModifiers().getAnnotations()
            .stream()
            .map(TransformerManager::matchTransformer)
            .filter(tuple -> tuple.b() != null)
            .forEachOrdered(tuple -> tuple.b().transform(argument, tree, tuple.a(), className));
    }

    public static void handle(MethodTree tree, String className) {
        tree.getModifiers().getAnnotations()
            .stream()
            .map(TransformerManager::matchTransformer)
            .filter(tuple -> tuple.b() != null)
            .forEachOrdered(tuple -> tuple.b().transform(null, tree, tuple.a(), className));
    }

    private static String getName(AnnotationTree annotation) {
        if (annotation.getAnnotationType() instanceof JCTree.JCIdent ident) {
            if (ident.sym == null) {
                //some java.lang annotations don't get a symbol as they are in java.lang
                return null;
            }
            return ident.sym.flatName().toString();
        }
        return null;
    }

    private static Tuple<AnnotationTree, Transformer> matchTransformer(AnnotationTree annotation) {
        return new Tuple<>(annotation, TRANSFORMERS.get(getName(annotation)));
    }

    private static Map<String, Transformer> findTransformers() {
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
