plugins {
    id("java-library")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8" // Use the UTF-8 charset for Java compilation
    options.compilerArgs.add("--add-exports=jdk.compiler/com.sun.tools.javac.api=ensureplugin")
    options.compilerArgs.add("--add-exports=jdk.compiler/com.sun.tools.javac.code=ensureplugin")
    options.compilerArgs.add("--add-exports=jdk.compiler/com.sun.tools.javac.tree=ensureplugin")
    options.compilerArgs.add("--add-exports=jdk.compiler/com.sun.tools.javac.util=ensureplugin")
}
