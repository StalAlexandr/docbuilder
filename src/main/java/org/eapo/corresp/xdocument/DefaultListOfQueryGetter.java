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
 * @author Alexandr Stal astal@eapo.org; al_stal@mail.ru
 */
public class DefaultListOfQueryGetter implements ListOfQueryGetter {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(DefaultListOfQueryGetter.class);

    @Override
    public List get(Path pathToTemplate) {

        List<String> names = new ArrayList<>();

        InputStream templateFile;
        try {
            templateFile = new FileInputStream(pathToTemplate.toFile());
        } catch (FileNotFoundException ex) {
            log.error(ex.getMessage());
            return names;
        }

        IXDocReport report;
        try {
            report = XDocReportRegistry.getRegistry().loadReport(templateFile, TemplateEngineKind.Freemarker);
        } catch (IOException | XDocReportException ex) {
            log.error(ex.getMessage());
            return names;
        }

        FieldsExtractor<FieldExtractor> extractor = new FieldsExtractor<>();

        try {
            report.extractFields(extractor);
        } catch (XDocReportException | IOException ex) {
            log.error(ex.getMessage());
            return names;
        }

        List<FieldExtractor> fields = extractor.getFields();
        for (FieldExtractor field : fields) {
            int ind = field.getName().indexOf(".");
            if (ind < 0)
                ind = field.getName().length() - 1;

            String str = field.getName().substring(0, ind);
            if (!names.contains(str)) {
                names.add(str);
            }
        }

        return names;

    }

}
