package org.eapo.corresp.util;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Вспомогательный класс, генерирующий и передающий экземпляр DocumentBuilder
 * @author Alexandr Stal astal@eapo.org; al_stal@mail.ru
 */
public class BuilderXmlSingleton {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger("org.eapo.corresp");

    protected static DocumentBuilder dBuilder;

    public static DocumentBuilder documentBuilder() throws ParserConfigurationException {
        if (dBuilder != null) {
            return dBuilder;
        }
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            log.error("error build new DOM Document ");
            log.error(ex.getStackTrace());
            throw ex;
        }
        return dBuilder;
    }

}
