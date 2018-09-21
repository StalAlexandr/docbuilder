package org.eapo.corresp.reestrfields;

import org.eapo.corresp.elementcreator.AbstractElementCreator;
import org.eapo.corresp.util.BuilderXmlSingleton;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Alexandr Stal
 *         astal@eapo.org; al_stal@mail.ru
 */
public class RenewStates extends AbstractElementCreator {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(RenewStates.class);

    Document document;

    @Override
    public Element createElement(Element inElement) throws Exception {

        DocumentBuilder dBuilder = BuilderXmlSingleton.documentBuilder();

        document = dBuilder.newDocument();

        String nameElement = inElement.getAttribute("name");
        if (null == nameElement) {
            nameElement = "renewstates";
        }
        Element element = document.createElement(nameElement);

        List<Map<String, Object>> list = getRenewStates();

        Iterator<Map<String, Object>> iter = list.iterator();

        while (iter.hasNext()) {
            Element renewStsteElenemt = document.createElement(nameElement.substring(0, nameElement.length() - 1));

            Map<String, Object> map = iter.next();

            String str = "";
            Object o = map.get("idmember");
            if (o != null) str = o.toString();

            Element elementIdmember = document.createElement("idmember");
            elementIdmember.appendChild(document.createTextNode(str));
            renewStsteElenemt.appendChild(elementIdmember);


            str = "";
            o = map.get("idcountry");
            if (o != null) str = o.toString();

            Element elementIdcountry = document.createElement("idcountry");
            elementIdcountry.appendChild(document.createTextNode(str));
            renewStsteElenemt.appendChild(elementIdcountry);

            str = "";
            o = map.get("nmstate");
            if (o != null) str = o.toString();

            Element elementNmstate = document.createElement("nmstate");
            elementNmstate.appendChild(document.createTextNode(str));
            renewStsteElenemt.appendChild(elementNmstate);

            str = "";
            o = map.get("gennmstate");
            if (o != null) str = o.toString();

            Element elementGennmstate = document.createElement("gennmstate");
            elementGennmstate.appendChild(document.createTextNode(str));
            renewStsteElenemt.appendChild(elementGennmstate);

            str = "";
            o = map.get("renew");
            if (o != null) str = o.toString();


            Element elementRenew = document.createElement("renew");
            elementRenew.appendChild(document.createTextNode(str));
            renewStsteElenemt.appendChild(elementRenew);
            element.appendChild(renewStsteElenemt);
        }
        return element;
    }


    public List<Map<String, Object>> getRenewStates() throws SQLException {


        String query = " select b.idmember, b.idcountry, b.nmstate, b.gennmstate,  "
                + "(select count(*) from  lapsed a where a.idappli = :idappli and a.idmember = b.idmember  and cdreason >= 30 "
                + " and odhisto = (select max(odhisto) from lapsed c where c.idappli =  :idappli and c.idmember =  b.idmember)) as renew"
                + " FROM memberstates b where (b.datewithdr  > (select dtappli from ptappli where idappli = :idappli)) or (datewithdr is null) ";


        return jdbcTemplateNamed.queryForList(query, params);


    }

    ;


}
