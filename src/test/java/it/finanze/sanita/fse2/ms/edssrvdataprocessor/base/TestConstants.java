/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
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
