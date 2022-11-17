/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * SpringBoot Application 
 *
 */
@SpringBootApplication
public class EdsSrvDataprocessorApplication {

	/** 
	 * Main Function 
	 * @param args  The args for the main function 
	 */
	public static void main(String[] args) {
		SpringApplication.run(EdsSrvDataprocessorApplication.class, args);
	} 
	

   /**
     * Definizione rest template.
     * 
     * @return	rest template
    */
    @Bean 
    @Qualifier("restTemplate")
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
