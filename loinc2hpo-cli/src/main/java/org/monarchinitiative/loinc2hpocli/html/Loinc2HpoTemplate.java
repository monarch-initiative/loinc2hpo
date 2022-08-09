package org.monarchinitiative.loinc2hpocli.html;

import freemarker.template.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Loinc2HpoTemplate {

    protected final Map<String, Object> templateData= new HashMap<>();
    /** FreeMarker configuration object. */
    protected final Configuration cfg;

    public Loinc2HpoTemplate(List<LoincVisualizable> visualizableList) throws IOException {
        this.cfg = new Configuration(new Version("2.3.23"));
        cfg.setDefaultEncoding("UTF-8");
        cfg.setClassForTemplateLoading(Loinc2HpoTemplate.class, "");
        cfg.setDirectoryForTemplateLoading(new File("loinc2hpo-cli/src/main/resources"));
       // cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.DEBUG_HANDLER);
        cfg.setLogTemplateExceptions(false);
        // Wrap unchecked exceptions thrown during template processing into TemplateException-s:
        cfg.setWrapUncheckedExceptions(true);
        // Do not fall back to higher scopes when reading a null loop variable:
        cfg.setFallbackOnNullLoopVariable(false);
        this.templateData.put("loincItems", visualizableList);
    }

    public void outputFile() {
        String outputPath = "test.html";
        try (BufferedWriter out = Files.newBufferedWriter(Path.of(outputPath))) {
            Template template = cfg.getTemplate("annotations.ftl");
            template.process(templateData, out);
        } catch (TemplateException | IOException te) {
            System.out.printf("Error writing out results: %s", te.getMessage());
        }
    }
}
