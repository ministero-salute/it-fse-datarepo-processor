package it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.mongo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
*
*	Mongo properties configuration.
*/
@Data
@Component
@EqualsAndHashCode(callSuper = false)  
public class MongoPropertiesCFG implements Serializable {

	/**
	 * Serial Version UID 
	 */
	private static final long serialVersionUID = 1895975715839605374L; 
	
	
	/**
	 * The Mongo DB URI 
	 */
	@Value("${data.mongodb.uri}")
	private String uri;
	
}
