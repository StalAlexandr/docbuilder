package org.eapo.corresp.xdocument;

import java.io.OutputStream;
import java.util.Map;
import org.w3c.dom.Node;

/**
 * @author Alexandr Stal astal@eapo.org; al_stal@mail.ru
 * Генерация документа в OutputStream исходя из idappli и idletter
 */
public interface XDocumentCreator {
    String getIdletter();

    /**
     * Параметры, передаваемые в SQL
     */
    Map<String, Object> getParams();

    boolean process(OutputStream out);

    void setIdletter(String idletter);

    void setParams(Map<String, Object> params);
    
    void addAdditionalNode(Node node);  

}
