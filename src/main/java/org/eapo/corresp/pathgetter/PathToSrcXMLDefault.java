package org.eapo.corresp.pathgetter;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Alexandr Stal astal@eapo.org; al_stal@mail.ru
 *         <p/>
 *         Вычисляет путь к xml-нику с sql по idletter
 */
public class PathToSrcXMLDefault extends PathGetter {

    private final String PATHTODEFAULTXML = "default.xml";
    private final String PREFIX = "";

    private String pathToQuerys;

    @Override
    public Path get(String idletter) {

        Path pathQueryXmlFile = Paths.get(PREFIX + idletter + ".xml");
        Path result = pathToWorkDir.resolve(Paths.get(pathToQuerys).resolve(pathQueryXmlFile)); //Paths.get(pathToQuerys).resolve(pathQueryXmlFile);
        File f = result.toFile();

        if (f.exists() && (f.canRead())) {

            return pathToWorkDir.resolve(Paths.get(pathToQuerys).resolve(pathQueryXmlFile));
        }
        return pathToWorkDir.resolve(Paths.get(pathToQuerys).resolve(PATHTODEFAULTXML));
    }

    public void setPathToQuerys(String pathToQuerys) {
        this.pathToQuerys = pathToQuerys;
    }

    public String getPathToQuerys() {
        return pathToQuerys;
    }

}
