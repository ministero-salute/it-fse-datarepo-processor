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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.Constants;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.AccreditamentoPrefixEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.EventStatusEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.EventTypeEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.OperationLogEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.ResultLogEnum;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(Constants.Profile.TEST)
class EnumsTest {

    @Test
    @DisplayName("EventStatusEnum test")
    void testEventStatusEnums() {
        String name = "SUCCESS";
        assertEquals(name, EventStatusEnum.SUCCESS.getName());
    }

    @Test
    @DisplayName("ResultLogEnum test")
    void testResultLogEnums() {
        String code = "OK";
        String description = "Operazione eseguita con successo";
        assertEquals(code, ResultLogEnum.OK.getCode());
        assertEquals(description, ResultLogEnum.OK.getDescription());
    }

    @Test
    @DisplayName("EventTypeEnum test")
    void testEventTypeEnums() {
        String edsWorkflow = "EDS_WORKFLOW";
        String deserialize = "DESERIALIZE";
        assertEquals(edsWorkflow, EventTypeEnum.EDS_WORKFLOW.getName());
        assertEquals(deserialize, EventTypeEnum.DESERIALIZE.getName());
    }

    @Test
    @DisplayName("OperationLogEnum test")
    void testOperationLogEnums() {
        String code = "FHIR-PUBLISH";
        String description = "Pubblicazione su server FHIR";
        assertEquals(description, OperationLogEnum.FHIR_PUBLISH.getDescription());
        assertEquals(code, OperationLogEnum.FHIR_PUBLISH.getCode());
    }

    @Test
    @DisplayName("AccreditamentoPrefixEnum test")
    void testAccreditamentoPrefixEnum() {
        String prefix = "CRASH_WF_EDS";
        assertEquals(prefix, AccreditamentoPrefixEnum.CRASH_WF_EDS.getPrefix());
        assertEquals(AccreditamentoPrefixEnum.CRASH_WF_EDS, AccreditamentoPrefixEnum.get(prefix));
        assertEquals(AccreditamentoPrefixEnum.CRASH_WF_EDS, AccreditamentoPrefixEnum.getStartWith(prefix));
    }
    
}
