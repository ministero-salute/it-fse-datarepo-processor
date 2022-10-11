package it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.kafka;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.Constants;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility.ProfileUtility;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 *	@author vincenzoingenito
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

	@PostConstruct
	public void afterInit() {
		if (profileUtility.isTestProfile()) {
			this.ingestorPublishLowPriorityTopic = Constants.Profile.TEST_PREFIX + this.ingestorPublishLowPriorityTopic;
			this.ingestorPublishMediumPriorityTopic = Constants.Profile.TEST_PREFIX + this.ingestorPublishMediumPriorityTopic;
			this.ingestorPublishHighPriorityTopic = Constants.Profile.TEST_PREFIX + this.ingestorPublishHighPriorityTopic;
			this.ingestorGenericTopic = Constants.Profile.TEST_PREFIX + this.ingestorGenericTopic;
			this.ingestorPublishDeadLetterTopic = Constants.Profile.TEST_PREFIX + this.ingestorPublishDeadLetterTopic;
		}
	}

}
