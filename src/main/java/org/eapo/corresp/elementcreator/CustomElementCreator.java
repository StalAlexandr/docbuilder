package org.eapo.corresp.elementcreator;

import org.w3c.dom.Element;

/**
 * Передает создание xml-элемента другой реализации AbstractElementCreator
 * @author astal
 */
public class CustomElementCreator extends AbstractElementCreator {

    /**
     * Реализация создания xml-элемента путем передачи в другую реализацию
     * @param inElement тело элемента должно содержать имя другой реализации
     * AbstractElementCreator берущей на себя создание xml-элемента
     * @return
     * @throws Exception
     */
    @Override
    public Element createElement(Element inElement) throws Exception {

        String className = inElement.getFirstChild().getNodeValue().trim().replaceAll(System.getProperty("line.separator"), "");

        AbstractElementCreator creator = null;
        try {
            try {
                creator = (AbstractElementCreator) Class.forName(className).newInstance();
            } catch (InstantiationException | IllegalAccessException ex) {
                log.error(ex);
                throw ex;
            }
        } catch (ClassNotFoundException ex) {
            log.error(ex);
            throw ex;
        }
        creator.setJdbcTemplateNamed(jdbcTemplateNamed);
        creator.setParams(params);
        return creator.createElement(inElement);
    }

}
