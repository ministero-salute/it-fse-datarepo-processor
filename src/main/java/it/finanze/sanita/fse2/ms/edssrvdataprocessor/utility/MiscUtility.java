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
