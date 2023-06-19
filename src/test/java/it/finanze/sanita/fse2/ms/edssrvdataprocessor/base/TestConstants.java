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
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.base;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.PriorityTypeEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.ProcessorOperationEnum;
import org.springframework.messaging.MessageHeaders;

import java.util.HashMap;

public class TestConstants {
    private TestConstants() {}

    public static final String TEST_IDENTIFIER = "testId";
    public static final String TEST_MASTER_IDENTIFIER = "testMasterId";
    public static final ProcessorOperationEnum TEST_PUBLISH_OPERATION = ProcessorOperationEnum.PUBLISH;
    public static final ProcessorOperationEnum TEST_DELETE_OPERATION = ProcessorOperationEnum.DELETE;
    public static final ProcessorOperationEnum TEST_REPLACE_OPERATION = ProcessorOperationEnum.REPLACE;
    public static final String TEST_JSON_STRING = "{\"test\": \"test\"}";
    public static final PriorityTypeEnum TEST_PRIORITY = PriorityTypeEnum.HIGH;
    public static final MessageHeaders headers = new MessageHeaders(new HashMap<>());
}
