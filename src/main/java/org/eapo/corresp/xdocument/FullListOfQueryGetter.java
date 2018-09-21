package org.eapo.corresp.xdocument;

import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.FieldExtractor;
import fr.opensagres.xdocreport.template.FieldsExtractor;
import fr.opensagres.xdocreport.template.TemplateEngineKind;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexandr Stal
 * astal@eapo.org; al_stal@mail.ru
 */
public class FullListOfQueryGetter implements ListOfQueryGetter {

    @Override
    public List get(Path pathToTemplate) {

        List<String> names = new ArrayList<>();

        InputStream templateFile;
        try {
            templateFile = new FileInputStream(pathToTemplate.toFile());
        } catch (FileNotFoundException ex) {

            return names;
        }

        IXDocReport report;
        try {
            report = XDocReportRegistry.getRegistry().loadReport(templateFile, TemplateEngineKind.Freemarker);
        } catch (IOException | XDocReportException ex) {
            return names;
        }

        FieldsExtractor<FieldExtractor> extractor = new FieldsExtractor<>();

        try {
            report.extractFields(extractor);
        } catch (XDocReportException | IOException ex) {
            return names;
        }

        List<FieldExtractor> fields = extractor.getFields();
        for (FieldExtractor field : fields) {
            String str = field.getName();
            if (!names.contains(str)) {
                names.add(str);
            }
        }

        return names;

    }

}
