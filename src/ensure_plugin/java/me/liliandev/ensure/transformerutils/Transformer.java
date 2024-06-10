package me.liliandev.ensure.transformerutils;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.VariableTree;

public interface Transformer {

    void transform(VariableTree variableTree, MethodTree methodTree, AnnotationTree ensuresAnnotation, String className);
}
