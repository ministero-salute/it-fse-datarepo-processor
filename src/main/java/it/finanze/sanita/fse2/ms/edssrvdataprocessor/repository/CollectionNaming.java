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
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility.ProfileUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.Constants.Collections.*;
import static it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.Constants.Profile.*;

@Configuration
public class CollectionNaming {
    
    @Autowired
    private ProfileUtility profileUtility;

    @Bean("ingestionStagingBean")
    public String getDocumentReference() {
        return profileUtility.isTestProfile() ? TEST_PREFIX + INGESTION_STAGING : INGESTION_STAGING;
    }

    @Bean("transactionBean")
    public String getTransactionStatus() {
        return profileUtility.isTestProfile() ? TEST_PREFIX + TRANSACTION_STATUS : TRANSACTION_STATUS;
    }
    
}
