package it.finanze.sanita.fse2.ms.edssrvdataprocessor.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 *  Microservices URL.
 */
@Configuration
@Getter
public class MicroservicesURLCFG {

	@Value("${ms.url.eds-srv-data-quality.host}")
	private String edsDataQualityHost;

	@Value("${ms.url.eds-srv-query.host}")
	private String edsQueryHost;
}
