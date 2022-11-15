/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import static it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.Constants.Profile.TEST;

/**
 * Profile Utility Class 
 *
 */
@Component
public class ProfileUtility {
	
	/** 
	 * Environment 
	 */
    @Autowired
    private Environment environment; 
    

    /**
     * True if we are in test environmnet 
     * 
     * @return  A boolean which is true if we are in test environment 
     */
    public boolean isTestProfile() {
        if (environment != null && environment.getActiveProfiles().length > 0) {
            return environment.getActiveProfiles()[0].toLowerCase().contains(TEST);
        }
        return false;
    }
} 

