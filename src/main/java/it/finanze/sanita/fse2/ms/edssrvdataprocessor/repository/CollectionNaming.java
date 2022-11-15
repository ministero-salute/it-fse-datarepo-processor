/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
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

    @Bean("referenceBean")
    public String getDocumentReference() {
        return profileUtility.isTestProfile() ? TEST_PREFIX + DOCUMENT_REFERENCE : DOCUMENT_REFERENCE;
    }
}
