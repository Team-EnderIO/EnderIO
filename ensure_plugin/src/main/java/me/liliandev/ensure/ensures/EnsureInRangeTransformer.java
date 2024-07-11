package me.liliandev.ensure.ensures;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.VariableTree;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import me.liliandev.ensure.transformerutils.Handler;
import me.liliandev.ensure.transformerutils.Transformer;
import me.liliandev.ensure.transformerutils.TransformerUtil;

import java.lang.annotation.AnnotationFormatError;

@Handler(annotation = EnsureInRange.class)
public class EnsureInRangeTransformer implements Transformer {

    @Override
    public void transform(VariableTree variableTree, MethodTree methodTree, AnnotationTree ensuresAnnotation, String className) {
        if (TransformerUtil.isObject(variableTree)) {
            throw new AnnotationFormatError(
                "Ranges can only be added to primitives @" + className + "." + methodTree.getName() + " argument: " + variableTree.getName());
        }
        JCTree.JCExpression min = getAnnotationArgument(ensuresAnnotation.getArguments().get(0));
        JCTree.JCExpression max = getAnnotationArgument(ensuresAnnotation.getArguments().get(1));
        TransformerUtil.addCheck(methodTree, variableTree, createIfCondition(variableTree, min, max), createErrorMessage(variableTree, min, max));
    }

    private static JCTree.JCExpression getAnnotationArgument(ExpressionTree assignment) {
        return ((JCTree.JCAssign) assignment).getExpression();
    }

    private static JCTree.JCBinary createIfCondition(VariableTree argument, JCTree.JCExpression min, JCTree.JCExpression max) {
        Context context = TransformerUtil.getContext();
        TreeMaker factory = TreeMaker.instance(context);
        Names symbolsTable = Names.instance(context);
        Name argumentId = symbolsTable.fromString(argument.getName().toString());
        JCTree.JCBinary minCheck = factory.Binary(JCTree.Tag.LT, factory.Ident(argumentId), min);
        JCTree.JCBinary maxCheck = factory.Binary(JCTree.Tag.GE, factory.Ident(argumentId), max);
        return factory.Binary(JCTree.Tag.OR, minCheck, maxCheck);
    }

    private static String createErrorMessage(VariableTree parameter, JCTree.JCExpression min, JCTree.JCExpression max) {
        String parameterName = parameter.getName().toString();
        return String.format(
                "Argument '%s' of type %s should be in range [%s, %s) but was: ",
                parameterName, parameter.getType(), min.toString(), max.toString());
    }
}
