/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 * 
 * Copyright (C) 2023 Ministero della Salute
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.kafka;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.Constants;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility.ProfileUtility;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 *
 *	Kafka topic configuration.
 */
@Data
@Component
public class KafkaTopicCFG {

	/**
	 * Profile Utility 
	 */
	@Autowired
	private ProfileUtility profileUtility;

	/**
	 * Ingestor publish low priority Topic.
	 */
	@Value("${kafka.ingestor-publish.topic.low-priority}")
	private String ingestorPublishLowPriorityTopic;

	/**
	 * Ingestor publish medium priority Topic.
	 */
	@Value("${kafka.ingestor-publish.topic.medium-priority}")
	private String ingestorPublishMediumPriorityTopic;

	/**
	 * Ingestor publish high priority Topic.
	 */
	@Value("${kafka.ingestor-publish.topic.high-priority}")
	private String ingestorPublishHighPriorityTopic;

	/**
	 * Ingestor publish Dead letter Topic.
	 */
	@Value("${kafka.ingestor-publish.deadletter.topic}")
	private String ingestorPublishDeadLetterTopic;

	/**
	 * Ingestor Generic Topic 
	 */
	@Value("${kafka.dataprocessor.generic.topic}")
	private String ingestorGenericTopic;

	@Value("${kafka.statusmanager.topic}")
	private String statusManagerTopic;

	@PostConstruct
	public void afterInit() {
		if (profileUtility.isTestProfile()) {
			this.ingestorPublishLowPriorityTopic = Constants.Profile.TEST_PREFIX + this.ingestorPublishLowPriorityTopic;
			this.ingestorPublishMediumPriorityTopic = Constants.Profile.TEST_PREFIX + this.ingestorPublishMediumPriorityTopic;
			this.ingestorPublishHighPriorityTopic = Constants.Profile.TEST_PREFIX + this.ingestorPublishHighPriorityTopic;
			this.ingestorGenericTopic = Constants.Profile.TEST_PREFIX + this.ingestorGenericTopic;
			this.ingestorPublishDeadLetterTopic = Constants.Profile.TEST_PREFIX + this.ingestorPublishDeadLetterTopic;
			this.statusManagerTopic = Constants.Profile.TEST_PREFIX + this.statusManagerTopic;
		}
	}

}
