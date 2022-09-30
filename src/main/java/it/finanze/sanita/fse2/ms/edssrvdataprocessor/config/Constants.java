package it.finanze.sanita.fse2.ms.edssrvdataprocessor.config;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.ErrorLogEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.OperationLogEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.ProcessorOperationEnum;

import java.util.EnumMap;
import java.util.Map;

/**
 * 
 * @author vincenzoingenito
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
		public static final String BASE = "it.sanita.edssrvdataprocessor";

		/**
		 * Controller path.
		 */
		public static final String CONTROLLER = "it.sanita.edssrvdataprocessor.controller";

		/**
		 * Service path.
		 */
		public static final String SERVICE = "it.sanita.edssrvdataprocessor.service";

		/**
		 * Configuration path.
		 */
		public static final String CONFIG = "it.sanita.edssrvdataprocessor.config";
		
		/**
		 * Configuration mongo path.
		 */
		public static final String CONFIG_MONGO = "it.sanita.edssrvdataprocessor.config.mongo";
		
		/**
		 * Configuration mongo repository path.
		 */

		public static final String INGESTION_STAGING = "ingestion-staging";

		private ComponentScan() {
			//This method is intentionally left blank.
		}

	}
 
	public static final class Profile {
		
		public static final String TEST_SYNC = "test-sync";
		
		public static final String TEST_ASYNC = "test-async";

		public static final String DEV = "dev";

		public static final String TEST_PREFIX = "test_";


		/** 
		 * Constructor.
		 */
		private Profile() {
			//This method is intentionally left blank.
		}

	}

	public static final class App {
		
		public static final String IDENTIFIER = "identifier";

		private App() {
			//This method is intentionally left blank.
		}
	}

	public static final class AppConstants {

		private AppConstants() {
		}

		public static final Map<ProcessorOperationEnum, OperationLogEnum> logMap = new EnumMap<>(ProcessorOperationEnum.class);
			static {
				logMap.put(ProcessorOperationEnum.PUBLISH, OperationLogEnum.FHIR_PUBLISH);
				logMap.put(ProcessorOperationEnum.REPLACE, OperationLogEnum.FHIR_REPLACE);
				logMap.put(ProcessorOperationEnum.UPDATE, OperationLogEnum.FHIR_UPDATE);
				logMap.put(ProcessorOperationEnum.DELETE, OperationLogEnum.FHIR_DELETE);
			}

		public static final Map<ProcessorOperationEnum, ErrorLogEnum> logErrorMap = new EnumMap<>(ProcessorOperationEnum.class);
			static {
				logErrorMap.put(ProcessorOperationEnum.PUBLISH, ErrorLogEnum.KO_FHIR_PUBLISH);
				logErrorMap.put(ProcessorOperationEnum.REPLACE, ErrorLogEnum.KO_FHIR_REPLACE);
				logErrorMap.put(ProcessorOperationEnum.UPDATE, ErrorLogEnum.KO_FHIR_UPDATE);
				logErrorMap.put(ProcessorOperationEnum.DELETE, ErrorLogEnum.KO_FHIR_DELETE);
			}
	}

	public static final class Logs {

		public static final String ELASTIC_LOGGER_APP_NAME = "application";

		public static final String ELASTIC_LOGGER_OP_NAME = "operation";

		public static final String ELASTIC_LOGGER_OP_TIMESTAMP = "op-log-timestamp";

		public static final String ELASTIC_LOGGER_OP_RESULT = "op-result";

		public static final String ELASTIC_LOGGER_OP_TIMESTAMP_START = "op-timestamp-start";

		public static final String ELASTIC_LOGGER_OP_TIMESTAMP_END = "op-timestamp-end";

		public static final String ELASTIC_LOGGER_OP_ERROR_CODE = "op-error";

		public static final String ELASTIC_LOGGER_OP_ERROR_DESCRIPTION = "op-error-description";

		public static final String ERROR_RETRIEVING_HOST_INFO = "Error while retrieving host informations";

		public static final String ERROR_MONGO_INSERT = "MongoDB: Error while inserting document";

		public static final String ERROR_DOCUMENT_NOT_FOUND = "Error: document not found on staging database";

		public static final String DOCUMENT_NOT_FOUND_ON_FHIR_SERVER = "Document not found on FHIR server";

		public static final String ERROR_EMPTY_IDENTIFIER = "Error: empty identifier received from kafka";

		public static final String ERROR_HANDLER_GENERIC_EXCEPTION = "HANDLER handleGenericException()";

		public static final String ERROR_HANDLER_UNSUPPORTED_OPERATION_EXCEPTION = "HANDLER handleUnsupportedOperationException()";

        public static final String ERROR_CONNECTION_REFUSED = "Error: Connection refused";

		public static final String SRV_QUERY_RESPONSE = "{} status returned from eds";

        private Logs() {
			//This method is intentionally left blank.
		}

	}
  
	/**
	 *	Constants.
	 */
	private Constants() {

	}

}