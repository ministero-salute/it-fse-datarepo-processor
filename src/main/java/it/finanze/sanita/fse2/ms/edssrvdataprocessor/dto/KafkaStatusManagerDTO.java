/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.EventStatusEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.EventTypeEnum;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class KafkaStatusManagerDTO {

	private EventTypeEnum eventType;
	
	private Date eventDate;
	
	private EventStatusEnum eventStatus;
	
	private String message;
	
	private String microserviceName;
	
	private String extra;
}
 