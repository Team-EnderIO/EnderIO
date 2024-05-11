package me.liliandev.ensure.ensures;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.VariableTree;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import me.liliandev.ensure.transformerutils.Handler;
import me.liliandev.ensure.transformerutils.Transformer;
import me.liliandev.ensure.transformerutils.TransformerUtil;

import java.lang.annotation.AnnotationFormatError;

@Handler(annotation = EnsureNotNull.class)
public class EnsureNotNullTransformer implements Transformer {

    @Override
    public void transform(VariableTree variableTree, MethodTree methodTree, AnnotationTree ensuresAnnotation, String className) {
        if (TransformerUtil.isPrimitive(variableTree)) {
            throw new AnnotationFormatError(
                "Not null can only be added to non primitives @" + className + "." + methodTree.getName() + " argument: " + variableTree.getName());
        }
        TransformerUtil.addCheck(methodTree, variableTree, createIfCondition(variableTree), createErrorMessage(variableTree));
    }

    private static JCTree.JCBinary createIfCondition(VariableTree parameter) {
        Context context = TransformerUtil.getContext();
        TreeMaker factory = TreeMaker.instance(context);
        Names symbolsTable = Names.instance(context);
        Name parameterId = symbolsTable.fromString(parameter.getName().toString());
        return factory.Binary(JCTree.Tag.EQ,
                factory.Ident(parameterId),
                factory.Literal(TypeTag.BOT, null));
    }

    private static String createErrorMessage(VariableTree parameter) {
        String parameterName = parameter.getName().toString();
        return String.format(
                "Argument '%s' of type %s is marked by @EnsureNotNull but was: ",
                parameterName, parameter.getType());
    }
}
