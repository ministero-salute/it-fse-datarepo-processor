/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.config;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.ErrorLogEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.OperationLogEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.ProcessorOperationEnum;

import java.util.EnumMap;
import java.util.Map;

/**
 * 
 *
 * Constants application.
 */
public final class Constants {

	/**
	 *	Path scan.
	 */
	public static final class ComponentScan {

		/**
		 * Base path.
		 */
		public static final String BASE = "it.finanze.sanita.fse2.ms.edssrvdataprocessor";

		/**
		 * Controller path.
		 */
		public static final String CONTROLLER = "it.finanze.sanita.fse2.ms.edssrvdataprocessor.controller";

		/**
		 * Service path.
		 */
		public static final String SERVICE = "it.finanze.sanita.fse2.ms.edssrvdataprocessor.service";

		/**
		 * Configuration path.
		 */
		public static final String CONFIG = "it.finanze.sanita.fse2.ms.edssrvdataprocessor.config";
		
		/**
		 * Configuration mongo path.
		 */
		public static final String CONFIG_MONGO = "it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.mongo";
		
		/**
		 * Configuration mongo repository path.
		 */

		public static final String INGESTION_STAGING = "ingestion-staging";

		/**
		 * This method is intentionally left blank 
		 */
		private ComponentScan() {

		}

	}
 
	/** 
	 * Contains the constants for the launch profile of the SpringBoot application. 
	 * 
	 */
	public static final class Profile {
		
		/**
		 * Test Profile - Sync Flow 
		 */
		public static final String TEST_SYNC = "test-sync";
		
		/**
		 * Test Profile - Async Flow 
		 */
		public static final String TEST_ASYNC = "test-async";

		/**
		 * Dev Profile 
		 */
		public static final String DEV = "dev";

		/**
		 * Test Prefix 
		 */
		public static final String TEST_PREFIX = "test_";


		/** 
		 * Constructor. This method is intentionally left blank. 
		 */
		private Profile() {

		}

	}

	/** 
	 * Generic Constants used in the application. 
	 *
	 */
	public static final class App {
		
		/**
		 * Identifier
		 */
		public static final String IDENTIFIER = "identifier";
		public static final String MISSING_WORKFLOW_PLACEHOLDER = "UNKNOWN_WORKFLOW_ID";

		/** 
		 * This method is intentionally left blank. 
		 */
		private App() {

		}
	}

	public static final class AppConstants {

		/**
		 * This method is intentionally left blank. 
		 */
		private AppConstants() {
			
		}

		/**
		 * Log Map
		 */
		public static final Map<ProcessorOperationEnum, OperationLogEnum> logMap = new EnumMap<>(ProcessorOperationEnum.class);
			static {
				logMap.put(ProcessorOperationEnum.PUBLISH, OperationLogEnum.FHIR_PUBLISH);
				logMap.put(ProcessorOperationEnum.REPLACE, OperationLogEnum.FHIR_REPLACE);
				logMap.put(ProcessorOperationEnum.UPDATE, OperationLogEnum.FHIR_UPDATE);
				logMap.put(ProcessorOperationEnum.DELETE, OperationLogEnum.FHIR_DELETE);
			}

		/**
		 * Log Error Map 
		 */
		public static final Map<ProcessorOperationEnum, ErrorLogEnum> logErrorMap = new EnumMap<>(ProcessorOperationEnum.class);
			static {
				logErrorMap.put(ProcessorOperationEnum.PUBLISH, ErrorLogEnum.KO_FHIR_PUBLISH);
				logErrorMap.put(ProcessorOperationEnum.REPLACE, ErrorLogEnum.KO_FHIR_REPLACE);
				logErrorMap.put(ProcessorOperationEnum.UPDATE, ErrorLogEnum.KO_FHIR_UPDATE);
				logErrorMap.put(ProcessorOperationEnum.DELETE, ErrorLogEnum.KO_FHIR_DELETE);
			}
	}

	/**
	 * Constants used in logging. 
	 *
	 */	
	public static final class Logs {

		/**
		 * App name in Structured Logs 
		 */
		public static final String ELASTIC_LOGGER_APP_NAME = "application";

		/**
		 * Operation Name in Structured Logs 
		 */
		public static final String ELASTIC_LOGGER_OP_NAME = "operation";

		/**
		 * Operation Timestamp in Structured Logs 
		 */
		public static final String ELASTIC_LOGGER_OP_TIMESTAMP = "op-log-timestamp";

		/**
		 * Operation Result in Structured Logs 
		 */
		public static final String ELASTIC_LOGGER_OP_RESULT = "op-result";

		/**
		 * Operation Timestamp Start in Structured Logs 
		 */
		public static final String ELASTIC_LOGGER_OP_TIMESTAMP_START = "op-timestamp-start";

		/**
		 * Operation Timestamp End in Structured Logs 
		 */
		public static final String ELASTIC_LOGGER_OP_TIMESTAMP_END = "op-timestamp-end";

		/**
		 * Operation Error Code in Structured Logs 
		 */
		public static final String ELASTIC_LOGGER_OP_ERROR_CODE = "op-error";

		/**
		 * Operation Error Description in Structured Logs 
		 */
		public static final String ELASTIC_LOGGER_OP_ERROR_DESCRIPTION = "op-error-description";

		/**
		 * When there is an error retrieving the host info 
		 */
		public static final String ERROR_RETRIEVING_HOST_INFO = "Error while retrieving host informations";

		/**
		 * When there is an error inserting a document on MongoDB 
		 */
		public static final String ERROR_MONGO_INSERT = "MongoDB: Error while inserting document";

		/**
		 * When a document is not found on MongoDB 
		 */
		public static final String ERROR_DOCUMENT_NOT_FOUND = "Error: document not found on staging database";

		/**
		 * When the document is not found on FHIR Server 
		 */
		public static final String DOCUMENT_NOT_FOUND_ON_FHIR_SERVER = "Document not found on FHIR server";

		/**
		 * When an empty identifier is received from Kafka 
		 */
		public static final String ERROR_EMPTY_IDENTIFIER = "Error: empty identifier received from kafka";

		/** 
		 * Generic Excpetion Handler 
		 */
		public static final String ERROR_HANDLER_GENERIC_EXCEPTION = "HANDLER handleGenericException()";

		/**
		 * Unsupported Operation Exception Handler 
		 */
		public static final String ERROR_HANDLER_UNSUPPORTED_OPERATION_EXCEPTION = "HANDLER handleUnsupportedOperationException()";

		/**
		 * When a Connection Refused Error occurs 
		 */
        public static final String ERROR_CONNECTION_REFUSED = "Error: Connection refused";

        /**
         * Srv Query Response Log 
         */
		public static final String SRV_QUERY_RESPONSE = "{} status returned from eds";

		/**
		 * This method is intentionally left blank 
		 */
        private Logs() {

        }

	}
  
	/**
	 *	Constants.
	 */
	private Constants() {

	}

}