package me.liliandev.ensure.ensures;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.VariableTree;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;
import me.liliandev.ensure.transformerutils.Handler;
import me.liliandev.ensure.transformerutils.Transformer;
import me.liliandev.ensure.transformerutils.TransformerUtil;

@Handler(annotation = EnsureSide.class)
public class EnsureSideTransformer implements Transformer {

    @Override
    public void transform(VariableTree unusedMethod, MethodTree methodTree, AnnotationTree ensuresAnnotation, String className) {
        EnsureSide.Side side = getAnnotationArgument(ensuresAnnotation.getArguments().getFirst());
        TransformerUtil.addCheck(methodTree, unusedMethod, createIfCondition(side), createErrorMessage(side));
    }

    private static EnsureSide.Side getAnnotationArgument(ExpressionTree assignment) {
        return ((MemberSelectTree) ((JCTree.JCAssign)assignment).getExpression()).getIdentifier().contentEquals("CLIENT") ? EnsureSide.Side.CLIENT : EnsureSide.Side.SERVER;
    }

    private static JCTree.JCExpression createIfCondition(EnsureSide.Side side) {
        Context context = TransformerUtil.getContext();
        TreeMaker factory = TreeMaker.instance(context);
        Names symbolsTable = Names.instance(context);
        JCTree.JCMethodInvocation getSide = factory.Apply(
                List.nil(),
                qualifiedName(factory, symbolsTable, "net", "neoforged", "fml", "util", "thread", "EffectiveSide", "get"),
                List.nil()
        );
        JCTree.JCFieldAccess target = factory.Select(qualifiedName(factory, symbolsTable, "net", "neoforged", "fml", "LogicalSide"), symbolsTable.fromString(side.getOpposite()
                .name()));

        return factory.Binary(JCTree.Tag.EQ,
                getSide,
                target);
    }

    private static JCTree.JCExpression qualifiedName(TreeMaker factory, Names symbolsTable, String... name) {
        JCTree.JCExpression prior = factory.Ident(symbolsTable.fromString(name[0]));
        for(int i = 1; i < name.length; i++) {
            prior = factory.Select(prior, symbolsTable.fromString(name[i]));
        }
        return prior;
    }

    private static String createErrorMessage(EnsureSide.Side side) {
        return String.format(
                "Method should be called on %s but was called on %s",
                side.toString(), side.getOpposite().toString());
    }
}
