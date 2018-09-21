/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eapo.corresp.xdocument;

import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.core.document.SyntaxKind;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.PropertyConfigurator;
import org.apache.xerces.dom.ElementImpl;
import org.eapo.corresp.converter.CharConverter;
import org.eapo.corresp.converter.SpecialCharConverter;
import org.eapo.corresp.pathgetter.PathGetter;
import org.eapo.corresp.pathgetter.PathsContainer;
import org.eapo.corresp.util.XmlSaver;
import org.eapo.corresp.xmlcreator.XmlDocumentCreator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author astal Генерация документа в OutputStream исходя из idappli и idletter
 * Реализация для Word
 */
public class WordDocumentCreator implements XDocumentCreator {

    protected XmlDocumentCreator xmlDocumentCreator;

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger("org.eapo.corresp");

    private final ApplicationContext ctx;                        // спринговый контекст
    private final String CONTEXTFILE = "corresp_context.xml";    // файл спрингового контекста приложения 

    // имена свойств из спрингового контекста:
    private final String STRXMLDOCUMENTCREATOR = "xmlDocumentCreator";  // бин класса, генерирующего xml c экстрактом sql-запросов
    private final String PATHTORESULTXML = "pathToResultXML";  // 
    private final String PATHSCONTAINER = "pathsContainer";
    private final String PATHTOLOG = "pathToLog";

    private String pathToResultXML = "";
    private String pathToLog = "";
    private PathsContainer pathsContainer;

    // входные данные
    private String idletter = "";

    // строки, выводимые в текст итогового док-та при ошибках
    private final String ERRORMESSAGE = "errorMessage";
    private final String ERRORTEXT = "errorText";

    // объекты XDocReport
    private IXDocReport report;
    private IContext context;

    private Node additionalNode = null;

    // параметры в SQL
    Map<String, Object> params = new HashMap<>();

    {
      //  PathGetter getterPathToDir = new PathToWorkDirDefault();
        //  Path pathToWorkDir = getterPathToDir.get("");

      //  PathGetter.setPathToWorkDir(pathToWorkDir);
        String workDir = System.getProperty("soprano.corresp.home");
     ///   if (workDir==null)
        //      workDir="";

        Path pathToWorkDir = Paths.get(workDir);
        PathGetter.setPathToWorkDir(pathToWorkDir);

        ctx = new FileSystemXmlApplicationContext("/" + pathToWorkDir.resolve(CONTEXTFILE).toString());

        xmlDocumentCreator = (XmlDocumentCreator) ctx.getBean(STRXMLDOCUMENTCREATOR);
        pathsContainer = (PathsContainer) ctx.getBean(PATHSCONTAINER);

        pathToResultXML = (String) ctx.getBean(PATHTORESULTXML);
        pathToLog = (String) ctx.getBean(PATHTOLOG);

        if (!((pathToLog == null) || (pathToLog.equals("")))) {
            PropertyConfigurator.configure(pathToLog);
        }

    }

    @Override
    public boolean process(OutputStream out) {

        String errorMessage = "";

        Path pathToTemplate = pathsContainer.getPathToTemplate().get(idletter);
        Path pathToSrcXML = pathsContainer.getPathToSrcXML().get(idletter);

        InputStream templateFile;

        try {
            templateFile = new FileInputStream(pathToTemplate.toFile());
        } catch (FileNotFoundException ex) {
            errorMessage = pathToTemplate.toString();
            log.error(ex);
            try {
                templateFile = new FileInputStream(pathsContainer.getPathToTemplate().get("document404").toFile());
            } catch (FileNotFoundException ex1) {
                log.error(ex1);
                return false;
            }
        }

        
         String idappli = null;
         if ((params!=null)&&(params.get("idappli")!=null)){
          idappli = params.get("idappli").toString();
          }
 
         // вычисление idappli если задан extidappli
         if ((params!=null)&&(params.get("extidappli")!=null)){
                idappli =  org.eapo.corresp.util.IdappliGetter.getIdappli(params,xmlDocumentCreator.getJdbcTemplateNamed()) ;
            if (idappli!=null)
                params.put("idappli", idappli);
        }
       
        
        xmlDocumentCreator.setParams(params);
        Document doc;
        try {

            if (pathToSrcXML.toFile().getName().equals("default.xml")) {

                ListOfQueryGetter queryGetter = new DefaultListOfQueryGetter();
                List queryNames = queryGetter.get(pathToTemplate);
                DocumentWithQuerysCreator dwc = new DocumentWithQuerysCreator();
                Document docForParse = dwc.getDocument(queryNames, pathToSrcXML.toString());
                doc = xmlDocumentCreator.createDocumentFromXML(docForParse, "root");
            } else {
                doc = xmlDocumentCreator.createDocumentFromXML(pathToSrcXML.toString(), "root");
            }

            if (!"".equals(pathToResultXML)) {
                Path absPathXML = PathGetter.getPathToWorkDir().resolve(Paths.get(pathToResultXML).resolve(Paths.get(idappli + "_" + idletter + System.currentTimeMillis() + ".xml")));

                XmlSaver.saveXmlDocument(doc, absPathXML.toString());
            }
        } catch (Exception ex) {

            try {
                templateFile = new FileInputStream(pathsContainer.getPathToTemplate().get("error").toFile());
            } catch (FileNotFoundException ex1) {
                log.error(ex1);
                return false;
            }
            initContext(templateFile);
            context.put(ERRORMESSAGE, ex.getClass().toString());

            StackTraceElement[] se = ex.getStackTrace();
          //  String str = "";
            //  for (StackTraceElement s:se){
            //  str+=s;
            //  }
            context.put(ERRORTEXT, ex.toString());

            try {
                report.process(context, out);
            } catch (XDocReportException | IOException ex1) {
                log.error(ex1);
            }
            return false;
        }

        initContext(templateFile);
        context.put(ERRORMESSAGE, errorMessage);
        
        

        NodeList list = doc.getDocumentElement().getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);

