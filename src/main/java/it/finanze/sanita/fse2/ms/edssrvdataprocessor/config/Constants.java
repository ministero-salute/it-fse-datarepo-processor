/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.config;

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
		 * Configuration mongo path.
		 */
		public static final String CONFIG_MONGO = "it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.mongo";

		public static final String REPOSITORY = "it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository";
		public static final String UTILITY = "it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility";

		/**
		 * This method is intentionally left blank 
		 */
		private ComponentScan() {}

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
		 * Test profile
		 */
		public static final String TEST = "test";

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
		public static final String MISSING_WORKFLOW_PLACEHOLDER = "UNKNOWN_WORKFLOW_ID";

		/** 
		 * This method is intentionally left blank. 
		 */
		private App() {

		}
	}

	/**
	 * Constants used in logging. 
	 *
	 */	
	public static final class Logs {
		public static final String ERR_VAL_PAGE_NOT_EXISTS = "La pagina richiesta non esiste, range valido da <%d> a <%d>";
		public static final String ERR_VAL_PAGE_IDX_LESS_ZERO = "L'indice pagina non può essere minore di zero";
		public static final String ERR_VAL_PAGE_LIMIT_LESS_ZERO = "Il limite pagina non può essere minore o uguale a zero";

		public static final String ERR_VAL_FUTURE_DATE = "La data di aggiornamento non può essere nel futuro";
		public static final String ERR_VAL_UNABLE_CONVERT = "Impossibile convertire %s al tipo %s";
		public static final String ERR_REP_DOCS_NOT_FOUND = "Impossibile recuperare i documenti richiesti";
		public static final String ERR_REP_DEL_DOCS = "Impossibile cancellare i documenti richiesti";

		/**
		 * When there is an error retrieving the host info 
		 */
		public static final String ERROR_RETRIEVING_HOST_INFO = "Error while retrieving host informations";

		/**
		 * When there is an error inserting a document on MongoDB 
		 */
		public static final String ERROR_MONGO_INSERT = "MongoDB: Error while inserting document";

		/**
		 * When there is an error inserting a document on MongoDB
		 */
		public static final String ERROR_MONGO_FIND_BY_ID = "MongoDB: Error while find document by id";

		/**
		 * When a document is not found on MongoDB 
		 */
		public static final String ERROR_DOCUMENT_NOT_FOUND = "Error: document not found on staging database";

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

	/**
	 * Collections
	 */
	public static final class Collections {

		/**
		 * Schema collection name
		 */
		public static final String DOCUMENT_REFERENCE = "ingestion-staging";

		/**
		 * Successful transaction data
		 */
		public static final String TRANSACTION_STATUS = "transaction-status";

		/**
		 * Private constructor to disallow to access from other classes
		 */
		private Collections() {}
	}

}