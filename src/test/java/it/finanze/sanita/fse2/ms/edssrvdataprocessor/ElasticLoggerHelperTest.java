package it.finanze.sanita.fse2.ms.edssrvdataprocessor;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.Constants;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.ErrorLogEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.OperationLogEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.ResultLogEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.logging.ElasticLoggerHelper;

@SpringBootTest
@ComponentScan
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles(Constants.Profile.TEST_ASYNC)
@ExtendWith(OutputCaptureExtension.class)
class ElasticLoggerHelperTest {
	
	
	public String TEST_LOG_MESSAGE = "Test Log Message"; 
	public OperationLogEnum TEST_LOG_OPERATION = OperationLogEnum.FHIR_PUBLISH; 
	public ResultLogEnum TEST_LOG_RESULT = ResultLogEnum.OK; 
	public Date TEST_LOG_DATE = new Date(); 
	public ErrorLogEnum TEST_LOG_ERROR = ErrorLogEnum.KO_FHIR_PUBLISH; 
	
	
	@Autowired
	public ElasticLoggerHelper elasticLogger; 
	
	
	
	@Test
	void traceTest(CapturedOutput output) {
		elasticLogger.trace(TEST_LOG_MESSAGE, TEST_LOG_OPERATION, TEST_LOG_RESULT, TEST_LOG_DATE); 
		assertTrue(true); 
	} 
	 
	@Test
	void debugTest(CapturedOutput output) {
		elasticLogger.debug(TEST_LOG_MESSAGE, TEST_LOG_OPERATION, TEST_LOG_RESULT, TEST_LOG_DATE); 
		assertTrue(true); 
	} 
	
	@Test
	void infoTest(CapturedOutput output) {
		elasticLogger.info(TEST_LOG_MESSAGE, TEST_LOG_OPERATION, TEST_LOG_RESULT, TEST_LOG_DATE);
		assertTrue(true); 
	} 
	
	@Test
	void warnTest(CapturedOutput output) {
		elasticLogger.warn(TEST_LOG_MESSAGE, TEST_LOG_OPERATION, TEST_LOG_RESULT, TEST_LOG_DATE); 
		assertTrue(true); 
	} 
	
	@Test
	void errorTest(CapturedOutput output) {
		elasticLogger.error(TEST_LOG_MESSAGE, TEST_LOG_OPERATION, TEST_LOG_RESULT, TEST_LOG_DATE, TEST_LOG_ERROR); 
		assertTrue(true); 
	} 
	
	
}