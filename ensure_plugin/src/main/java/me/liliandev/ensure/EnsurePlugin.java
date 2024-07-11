package me.liliandev.ensure;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.Plugin;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.api.BasicJavacTask;
import me.liliandev.ensure.setup.EnsureSetup;
import me.liliandev.ensure.transformerutils.TransformerUtil;

public class EnsurePlugin implements Plugin {

    @Override
    public String getName() {
        return "ContextEnsure";
    }

    @Override
    public void init(JavacTask task, String... args) {
        try {
            EnsureSetup.setup();
        } catch (Exception e) {
            throw new IllegalStateException("Setup and opening of required packages failed", e);
        }
        TransformerUtil.setContext(((BasicJavacTask) task).getContext());
        task.addTaskListener(new TaskListener() {
            @Override
            public void finished(TaskEvent e) {
                if (e.getKind() == TaskEvent.Kind.ENTER) {
                    afterASTBuild(e);
                }
            }
        });
    }

    public static String className = "";
    private void afterASTBuild(TaskEvent e) {
        e.getCompilationUnit().accept(new TreeScanner<Void, Void>() {

            @Override
            public Void visitClass(ClassTree node, Void aVoid) {
                className = node.getSimpleName().toString();
                return super.visitClass(node, aVoid);
            }

            @Override
            public Void visitMethod(MethodTree methodTree, Void aVoid) {
                TransformerManager.handle(methodTree, className);
                methodTree.getParameters().forEach(parameter -> TransformerManager.handle(parameter, methodTree, className));
                return super.visitMethod(methodTree, aVoid);
            }
        }, null);
    }
}
