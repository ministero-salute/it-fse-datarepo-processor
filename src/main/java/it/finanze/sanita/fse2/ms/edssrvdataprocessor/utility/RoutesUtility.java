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

public final class RoutesUtility {

    private RoutesUtility() {}

    public static final String API_VERSION = "v1";

    public static final String API_PROCESS = "process";
    public static final String API_TRANSACTIONS = "transactions";

    public static final String API_QP_TIMESTAMP = "timestamp";
    public static final String API_QP_PAGE = "page";
    public static final String API_QP_LIMIT= "limit";

    public static final String PATH_DELIMITER = "/";

    public static final String API_PROCESS_PATH = PATH_DELIMITER + API_VERSION + PATH_DELIMITER + API_PROCESS;
    public static final String API_TRANSACTIONS_PATH = PATH_DELIMITER + API_VERSION + PATH_DELIMITER + API_TRANSACTIONS;

    public static final String API_PROCESSOR_TAG = "Data Processor";
    public static final String API_TRANSACTIONS_TAG = "Transactions";

}
