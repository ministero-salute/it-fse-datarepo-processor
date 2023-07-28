package it.finanze.sanita.fse2.ms.edssrvdataprocessor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.Constants;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.EventStatusEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.BlockingException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.ConnectionRefusedException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.DocumentAlreadyExistsException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.EmptyIdentifierException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.NoRecordFoundException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.OutOfRangeException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.UATMockException;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles(Constants.Profile.TEST)
class ExceptionTest {
    
    @Test
	void businessExceptionTest() {
		BusinessException exc = new BusinessException("Error"); 
		BusinessException exc2 = new BusinessException("Error2", new RuntimeException());
		
		assertEquals(BusinessException.class, exc.getClass()); 
		assertEquals("Error", exc.getMessage()); 
		assertEquals(BusinessException.class, exc2.getClass()); 
		assertEquals("Error2", exc2.getMessage()); 
	}
	
	
	@Test
	void businessExceptionTestWithoutMsg() {
		BusinessException exc = new BusinessException(new RuntimeException()); 
		
		assertEquals(BusinessException.class, exc.getClass()); 
	}

	@Test
	void blockingExceptionTest() {
		BlockingException exc = new BlockingException("Error1", new RuntimeException());
		BlockingException excOnlyMsg = new BlockingException("Error2");
		
		assertEquals(BlockingException.class, exc.getClass()); 
		assertEquals("Error1", exc.getMessage());
		assertEquals(BlockingException.class, excOnlyMsg.getClass()); 
		assertEquals("Error2", excOnlyMsg.getMessage());
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
    
	@Test
	void UATMockExceptionTest() {
		UATMockException exc = new UATMockException(EventStatusEnum.BLOCKING_ERROR, "Error"); 
		
		assertEquals(UATMockException.class, exc.getClass());
		assertEquals(EventStatusEnum.BLOCKING_ERROR, exc.getStatus());
        assertEquals("Error", exc.getMessage());
	}

}
