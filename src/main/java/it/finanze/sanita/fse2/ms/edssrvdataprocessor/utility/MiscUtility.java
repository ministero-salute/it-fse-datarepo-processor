/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility;

import javax.validation.Path;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;

public final class MiscUtility {

    /**
     * Private constructor to disallow to access from other classes
     */
    private MiscUtility() {}

    public static OffsetDateTime convertToOffsetDateTime(Date dateToConvert) {
        return dateToConvert.toInstant().atOffset(ZoneOffset.UTC);
    }

    public static String extractKeyFromPath(Path path) {
        String field = "";
        for(Path.Node node: path) field = node.getName();
        return field;
    }
}
