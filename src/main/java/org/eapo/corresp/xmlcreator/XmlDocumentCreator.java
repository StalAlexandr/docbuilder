package org.eapo.corresp.xmlcreator;

import org.eapo.corresp.elementcreator.ElementCreatorSelector;
import org.eapo.corresp.util.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import org.eapo.corresp.elementcreator.AbstractElementCreator;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Alexandr Stal astal@eapo.org; al_stal@mail.ru
 *
 *
 * Класс отвечает за выгрузку данных из БД в XML Создает XML с выгруженными из
 * БД данными при помощи метода createDocumentFromXML 
 * Перед вызовом createDocumentFromXML при необходимости нужно передать
 * параметры SQL-запроса setParams
 *
 *
 */
public class XmlDocumentCreator {

   protected static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger("org.eapo.corresp");
    
    protected NamedParameterJdbcTemplate jdbcTemplateNamed;

    protected Map<String, Object> params;

    protected ElementCreatorSelector elementCreatorSelector;

    BuilderXmlSingleton builder = new BuilderXmlSingleton();

    /**
     *
     * @param pathToXMLFile путь к xml-файлу c SQL-запросами
     * @param documentName имя создаваемого документа
     * @return XML Document с результатами SQL-запросов
     * @throws ParserConfigurationException
     * @throws SQLException
     * @throws SAXException
     * @throws IOException
     * @throws Exception
     */
    public Document createDocumentFromXML(String pathToXMLFile, String documentName) throws ParserConfigurationException, SQLException, SAXException, IOException, Exception {

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
     *
     * @param documentForParse XML c SQL-запросами
     * @param documentName имя создаваемого документа
     * @return XML Document с результатами SQL-запросов
     * @throws ParserConfigurationException
     * @throws SQLException
     * @throws SAXException
     * @throws IOException
     * @throws Exception
     */
    public Document createDocumentFromXML(Document documentForParse, String documentName) throws ParserConfigurationException, SQLException, SAXException, IOException, Exception {

        Document resultDocument = BuilderXmlSingleton.documentBuilder().newDocument();
        Element rootElement = resultDocument.createElement(documentName);
        resultDocument.appendChild(rootElement);

        try {
      //      System.out.println(documentForParse.getNodeName());
            
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
    protected void parseNodeList(NodeList nodeList, Document resultDocument) throws IOException, SQLException, Exception {

        int length = nodeList.getLength();
        for (int item = 0; item < length; item++) {
            Node node = nodeList.item(item);
            if (node.getNodeType() == Node.ELEMENT_NODE) {

                AbstractElementCreator creator = elementCreatorSelector.select(node.getNodeName());

                if (creator != null) {
                    
                    creator.setParams(params);
                    creator.setJdbcTemplateNamed(jdbcTemplateNamed);
                    Element outElement = creator.createElement((Element) node);
                    resultDocument.adoptNode(outElement);
                    Element rootElement = resultDocument.getDocumentElement();
                    rootElement.appendChild(outElement);
                } else {
                    parseNodeList(node.getChildNodes(), resultDocument);
                }
            }
        }
    }

    /**
     *
     * @param jdbcTemplateNamed получение спрингового объекта выполняющего
     * SQL-запросы
     */
    public void setJdbcTemplateNamed(NamedParameterJdbcTemplate jdbcTemplateNamed) {
        this.jdbcTemplateNamed = jdbcTemplateNamed;
    }
    
    public NamedParameterJdbcTemplate getJdbcTemplateNamed() {
       return this.jdbcTemplateNamed;
    }
    

    /**
     *
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
