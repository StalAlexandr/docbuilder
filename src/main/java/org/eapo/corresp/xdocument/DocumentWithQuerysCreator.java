package org.eapo.corresp.xdocument;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;

import org.eapo.corresp.util.BuilderXmlSingleton;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Alexandr Stal astal@eapo.org; al_stal@mail.ru
 */
public class DocumentWithQuerysCreator {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(DocumentWithQuerysCreator.class);

    public Document getDocument(List<String> queryNames, String src) {
        Document resultDocument;
        try {
            resultDocument = BuilderXmlSingleton.documentBuilder().newDocument();
        } catch (ParserConfigurationException ex) {
            return null;
        }

        Map<String, Element> map = new HashMap<>();

        Document documentForParse;
        try {
            try {
                documentForParse = BuilderXmlSingleton.documentBuilder().parse(new File(src));
            } catch (SAXException | IOException ex) {
                return null;
            }
        } catch (ParserConfigurationException ex) {
            return null;
        }

        parseNodeList(documentForParse.getChildNodes(), map);

        Element rootElement = resultDocument.createElement("header");
        resultDocument.appendChild(rootElement);

        for (String str : queryNames) {
            Element el = map.get(str);
            if (el == null) {
                el = map.get(str + "s");
            }
            if (el != null) {
                resultDocument.adoptNode(el);
                rootElement.appendChild(el);
            }
        }

        return resultDocument;
    }

    protected static void parseNodeList(NodeList nodeList, Map<String, Element> map) {

        int length = nodeList.getLength();
        for (int item = 0; item < length; item++) {
            Node node = nodeList.item(item);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                if ((element.getNodeName().equalsIgnoreCase("query")) || (element.getNodeName().equalsIgnoreCase("customclass"))) {
                    map.put(element.getAttribute("name"), element);
                } else {
                    parseNodeList(node.getChildNodes(), map);
                }
            }
        }
    }

}
