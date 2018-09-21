package org.eapo.corresp.xmlcreator;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.eapo.corresp.xdocument.WordDocumentCreator;
/**
 *
 * @author astal
 */
public class TestStarter {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(TestStarter.class);

    private Map<String, String> params;

    public static void main(String args[]) throws TransformerConfigurationException, TransformerException, SQLException, Exception {

        WordDocumentCreator creator = new WordDocumentCreator();

        List<String> applis = new LinkedList();

        applis.add("X1999026148");
       
        
        for (String appli:applis)
        {
          String idappli = appli;// "X1999014100";
          
        Map<String, Object> map = new HashMap<>();
        map.put("idappli", idappli);
        
        creator.setParams(map);
        
        ArrayList<String> letters = new ArrayList<>();

       letters.add("R10");
       
        Path pathToResultDir = Paths.get("C:\\resultletters");

        for (String idletter : letters) {
            Path resultFile = pathToResultDir.resolve(Paths.get(idletter + ".docx"));
            try(OutputStream fos = new FileOutputStream(resultFile.toFile())){
            creator.setIdletter(idletter);
            if (creator.process(fos)==false)
                
                    System.err.println("Ошибка в реестре для заявки " + appli);
                break;
            }
        }
        }
        /*
         ApplicationContext ctx = new FileSystemXmlApplicationContext("C:\\testxml\\corresp_context.xml");
          XmlDocumentCreator starter = (XmlDocumentCreator) ctx.getBean("xmlDocumentCreator");

         Map<String, String> map = new HashMap<>();
         map.put("extidappli", "200800011");
         map.put("idletter", "100");
         map.put("idappli", "X1999014100");
            
         starter.setParams(map);

         Document document =  starter.createDocumentFromXML("C:\\testxml\\test.xml", "root");
          
         TransformerFactory transformerFactory = TransformerFactory.newInstance();
         Transformer transformer = transformerFactory.newTransformer();
         DOMSource source = new DOMSource(document);
         StreamResult result = new StreamResult(new File("C:\\testxml\\file.xml"));
         transformer.transform(source, result);
        
        
        //  String test = "304.properties";

       /*
         PropertyConfigurator.configure("log4j.properties");
            
         ApplicationContext ctx = new FileSystemXmlApplicationContext("C:\\testxml\\corresp_context.xml");
         XmlDocumentCreator starter = (XmlDocumentCreator) ctx.getBean("xmlDocumentCreator");

         Map<String, String> map = new HashMap<>();
         map.put("extidappli", "200800011");
         map.put("idletter", "100");
         map.put("idappli", "X1999014100");
            
         starter.setParams(map);

         Document document =  starter.createDocumentFromXML("C:\\tomcat\\webapps\\SopranoV522\\WEB-INF\\correspcreator\\querys\\default.xml", "root");

           
         NodeList list = document.getDocumentElement().getChildNodes();
         for (int i = 0; i<list.getLength(); i++){
         Node node = list.item(i);
         log.info(node.getNodeName());

            
         TransformerFactory transformerFactory = TransformerFactory.newInstance();
         Transformer transformer = transformerFactory.newTransformer();
         DOMSource source = new DOMSource(document);
         StreamResult result = new StreamResult(new File("C:\\testxml\\file.xml"));
         transformer.transform(source, result);
            
            
         //   context.put(node.getNodeName().trim(), node);
         }
           
           
  

         //       XmlDocumentCreator dc = new XmlDocumentCreator();
         //      dc.createfromDocument(document);
            
         */
        
          /*
         letters.add("103");
         letters.add("104");
         letters.add("105");
         letters.add("106");
         letters.add("107");
         letters.add("108");
         letters.add("110");
         letters.add("111");
         letters.add("116");
         letters.add("116_int");
         letters.add("117");
         letters.add("126");
         letters.add("130");
         letters.add("131");
        
         letters.add("132");
         letters.add("133");
         letters.add("134");
         letters.add("138");
         letters.add("140a");
         letters.add("150");
         letters.add("151");
         letters.add("152");
         letters.add("153");
         letters.add("154");
        
         letters.add("155");
         letters.add("155a");
         letters.add("156");
         letters.add("157");
         letters.add("158");
         letters.add("159");
         letters.add("160");
         letters.add("160n");
         letters.add("170");
         letters.add("180");
         letters.add("181");
         letters.add("191");

         letters.add("300");
         letters.add("301");
         letters.add("302");
         letters.add("303");
         letters.add("304");
         letters.add("305");
        
         letters.add("307");
         letters.add("310");
         letters.add("310fi");
         letters.add("312");
         letters.add("314a");
         letters.add("317");
         letters.add("318");
         letters.add("319");
         letters.add("320");
         letters.add("321");
         letters.add("322");
         letters.add("323");
         letters.add("324");
         letters.add("324b");
         letters.add("325a");
         letters.add("325b");
       
    //    letters.add("326a");
         */
        /*
         letters.add("330");
         letters.add("334");
         letters.add("labelr");
         letters.add("labelr");
         letters.add("labelr");

         letters.add("803");
         letters.add("patent_m");
       */
     //    letters.add("patent_n");
/*
         letters.add("600");
         letters.add("601");
         letters.add("602");
         letters.add("603");
         letters.add("604");
         letters.add("605");
       
         
         letters.add("600");
  */
         
         //   letters.add("R50");
  // letters.add("R10");

        //   letters.add("803");

        
    }

}
