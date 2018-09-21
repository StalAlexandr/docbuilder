package org.eapo.docbuilder;

import org.eapo.docbuilder.builder.DocBuilderService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.HashMap;
import java.util.List;


@SpringBootApplication
public class DocbuilderApplication {

	public static void main(String[] args) {
		ApplicationContext context  = SpringApplication.run(AppConfig.class, args);
		DocBuilderService service = context.getBean(DocBuilderService.class);
	    service.process();
	    System.exit(0);
	}



}
