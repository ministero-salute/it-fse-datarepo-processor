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
