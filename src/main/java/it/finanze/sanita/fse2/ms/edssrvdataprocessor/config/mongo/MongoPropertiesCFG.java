/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.mongo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* Mongo properties configuration.
*/
@Data
@Component
@EqualsAndHashCode(callSuper = false)  
public class MongoPropertiesCFG {

	/**
	 * The Mongo DB URI 
	 */
	@Value("${data.mongodb.uri}")
	private String uri;
	
}
