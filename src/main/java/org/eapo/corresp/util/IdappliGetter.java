package org.eapo.corresp.util;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Map;

/**
 * @author stal
 */
public class IdappliGetter {

    public static String getIdappli(Map params, NamedParameterJdbcTemplate jdbc) {

        try {
            String res = jdbc.queryForObject("select idappli from ptappli where extidappli =:extidappli", params, String.class);
            return res;
        } catch (DataAccessException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }
}
