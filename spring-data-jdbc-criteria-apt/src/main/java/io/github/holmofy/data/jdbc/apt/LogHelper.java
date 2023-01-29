package io.github.holmofy.data.jdbc.apt;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic;

public class LogHelper {

    private final ProcessingEnvironment processingEnv;
    private final boolean shouldLogInfo;

    public LogHelper(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
        this.shouldLogInfo = processingEnv.getOptions().containsKey(APTOptions.show_log);
    }

    public void log(Diagnostic.Kind kind, String message) {
        if (shouldLogInfo) {
            processingEnv.getMessager().printMessage(kind, message);
        }
    }
}
