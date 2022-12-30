package io.github.holmofy.example;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import io.github.holmofy.data.jdbc.apt.SpringDataRelationalAnnotationProcessor;
import org.junit.Test;

import static com.google.testing.compile.CompilationSubject.assertThat;

public class CompileTest {

    @Test
    public void test() {
        Compilation compilation =
                Compiler.javac()
                        .withProcessors(new SpringDataRelationalAnnotationProcessor())
                        .compile(JavaFileObjects.forResource("TestModel.java"));
        assertThat(compilation).succeeded();
        assertThat(compilation)
                .generatedSourceFile("io.github.holmofy.example.TestModel_")
                .hasSourceEquivalentTo(JavaFileObjects.forResource("TestModel_.java"));
    }

}
