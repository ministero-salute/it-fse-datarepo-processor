package it.finanze.sanita.fse2.ms.edssrvdataprocessor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.Constants;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.FhirAdvicesCFG;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles(Constants.Profile.TEST)
class FhirAdvicesTest {
	
	@Autowired
	private FhirAdvicesCFG advices;
	
	@Test
	void getAdvice() {
		Optional<String> res = advices.get("HAPI-0545");
		assertTrue(res.isPresent());
	}
	
	@Test
	void extractAdvice() {
		Optional<String> res = advices.exists("HTTP 400 : HAPI-0545: Unable to perform PUT, no URL provided.");
		assertTrue(res.isPresent());
		assertEquals("HAPI-0545", res.get());
	}
}
