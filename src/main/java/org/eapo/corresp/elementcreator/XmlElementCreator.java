package org.eapo.corresp.elementcreator;

import org.eapo.corresp.xmlcreator.XmlDocumentCreator;
import org.w3c.dom.Element;

import javax.xml.parsers.ParserConfigurationException;

/**
 * @author astal Создает элемент xml на основе xml-документа с набором
 *         sql-запроов
 */
public class XmlElementCreator extends AbstractElementCreator {

    /**
     * @param inElement тело элемента должно содержать имя xml-документа с
     *                  sql-запросами
     * @return
     * @throws ParserConfigurationException
     * @throws Exception
     */
    @Override
    public Element createElement(Element inElement) throws Exception {

        String elementName = "elementName";

        if ((inElement.getAttributes() != null) && (inElement.getAttributes().getNamedItem("name")) != null) {
            elementName = inElement.getAttributes().getNamedItem("name").getNodeValue();
        }

        String fileName = inElement.getFirstChild().getNodeValue().trim().replaceAll(System.getProperty("line.separator"), "");

        XmlDocumentCreator dc = new XmlDocumentCreator();
        dc.setJdbcTemplateNamed(jdbcTemplateNamed);
        dc.setParams(params);
        return dc.createDocumentFromXML(fileName, elementName).getDocumentElement();

    }

}
