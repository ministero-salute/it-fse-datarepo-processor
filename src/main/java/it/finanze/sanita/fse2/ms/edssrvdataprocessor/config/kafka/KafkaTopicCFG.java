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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.Constants;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility.ProfileUtility;
import jakarta.annotation.PostConstruct;
import lombok.Data;

/**
 * Kafka topic configuration.
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
     * Ingestor publish Topic.
     */
    @Value("${kafka.ingestion-datarepo.publication.topic}")
    private String ingestorPublishTopic;


    /**
     * Ingestor PUT Operations Topic
     */
    @Value("${kafka.ingestion-datarepo.generic.topic}")
    private String ingestorGenericTopic;

    /**
     * Ingestor publish Dead letter Topic.
     */
    @Value("${kafka.ingestor-publish.deadletter.topic}")
    private String ingestorPublishDeadLetterTopic;

    @Value("${kafka.statusmanager.topic}")
    private String statusManagerTopic;

    @PostConstruct
    public void afterInit() {
        if (profileUtility.isTestProfile()) {
            this.ingestorPublishTopic = Constants.Profile.TEST_PREFIX + this.ingestorPublishTopic;
            this.ingestorGenericTopic = Constants.Profile.TEST_PREFIX + this.ingestorGenericTopic;
            this.ingestorPublishDeadLetterTopic = Constants.Profile.TEST_PREFIX + this.ingestorPublishDeadLetterTopic;
            this.statusManagerTopic = Constants.Profile.TEST_PREFIX + this.statusManagerTopic;
        }
    }

}
