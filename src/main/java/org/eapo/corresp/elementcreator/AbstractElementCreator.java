package org.eapo.corresp.elementcreator;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.w3c.dom.Element;

import java.util.Map;

/**
 * Базовый класс для всех создателей XML-элементов на основе исходного
 * @author astal
 */
public abstract class AbstractElementCreator {

    protected static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger("org.eapo.corresp");

    public abstract Element createElement(Element inElement) throws Exception;

    // связь с БД
    protected NamedParameterJdbcTemplate jdbcTemplateNamed;

    // дополнительные параметры
    protected Map<String, Object> params;

    public NamedParameterJdbcTemplate getJdbcTemplateNamed() {
        return jdbcTemplateNamed;
    }

    public void setJdbcTemplateNamed(NamedParameterJdbcTemplate jdbcTemplateNamed) {
        this.jdbcTemplateNamed = jdbcTemplateNamed;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

}
