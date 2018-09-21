package org.eapo.corresp.util;

import org.w3c.dom.Document;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

/**
 * @author Alexandr Stal astal@eapo.org; al_stal@mail.ru
 */
public class XmlSaver {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger("org.eapo.corresp");

    /**
     * Сохраняет xml в файл
     *
     * @param doc
     * @param path
     */
    public static void saveXmlDocument(Document doc, String path) {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(path));

            transformer.transform(source, result);
        } catch (TransformerConfigurationException ex) {
            log.error(ex);
        } catch (TransformerException ex) {
            log.error(ex);
        }
    }

}
