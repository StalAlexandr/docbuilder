package org.eapo.corresp.xdocument;

import java.nio.file.Path;
import java.util.List;

/**
 * Интерфейс для извлечения запросов
 * @author astal
 */
public interface ListOfQueryGetter {
  List get(Path pathToTemplate);
}