            context.put(node.getNodeName().trim(), node);
            System.out.println("====================");
            System.out.println(node.getNodeName() + ": \t" + node);
            /**
             * *
             */
        }
        
        
        
        

        if (additionalNode != null) {

            context.put(additionalNode.getNodeName().trim(), additionalNode);

        }
        try {
            
//            CharConverter converter = new SpecialCharConverter();
//            converter.convert(doc.getDocumentElement());   // КОСТЫЛЬ 
//            FieldsMetadata metadata = report.createFieldsMetadata();
            String res_str = "";
            Map map = context.getContextMap();
            System.out.println("MAP");

            Node parent = (Node) map.get("ptappli");

            if (parent != null) {

                for (Node child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
                    if ("title".equals(child.getNodeName())) {
                     System.out.println("title = " + child.getTextContent());
                     System.out.println("ParentName = " + parent.getNodeName());
                     System.out.println("ChildName = " + child.getNodeName());
                     res_str = child.getTextContent();
                    }
                }
            }


            FieldsMetadata metadata = report.createFieldsMetadata();
            // КОСТЫЛЬ 
//            ElementImpl idappli_title = (ElementImpl) context.getContextMap().get("ptappli:title");
//             metadata.addFieldAsTextStyling(idappli_title.getLocalName(), SyntaxKind.Html);
//             metadata.addFieldAsTextStyling("ptappli.title", SyntaxKind.Html);
             metadata.addFieldAsTextStyling("myfield", SyntaxKind.Html);     
             //////////////////////////////////////////////////    
             // Регулярное выражение для поиска вхождения тегов.
             String pattern = "</?[A-z]+>+";
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(res_str);
            while (m.find()) {
                res_str = res_str.replace(res_str.substring(m.start(), m.end()), res_str.substring(m.start(), m.end()).toLowerCase());
                // System.out.println(str + " | ");
            }
             /////////////////////////////////////////////
             context.put("myfield", res_str);
             
//             context.put("ptappli.title", "1<sup>1</sup>1");
             System.out.println(context.get("ptappli")==null?"null":context.get("ptappli").getClass());
             System.out.println("!!!!!!!!!");
//             System.out.print(idappli_title);
             System.out.println("!!!!!!!!!");
              
              
            report.process(context, out);
        } catch (XDocReportException | IOException ex) {
            log.error(ex);

            try {
                templateFile = new FileInputStream(pathsContainer.getPathToTemplate().get("error").toFile());
            } catch (FileNotFoundException ex1) {
                log.error(ex1);
                return false;
            }
            initContext(templateFile);
            context.put(ERRORMESSAGE, ex.getClass().toString());
            context.put(ERRORTEXT, ex.toString());

            try {

                report.process(context, out);
            } catch (XDocReportException | IOException ex1) {
                log.error(ex1);

            }
            return false;
        }
        return true;
    }

    private void initContext(InputStream in) {
        try {
            report = XDocReportRegistry.getRegistry().loadReport(in, TemplateEngineKind.Freemarker);
            context = report.createContext();
        } catch (IOException | XDocReportException ex) {
            log.error(ex);
        }
    }

    @Override
    public String getIdletter() {
        return idletter;
    }

    @Override
    public void setIdletter(String idletter) {
        this.idletter = idletter;
    }

 
   
    /**
     * Параметры, передаваемые в SQL
     *
     * @return
     */
    @Override
    public Map<String, Object> getParams() {
        return params;
    }

    @Override
    public void setParams(Map<String, Object> params) {

      //     System.out.println("extidapplis222 = " + params.get("extidapplis"));
        this.params = params;
    }

    /* Получение путей */
    public PathsContainer getPathsContainer() {
        return pathsContainer;
    }

    public void setPathsContainer(PathsContainer pathsContainer) {
        this.pathsContainer = pathsContainer;
    }

    public String getPathToResultXML() {
        return pathToResultXML;
    }

    public void setPathToResultXML(String pathToResultXML) {
        this.pathToResultXML = pathToResultXML;
    }

    @Override
    public void addAdditionalNode(Node node) {
        this.additionalNode = node;
    }

}
