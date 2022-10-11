package it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility;

import lombok.extern.slf4j.Slf4j;

/**
 * Helper Utility Class 
 *
 */
@Slf4j
public class HelperUtility {
    /**
     * Dead Letter Helper 
     * 
     * @param e  Exception
     */
    public static void deadLetterHelper(Exception e) {
        StringBuilder sb = new StringBuilder("LIST OF USEFUL EXCEPTIONS TO MOVE TO DEADLETTER OFFSET 'kafka.consumer.dead-letter-exc'. ");
        boolean continua = true;
        Throwable excTmp = e;
        Throwable excNext = null;

        while (continua) {

            if (excNext != null) {
                excTmp = excNext;
                sb.append(", ");
            }

            sb.append(excTmp.getClass().getCanonicalName());
            excNext = excTmp.getCause();

            if (excNext == null) {
                continua = false;
            }

        }

        log.error("{}", sb);
    }
}
