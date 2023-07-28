package it.finanze.sanita.fse2.ms.edssrvdataprocessor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.Constants;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.ConnectionRefusedException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.DocumentAlreadyExistsException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.EmptyIdentifierException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.NoRecordFoundException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.OutOfRangeException;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles(Constants.Profile.TEST)
class ExceptionTest {
    
    @Test
	void businessExceptionTest() {
		BusinessException exc = new BusinessException("Error"); 
		
		assertEquals(BusinessException.class, exc.getClass()); 
		assertEquals("Error", exc.getMessage()); 
	} 
	
	@Test
	void businessExceptionTestWithoutMsg() {
		BusinessException exc = new BusinessException(new RuntimeException()); 
		
		assertEquals(BusinessException.class, exc.getClass()); 
	}
	
	@Test
	void connectionRefusedExceptionTest() {
		String url = "testUrl";
		ConnectionRefusedException exc = new ConnectionRefusedException(url, "message"); 
		
		assertEquals(ConnectionRefusedException.class, exc.getClass());
		assertEquals(url, exc.getUrl());
	}
	
    @Test
	void documentAlreadyExistsExceptionTest() {
		DocumentAlreadyExistsException exc = new DocumentAlreadyExistsException("Error"); 
		
		assertEquals(DocumentAlreadyExistsException.class, exc.getClass());
		assertEquals("Error", exc.getMessage()); 
	}

    @Test
	void emptyIdentifierExceptionTest() {
		EmptyIdentifierException exc = new EmptyIdentifierException("Error"); 
		
		assertEquals(EmptyIdentifierException.class, exc.getClass());
		assertEquals("Error", exc.getMessage()); 
	}

    @Test
	void noRecordFoundExceptionTest() {
		NoRecordFoundException exc = new NoRecordFoundException("Error"); 
		
		assertEquals(NoRecordFoundException.class, exc.getClass());
		assertEquals("Error", exc.getMessage()); 
	}

    @Test
	void outOfRangeExceptionTest() {
		OutOfRangeException exc = new OutOfRangeException("Error", "Field"); 
		
		assertEquals(OutOfRangeException.class, exc.getClass());
		assertEquals("Error", exc.getMessage());
        assertEquals("Field", exc.getField());
	}
    
}
