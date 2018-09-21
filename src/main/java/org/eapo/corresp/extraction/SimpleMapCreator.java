package org.eapo.corresp.extraction;

import org.eapo.corresp.util.QueryExecutor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.w3c.dom.Element;

import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * @author Alexandr Stal
 *         astal@eapo.org; al_stal@mail.ru
 */
public class SimpleMapCreator {

    protected static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger("org.eapo.corresp");

    public Object createElement(Element inElement) throws Exception {

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

        Object element;
        try {

            MapExtractor se = new MapExtractor();
            se.setCurrentDataFormat(new SimpleDateFormat(dateFormat));
            se.setElementName(elementName);
            se.setIsSingleElement(isSingleElement);
            element = jdbcTemplateNamed.query(query, params, se);
            return element;

        } catch (Exception ex) {
            log.error("SQL error ");
            log.error(ex.getStackTrace());
            throw ex;

        }
    }


    // связь с БД
    protected NamedParameterJdbcTemplate jdbcTemplateNamed;

    // дополнительные параметры
    protected Map<String, Object> params;

    public NamedParameterJdbcTemplate getJdbcTemplateNamed() {
        return jdbcTemplateNamed;
    }

    public void setJdbcTemplateNamed(NamedParameterJdbcTemplate jdbcTemplateNamed) {
        this.jdbcTemplateNamed = jdbcTemplateNamed;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

}
