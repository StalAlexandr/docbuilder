package org.eapo.corresp.pathgetter;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Alexandr Stal astal@eapo.org; al_stal@mail.ru Вычисляет путь к
 *         шаблону с sql по idletter
 */
public class PathToTemplateDefault extends PathGetter {

    private String docxprefix;
    private String docxextension;
    private String pathtodocxtemplates;

    @Override
    public Path get(String idletter) {

        return pathToWorkDir.resolve(Paths.get(pathtodocxtemplates).resolve(Paths.get(docxprefix + idletter + docxextension)));
    }

    public String getDocxprefix() {
        return docxprefix;
    }

    public void setDocxprefix(String docxprefix) {
        this.docxprefix = docxprefix;
    }

    public String getDocxextension() {
        return docxextension;
    }

    public void setDocxextension(String docxextension) {
        this.docxextension = docxextension;
    }

    public String getPathtodocxtemplates() {
        return pathtodocxtemplates;
    }

    public void setPathtodocxtemplates(String pathtodocxtemplates) {
        this.pathtodocxtemplates = pathtodocxtemplates;
    }

}
