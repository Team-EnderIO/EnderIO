package me.liliandev.ensure.transformerutils;

import com.sun.source.tree.MethodTree;
import com.sun.source.tree.VariableTree;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TransformerUtil {
    private static final Set<String> PRIMITIVES = Stream.of(byte.class, short.class, char.class, int.class, long.class, float.class, double.class).map(Class::getName).collect(Collectors.toUnmodifiableSet());

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
        boolean shouldDelayCheck1Statement = false;
        if (!body.stats.isEmpty()) {
            JCTree.JCStatement firstStatement = body.stats.getFirst();
            if (firstStatement instanceof JCTree.JCExpressionStatement potentialConstructor) {
                if (potentialConstructor.getExpression() instanceof JCTree.JCMethodInvocation methodInvocation) {
                    if (methodInvocation.meth instanceof JCTree.JCIdent methodIdentifier && methodIdentifier.name.contentEquals("super")) {
                        shouldDelayCheck1Statement = true;
                    }
                }
            }
        }
        if (shouldDelayCheck1Statement) {
            List<JCTree.JCStatement> existingStatements = body.stats;
            body.stats = List.of(existingStatements.getFirst());
            body.stats = body.stats.append(check);
            for (int i = 1; i < existingStatements.size(); i++) {
                body.stats = body.stats.append(existingStatements.get(i));
            }
        } else {
            body.stats = body.stats.prepend(check);
        }
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

    public static Name getParameterId(Names symbolsTable, VariableTree parameter) {
        String parameterName = parameter.getName().toString();
        return symbolsTable.fromString(parameterName);
    }
}
