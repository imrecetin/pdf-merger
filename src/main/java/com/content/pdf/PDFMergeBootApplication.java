package com.content.pdf;

import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;

import com.content.pdf.storage.StorageService;

@SpringBootApplication
public class PDFMergeBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(PDFMergeBootApplication.class, args);
	}
	
	@Bean
    CommandLineRunner init(StorageService storageService) {
        return (args) -> { storageService.init(); };
    }
	
	@Bean
    public TomcatServletWebServerFactory tomcatEmbedded() {

		TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();

        tomcat.addConnectorCustomizers((TomcatConnectorCustomizer) connector -> {
            if ((connector.getProtocolHandler() instanceof AbstractHttp11Protocol<?>)) {
                //-1 means unlimited
                ((AbstractHttp11Protocol<?>) connector.getProtocolHandler()).setMaxSwallowSize(-1);
            }
        });

        return tomcat;

    }

}
