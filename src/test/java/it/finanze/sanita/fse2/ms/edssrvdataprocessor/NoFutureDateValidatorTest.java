package it.finanze.sanita.fse2.ms.edssrvdataprocessor;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.Constants;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.validators.impl.NoFutureDateValidator;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles(Constants.Profile.TEST)
class NoFutureDateValidatorTest {

    @Autowired
    private NoFutureDateValidator validator;

    @Test
    public void testNullValueIsValid() {
        assertTrue(validator.isValid(null, null));
    }
    
    @Test
    public void testPastDateIsValid() {
        // Create a past date (e.g., 2 days ago)
        Date pastDate = new Date(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000);
        assertTrue(validator.isValid(pastDate, null));
    }

    @Test
    public void testFutureDateIsNotValid() {
        // Create a future date (e.g., 2 days from now)
        Date futureDate = new Date(System.currentTimeMillis() + 2 * 24 * 60 * 60 * 1000);
        assertFalse(validator.isValid(futureDate, null));
    }

}
