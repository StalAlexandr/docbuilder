package org.eapo.corresp.extraction;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.w3c.dom.Document;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Alexandr Stal
 *         astal@eapo.org; al_stal@mail.ru
 */
class MapExtractor implements ResultSetExtractor {

    private Document document;
    private String elementName;
    private boolean isSingleElement;

    protected SimpleDateFormat sdf = null;

    @Override
    public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
        if ((elementName == null) || (elementName.length() < 1)) {
            elementName = "element";
        }

        Map<String, Object> element = new HashMap<>();

        if (isSingleElement) {
            if (rs.next()) {
                insertResultSetIntoElement(element, rs);
            }
            if (element.isEmpty()) { // забивка пустыми значениями если ничего не найдено
                fillElementByEmptyData(element, rs);
            }

            return element;
        }

        List list = new ArrayList();
        while (rs.next()) {
            Map<String, Object> map = new HashMap<>();

            insertResultSetIntoElement(map, rs);

            if (map.isEmpty()) {         // забивка пустыми значениями если ничего не найдено
                fillElementByEmptyData(map, rs);
            }
            list.add(map);

        }

        return list;
    }

    private void insertResultSetIntoElement(Map<String, Object> element, ResultSet rs) throws SQLException {

        try {
            if (rs.isClosed()) {
                return;
            }
        } catch (java.lang.AbstractMethodError ignored) {
        }

        if (sdf == null) {
            sdf = new SimpleDateFormat("dd/MM/yyyy");
        }

        int columnCount = rs.getMetaData().getColumnCount();
        for (int item = 1; item <= columnCount; item++) {

            String columnName = rs.getMetaData().getColumnName(item).toLowerCase();
            Map<String, Object> columnElement = new HashMap<>();
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
            element.put(columnName, value);
        }
    }


    private void fillElementByEmptyData(Map<String, Object> element, ResultSet rs) throws SQLException {

        int columnCount = rs.getMetaData().getColumnCount();
        for (int item = 1; item <= columnCount; item++) {
            String columnName = rs.getMetaData().getColumnName(item).toLowerCase();
            element.put(columnName, "");
        }

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
