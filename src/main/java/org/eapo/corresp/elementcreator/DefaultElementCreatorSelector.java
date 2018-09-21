package org.eapo.corresp.elementcreator;

/**
 * Реализация выбора класса создающего xml-элемент
 * @author Alexandr Stal astal@eapo.org; al_stal@mail.ru
 */
public class DefaultElementCreatorSelector implements ElementCreatorSelector {

    private final String QUERYTAG = "query";
    private final String XMLTAG = "xmlquery";
    private final String CLASSTAG = "customclass";

    @Override
    public AbstractElementCreator select(String elementName) {

        AbstractElementCreator creator = null;
        if (elementName.equalsIgnoreCase(QUERYTAG)) {
            creator = new QueryElementCreator();
        } else if (elementName.equalsIgnoreCase(XMLTAG)) {
            creator = new XmlElementCreator();
        } else if (elementName.equalsIgnoreCase(CLASSTAG)) {
            creator = new CustomElementCreator();
        }

        return creator;
    }

}
