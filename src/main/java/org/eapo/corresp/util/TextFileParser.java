package org.eapo.corresp.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Извлекает строку из текстового файла
 * @author astal
 */
public class TextFileParser {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger("org.eapo.corresp");

    static public String getQueryFromFile(String pathToQueryFile) throws IOException {

        StringBuilder qwery = new StringBuilder("");
        String line;
        try (BufferedReader reader = new BufferedReader(new FileReader(pathToQueryFile))) {
            while ((line = reader.readLine()) != null) {
                qwery.append(line).append(" ");
            }

        }
        return qwery.toString();
    }
}
