package org.eapo.corresp.util;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * @author astal
 */
public class QueryExecutor {

    private NamedParameterJdbcTemplate jdbcTemplateNamed;

    private Map<String, ?> params;

    public void setParams(Map<String, ?> params) {
        this.params = params;
    }

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger("org.eapo.corresp");

    Document document;

    public QueryExecutor(NamedParameterJdbcTemplate jdbcTemplateNamed) throws ParserConfigurationException {

        this.jdbcTemplateNamed = jdbcTemplateNamed;
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;

        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            log.error("error build new DOM Document ");
            log.error(ex.getStackTrace());
            throw ex;
        }
        document = dBuilder.newDocument();
    }

    public Element createElementFromQuery(String query, String elementName, boolean isSingleElement, SimpleDateFormat sdf) throws SQLException {

        SimpleExtractor se = new SimpleExtractor();
        se.setCurrentDataFormat(sdf);
        se.setDocument(document);
        se.setElementName(elementName);
        se.setIsSingleElement(isSingleElement);
        Element element = (Element) jdbcTemplateNamed.query(query, params, se);
        return element;
    }

    public NamedParameterJdbcTemplate getJdbcTemplateNamed() {
        return jdbcTemplateNamed;
    }

    public void setJdbcTemplateNamed(NamedParameterJdbcTemplate jdbcTemplateNamed) {
        this.jdbcTemplateNamed = jdbcTemplateNamed;
    }

}
