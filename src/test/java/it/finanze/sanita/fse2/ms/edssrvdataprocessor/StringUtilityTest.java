/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.base.AbstractTest;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.UIDModeEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility.StringUtility;

class StringUtilityTest extends AbstractTest {
 
	
	@Test
	void generateTransactionUIDTest() {
		String uuidIpMode = StringUtility.generateTransactionUID(UIDModeEnum.IP_UUID); 
		String uuidHostnameMode = StringUtility.generateTransactionUID(UIDModeEnum.HOSTNAME_UUID); 
		String uuidIdMode = StringUtility.generateTransactionUID(UIDModeEnum.UUID_UUID);  

		assertEquals(String.class, uuidIpMode.getClass()); 
		assertEquals(String.class, uuidHostnameMode.getClass()); 
		assertEquals(String.class, uuidIdMode.getClass()); 

		assertEquals(64, uuidIdMode.length()); 

	} 
	
}
