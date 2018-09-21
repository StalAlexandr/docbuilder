package org.eapo.corresp.util;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

/**
 * @author astal
 */
class SimpleExtractor implements ResultSetExtractor {

    private Document document;
    private String elementName;
    private boolean isSingleElement;

    protected SimpleDateFormat sdf = null;

    @Override
    public Object extractData(ResultSet rs) throws SQLException, DataAccessException {

        String childElementName;

        if ((elementName == null) || (elementName.length() < 1)) {
            elementName = "element";
            childElementName = "child";
        } else {
            childElementName = elementName.substring(0, elementName.length() - 1);
        }

        Element element = document.createElement(elementName);

        if (isSingleElement) {
            if (rs.next()) {
                insertResultSetIntoElement(element, rs);
            }
            if (!element.hasChildNodes()) {         // забивка пустыми значениями если ничего не найдено
                fillElementByEmptyData(element, rs);
            }

            return element;
        }

        while (rs.next()) {
            Element childElement = document.createElement(childElementName);
            insertResultSetIntoElement(childElement, rs);
            element.appendChild(childElement);
        }

        if (!element.hasChildNodes()) {  // забивка пустыми значениями если ничего не найдено
            Element childElement = document.createElement(childElementName);
            fillElementByEmptyData(childElement, rs);
            element.appendChild(childElement);
        }

        return element;
    }

    private void insertResultSetIntoElement(Element element, ResultSet rs) throws SQLException {

        try {
            if (rs.isClosed()) {
                return;
            }
        } catch (java.lang.AbstractMethodError e) {
        }

        if (sdf == null) {
            sdf = new SimpleDateFormat("dd/MM/yyyy");
        }

        int columnCount = rs.getMetaData().getColumnCount();
        for (int item = 1; item <= columnCount; item++) {

            String columnName = rs.getMetaData().getColumnName(item).toLowerCase();
            Element columnElement = document.createElement(columnName);
            String value = "";
            Object obj = rs.getObject(item);
            if (obj != null) {
                value = obj.toString();
            }

            if (rs.getMetaData().getColumnType(item) == java.sql.Types.DATE) {
                java.sql.Date dt = rs.getDate(item);

                if (dt != null) {
                    value = sdf.format(dt);
                }

            }

            columnElement.appendChild(document.createTextNode(value));
            element.appendChild(columnElement);
        }
    }

    private void fillElementByEmptyData(Element element, ResultSet rs) throws SQLException {
        int columnCount = rs.getMetaData().getColumnCount();
        for (int item = 1; item <= columnCount; item++) {

            String columnName = rs.getMetaData().getColumnName(item).toLowerCase();

            Element columnElement = document.createElement(columnName);
            columnElement.appendChild(document.createTextNode(""));
            element.appendChild(columnElement);
        }

    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public String getElementName() {
        return elementName;
    }

    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    public void setIsSingleElement(boolean isSingleElement) {
        this.isSingleElement = isSingleElement;
    }

    public SimpleDateFormat getCurrentDataFormat() {
        return sdf;
    }

    public void setCurrentDataFormat(SimpleDateFormat sdf) {
        this.sdf = sdf;
    }

}
