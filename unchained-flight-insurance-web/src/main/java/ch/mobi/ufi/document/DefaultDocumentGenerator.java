package ch.mobi.ufi.document;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

@RequiredArgsConstructor
public class DefaultDocumentGenerator implements DocumentGenerator<VelocityContext, String> {

    @NonNull
    VelocityEngine velocityEngine;

    @Override
    public String generate(VelocityContext context, String templateName) {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            File file = new File(classLoader.getResource("template/subscription.vm").getFile());
            String data = FileUtils.readFileToString(file, "UTF-8");

            StringWriter writer = new StringWriter();
            velocityEngine.evaluate(context, writer, "UFI", data);
            return writer.toString();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
