package org.eapo.corresp.extraction;

import org.eapo.corresp.elementcreator.ElementCreatorSelector;
import org.eapo.corresp.util.BuilderXmlSingleton;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Alexandr Stal astal@eapo.org; al_stal@mail.ru
 */
public class Extractor {

    protected static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger("org.eapo.corresp");

    protected NamedParameterJdbcTemplate jdbcTemplateNamed;

    protected Map<String, Object> params;

    protected ElementCreatorSelector elementCreatorSelector;

    BuilderXmlSingleton builder = new BuilderXmlSingleton();

    /**
     * @param pathToXMLFile путь к xml-файлу c SQL-запросами
     * @param documentName  имя создаваемого документа
     * @return XML Document с результатами SQL-запросов
     * @throws ParserConfigurationException
     * @throws SQLException
     * @throws SAXException
     * @throws IOException
     * @throws Exception
     */
    public Map<String, Object> createDocumentFromXML(String pathToXMLFile, String documentName) throws ParserConfigurationException, SQLException, SAXException, IOException, Exception {

        Document documentForParse;
        try {
            documentForParse = BuilderXmlSingleton.documentBuilder().parse(pathToXMLFile);
        } catch (SAXException ex) {
            log.error("error parse file " + pathToXMLFile);
            log.error(ex.getStackTrace());
            throw ex;
        } catch (IOException ex) {
            log.error("can't read file " + pathToXMLFile);
            log.error(ex.getStackTrace());
            throw ex;
        }
        return createDocumentFromXML(documentForParse, documentName);
    }

    /**
     * @param documentForParse XML c SQL-запросами
     * @param documentName     имя создаваемого документа
     * @return XML Document с результатами SQL-запросов
     * @throws ParserConfigurationException
     * @throws SQLException
     * @throws SAXException
     * @throws IOException
     * @throws Exception
     */
    public Map<String, Object> createDocumentFromXML(Document documentForParse, String documentName) throws ParserConfigurationException, SQLException, SAXException, IOException, Exception {

        Map<String, Object> resultDocument = new HashMap<>(); // BuilderXmlSingleton.documentBuilder().newDocument();


        try {
            parseNodeList(documentForParse.getChildNodes(), resultDocument);
        } catch (IOException ex) {


            log.error(" error ");
            log.error(ex.getStackTrace());
            throw ex;


        } catch (SQLException ex) {
            log.error("SQL error ");
            log.error(ex.getStackTrace());
            throw ex;
        }
        return resultDocument;
    }

    /**
     * разбирает список элементов исходного документа nodeList, на основе
     * которого генерит новые элементы, присоединяемые к resultDocument
     *
     * @param nodeList
     * @param resultDocument
     * @throws IOException
     * @throws SQLException
     * @throws Exception
     */
    protected void parseNodeList(NodeList nodeList, Map<String, Object> resultDocument) throws IOException, SQLException, Exception {

        int length = nodeList.getLength();
        for (int item = 0; item < length; item++) {
            Node node = nodeList.item(item);
            if (node.getNodeType() == Node.ELEMENT_NODE) {


                if (node.getNodeName().equalsIgnoreCase("query")) {

                    SimpleMapCreator creator = new SimpleMapCreator();// elementCreatorSelector.select(node.getNodeName());

                    String elementName = "en";

                    if ((node.getAttributes() != null) && (node.getAttributes().getNamedItem("name")) != null) {
                        elementName = node.getAttributes().getNamedItem("name").getNodeValue();
                    }


                    creator.setParams(params);
                    creator.setJdbcTemplateNamed(jdbcTemplateNamed);
                    Object outElement = creator.createElement((Element) node);
                    resultDocument.put(elementName, outElement);
                } else parseNodeList(node.getChildNodes(), resultDocument);

            }
        }
    }

    /**
     * @param jdbcTemplateNamed получение спрингового объекта выполняющего
     *                          SQL-запросы
     */
    public void setJdbcTemplateNamed(NamedParameterJdbcTemplate jdbcTemplateNamed) {
        this.jdbcTemplateNamed = jdbcTemplateNamed;
    }

    public NamedParameterJdbcTemplate getJdbcTemplateNamed() {
        return this.jdbcTemplateNamed;
    }


    /**
     * @param params получение параметров входящих в SQL-запросы
     */
    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public ElementCreatorSelector getElementCreatorSelector() {
        return elementCreatorSelector;
    }

    public void setElementCreatorSelector(ElementCreatorSelector elementCreatorSelector) {
        this.elementCreatorSelector = elementCreatorSelector;
    }


}
