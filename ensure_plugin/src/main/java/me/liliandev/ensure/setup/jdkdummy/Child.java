package me.liliandev.ensure.setup.jdkdummy;

@SuppressWarnings("all")
public class Child extends Parent {
    private transient volatile boolean foo;
    private transient volatile Object[] bar;
    private transient volatile Object baz;

}
