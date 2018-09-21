package org.eapo.corresp.elementcreator;

import org.eapo.corresp.util.QueryExecutor;
import org.w3c.dom.Element;

import javax.xml.parsers.ParserConfigurationException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

/**
 * @author astal Создает элемент XML по SQL запросу в исходном элемента
 */
public class QueryElementCreator extends AbstractElementCreator {

    /**
     * @param inElement тело элемента должно содержать sql-запрос
     * @return
     * @throws SQLException
     * @throws ParserConfigurationException
     */
    @Override
    public Element createElement(Element inElement) throws SQLException, ParserConfigurationException {

        String elementName = "elementName";
        boolean isSingleElement = false;
        String dateFormat = "dd/MM/yyyy";

        if ((inElement.getAttributes() != null) && (inElement.getAttributes().getNamedItem("name")) != null) {
            elementName = inElement.getAttributes().getNamedItem("name").getNodeValue();
        }

        if ((inElement.getAttributes() != null) && (inElement.getAttributes().getNamedItem("single")) != null) {
            isSingleElement = true;
        }

        if ((inElement.getAttributes() != null) && (inElement.getAttributes().getNamedItem("dateformat")) != null) {
            elementName = inElement.getAttributes().getNamedItem("dateformat").getNodeValue();
        }

        String query = inElement.getFirstChild().getNodeValue().trim().replaceAll(System.getProperty("line.separator"), "");

        QueryExecutor executor = new QueryExecutor(getJdbcTemplateNamed());
        executor.setParams(params);

        Element element;
        try {
            element = executor.createElementFromQuery(query, elementName, isSingleElement, new SimpleDateFormat(dateFormat));
        } catch (SQLException ex) {
            log.error("SQL error ");
            log.error(ex.getStackTrace());
            throw ex;

        }
        return element;
    }

}
