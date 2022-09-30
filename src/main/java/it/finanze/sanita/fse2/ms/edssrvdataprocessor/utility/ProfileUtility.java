package it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.Constants;


@Component
public class ProfileUtility {
	
    @Autowired
    private Environment environment; 
    

    public boolean isTestProfile() {
        if (environment != null && environment.getActiveProfiles().length > 0) {
            return environment.getActiveProfiles()[0].contains(Constants.Profile.TEST_SYNC) 
            		|| environment.getActiveProfiles()[0].contains(Constants.Profile.TEST_ASYNC) ;
        }
        return false;
    }

    public boolean isDevProfile() {
        if (environment != null && environment.getActiveProfiles().length > 0) {
            return environment.getActiveProfiles()[0].contains(Constants.Profile.DEV);
        }
        return false;
    }
} 
