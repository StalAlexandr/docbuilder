package org.eapo.corresp.pathgetter;

/**
 * @author Alexandr Stal astal@eapo.org; al_stal@mail.ru
 */
public class PathsContainer {

    private PathGetter pathToSrcXML;
    private PathGetter pathToTemplate;

    protected static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger("org.eapo.corresp");

    public PathGetter getPathToSrcXML() {
        return pathToSrcXML;
    }

    public void setPathToSrcXML(PathGetter pathToSrcXML) {
        this.pathToSrcXML = pathToSrcXML;
    }

    public PathGetter getPathToTemplate() {
        return pathToTemplate;
    }

    public void setPathToTemplate(PathGetter pathToTemplate) {
        this.pathToTemplate = pathToTemplate;
    }

}
