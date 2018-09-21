package org.eapo.docbuilder.builder;

import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import org.eapo.corresp.elementcreator.DefaultElementCreatorSelector;
import org.eapo.corresp.util.IdappliGetter;
import org.eapo.corresp.util.XmlSaver;
import org.eapo.corresp.xdocument.DefaultListOfQueryGetter;
import org.eapo.corresp.xdocument.DocumentWithQuerysCreator;
import org.eapo.corresp.xdocument.ListOfQueryGetter;
import org.eapo.corresp.xmlcreator.XmlDocumentCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DocBuilderService {

    @Value("${docbuilder.templatedir}")
    private String templateDir;

    @Value("${docbuilder.template}")
    private String template;

    @Value("${docbuilder.resultdir}")
    private String resultDir;

    @Value("${docbuilder.extidappli:}")
    private String extidappli;

    @Value("${docbuilder.sqlpath}")
    private String sqlpath;

    @Value("${docbuilder.params}")
    private String paramString;

    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public void setJdbcTemplate(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final Logger log = LoggerFactory.getLogger(DocBuilderService.class);

    public void process() {

        log.info("Начинаю генерацию документа - ");

        Map<String, Object> params = params(paramString);
        if (extidappli.length() > 0) {

            log.info("Обрабатываю заявку c номером {}", extidappli);
            params.put("extidappli", extidappli);
            String idappli = IdappliGetter.getIdappli(params, jdbcTemplate);
            log.info("Внутренний номер заявки  {}", idappli);
            if (idappli != null) {
                params.put("idappli", idappli);
            } else {
                log.error("Для заявки {} не смог вытащить внутренний номер -> заканчитвю работу ", extidappli);
                return;
            }
        }else {
            log.info("Номер заявки не задан, пробую сгенерить письмо без номера заявки");
        }


        Path pathToTemplate = Paths.get(templateDir, template).normalize();
        log.info("Путь к шаблону документа {}", pathToTemplate.toAbsolutePath());

        Path pathToSrcXML = Paths.get(sqlpath).normalize();
        log.info("Путь к файлу с sql-запросами {}", pathToSrcXML.toAbsolutePath());

        InputStream templateFile;
        try {
            templateFile = new FileInputStream(pathToTemplate.toFile());
        } catch (FileNotFoundException ex) {
            log.error("Не удалось открыть файл с именем {}  заканчиваю работу ", pathToTemplate);
            log.error(ex.getMessage());
            return;
        }

        XmlDocumentCreator xmlDocumentCreator = new XmlDocumentCreator();
        xmlDocumentCreator.setJdbcTemplateNamed(jdbcTemplate);
        xmlDocumentCreator.setElementCreatorSelector(new DefaultElementCreatorSelector());

        log.info("Параметры передаваемые в SQl-запросы : {}", params);
        xmlDocumentCreator.setParams(params);


        IXDocReport report;
        IContext context;
        try {
            report = XDocReportRegistry.getRegistry().loadReport(templateFile, TemplateEngineKind.Freemarker);
            context = report.createContext();
        } catch (Exception e) {
            log.error("Ошибка открытия файла с шаблоном {} заканчиваю работу", e.getMessage());
            return;
        }

        List<String> queryNames;
        try {
            ListOfQueryGetter queryGetter = new DefaultListOfQueryGetter();
            queryNames = queryGetter.get(pathToTemplate);
        } catch (Exception e) {
            log.error("Ошибка в процессе извлечения полей из шаблонов {} заканчиваю работу", e.getMessage());
            return;
        }

        Document doc;
        try {
            DocumentWithQuerysCreator dwc = new DocumentWithQuerysCreator();
            Document docForParse = dwc.getDocument(queryNames, pathToSrcXML.toString());
            doc = xmlDocumentCreator.createDocumentFromXML(docForParse, "root");
        } catch (SQLException | BadSqlGrammarException e) {
            log.error("Ошибка выполнения запроса к БД : {}", e.getMessage());
            return;
        } catch (Exception e) {
            log.error("Неопознанная ошибка с сообщением: {}", e.getMessage());
            return;
        }

        Path absPathXML = Paths.get(resultDir, System.currentTimeMillis() + ".xml").normalize();
        log.info("Сохраняю выгруженые из БД данные в файл: {}", absPathXML.toAbsolutePath());

        try {
            XmlSaver.saveXmlDocument(doc, absPathXML.toString());
        } catch (Exception e){
            log.warn("Не смог сохранить в файл {} xml c выгруженным из БД ", absPathXML.toAbsolutePath());
        }

        NodeList list = doc.getDocumentElement().getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            context.put(node.getNodeName().trim(), node);
        }

        OutputStream out;
        Path resultPath = Paths.get(resultDir, System.currentTimeMillis() + "-" + template).normalize();
        log.info("Готовлюсь сохранить результат в файл {}" + resultPath);
        try {
            out = new FileOutputStream(resultPath.toFile());
        } catch (Exception exc) {
            log.error("Не могу создать файл в который хочу сохранить документ {}" + resultPath);
            log.error(exc.getMessage());
            return;
        }

        try {
            report.process(context, out);
        } catch (XDocReportException e) {
            e.printStackTrace();
        } catch (IOException e) {
            log.error("Ошибка генерации документа {} ", e.getMessage());
        }
    }

    private Map<String, Object> params(String params) {
        Map<String, Object> map = new HashMap<>();
        if (params.length()<2)
            return map;
        Arrays.stream(params.split(";")).forEach(x -> toMap(x, map));
        return map;
    }

    private void toMap(String param, Map<String, Object> map) {
        String[] arr = param.split("=");
        map.put(arr[0], arr[1]);
    }

}
