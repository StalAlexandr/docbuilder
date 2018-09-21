package org.eapo.corresp.reestrfields;

import org.eapo.corresp.elementcreator.AbstractElementCreator;
import org.eapo.corresp.util.BuilderXmlSingleton;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * @author Alexandr Stal astal@eapo.org; al_stal@mail.ru
 */
public class SteppayInfo extends AbstractElementCreator {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(SteppayInfo.class);

    Document document;

    @Override
    public Element createElement(Element inElement) throws Exception {

        DocumentBuilder dBuilder = BuilderXmlSingleton.documentBuilder();

        document = dBuilder.newDocument();

        String nameElement = inElement.getAttribute("name");
        if (null == nameElement) {
            nameElement = "SteppayInfo";
        }
        Element element = document.createElement(nameElement);

        try {

            Element firstPayElement = document.createElement("firstpayyear");
            firstPayElement.appendChild(document.createTextNode(getFirstPayYear().toString()));
            element.appendChild(firstPayElement);

            String strDtPay = "";
            String strDtPayStep2 = "";


            Date dtpay = getDtNextpay();
            if (null == dtpay) {
                dtpay = getPseudoDtNextPay();
            }

            if (null != dtpay) {

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

                strDtPay = sdf.format(dtpay);

                Calendar c = Calendar.getInstance();
                c.setTime(dtpay);
                c.add(Calendar.YEAR, 1);

                strDtPayStep2 = sdf.format(new Date(c.getTime().getTime()));

            }

            Element dtpayElement = document.createElement("dtpay");
            dtpayElement.appendChild(document.createTextNode(strDtPay));
            element.appendChild(dtpayElement);

            Element dtpay2Element = document.createElement("dtpaystep2");
            dtpay2Element.appendChild(document.createTextNode(strDtPayStep2));
            element.appendChild(dtpay2Element);
        } catch (IllegalStateException e) {

            Element errorElement = document.createElement("error");
            errorElement.appendChild(document.createTextNode(e.getMessage()));
            element.appendChild(errorElement);
        }


        return element;
    }

    private Integer getFirstPayYear() {

        Date dtappli = getDtAppli();

        if (null == dtappli) {
            return 0;
        }

        Date dtnextpay = getDtNextpay();
        if (null == dtnextpay) {
            dtnextpay = getPseudoDtNextPay();
            if (null == dtnextpay) {
                return 0;
            }
        }
        Calendar cdtnextpay = Calendar.getInstance();
        cdtnextpay.setTime(dtnextpay);

        Calendar cdtappli = Calendar.getInstance();
        cdtappli.setTime(dtappli);

        return cdtnextpay.get(Calendar.YEAR) - cdtappli.get(Calendar.YEAR) + 1;

    }

    private java.sql.Date getDtPubli() {
        List<Map<String, Object>> list;
        String query = "SELECT dttopubli from publication where idappli = :idappli  and odhisto = (select max(odhisto) from history where idappli = :idappli and idoper='141')";
        list = jdbcTemplateNamed.queryForList(query, params);
        for (Map<String, Object> map : list) {
            Object o = map.get("dttopubli");
            if (o instanceof java.sql.Date) {
                return (java.sql.Date) o;
            }
        }
        return null;
    }

    private java.sql.Date getDtFinal() {
        List<Map<String, Object>> list;
        String query = "SELECT dtfinal from ptappli where idappli = :idappli";
        list = jdbcTemplateNamed.queryForList(query, params);
        for (Map<String, Object> map : list) {
            Object o = map.get("dtfinal");
            if (o instanceof java.sql.Date) {
                return (java.sql.Date) o;
            }
        }
        return null;
    }

    private java.sql.Date getDtAppli() {
        List<Map<String, Object>> list;
        String query = "SELECT dtappli from ptappli where idappli = :idappli";
        list = jdbcTemplateNamed.queryForList(query, params);
        for (Map<String, Object> map : list) {
            Object o = map.get("dtappli");
            if (o instanceof java.sql.Date) {
                return (java.sql.Date) o;
            }
        }
        return null;
    }

    private java.sql.Date getDtNextpay() {
        List<Map<String, Object>> list;
        String query = "SELECT dtnextpay from ptappli where idappli = :idappli";
        list = jdbcTemplateNamed.queryForList(query, params);
        for (Map<String, Object> map : list) {
            Object o = map.get("dtnextpay");
            if (o instanceof java.sql.Date) {
                return (java.sql.Date) o;
            }
        }
        return null;
    }

    private Date getPseudoDtNextPay() {

        Date dtappli = getDtAppli();
        Date dtfinal = getDtFinal();

        if (null == dtappli) {
            return null;
        }

        if (null == dtfinal) {
            dtfinal = getDtPubli();
        }
        if (null == dtfinal) {
            throw new IllegalStateException("illegal dtfinal");
        }

        Calendar cdtfinal = Calendar.getInstance();
        cdtfinal.setTime(dtfinal);

        Calendar cdtappli = Calendar.getInstance();
        cdtappli.setTime(dtappli);

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, cdtfinal.get(Calendar.YEAR));
        c.set(Calendar.MONTH, cdtappli.get(Calendar.MONTH));
        c.set(Calendar.DATE, cdtappli.get(Calendar.DATE));

        if (c.before(cdtfinal)) {
            c.set(Calendar.YEAR, cdtfinal.get(Calendar.YEAR) + 1);
        }
        return new java.sql.Date(c.getTime().getTime());

    }

}
