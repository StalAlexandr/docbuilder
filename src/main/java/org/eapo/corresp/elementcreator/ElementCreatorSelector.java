package org.eapo.corresp.elementcreator;

/**
 * @author astal Интерфейс выбора класса, создающего элемент результирующего xml
 */
public interface ElementCreatorSelector {

    /**
     * @param elementName имя исходного xml-элемента
     * @return
     */
    AbstractElementCreator select(String elementName);

}
