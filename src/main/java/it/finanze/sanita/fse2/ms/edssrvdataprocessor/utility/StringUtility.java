/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.TimeZone;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.Constants;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.UIDModeEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.BusinessException;
import lombok.extern.slf4j.Slf4j;


/**
 * String Utility Class 
 * 
 */
@Slf4j
public final class StringUtility {

	/**
	 * Private constructor to avoid instantiation.
	 */
	private StringUtility() {

	}
	
	/**
	 * Called to generate a Transaction ID 
	 * @param mode  An enum representing the generation mode for the Transaction ID 
	 * @return String  The Transaction ID 
	 */
	public static String generateTransactionUID(final UIDModeEnum mode) {
	    
		String uid = null;

		if (!Arrays.asList(UIDModeEnum.values()).contains(mode)) {
			uid = UUID.randomUUID().toString().replace("-", "");
		} else {
			switch (mode) {
				case HOSTNAME_UUID:
					try {
						InetAddress ip = InetAddress.getLocalHost();
						uid = ip.getHostName() + UUID.randomUUID().toString().replace("-", "");
					} catch (Exception e) {
						log.error(Constants.Logs.ERROR_RETRIEVING_HOST_INFO, e);
						throw new BusinessException(Constants.Logs.ERROR_RETRIEVING_HOST_INFO, e);
					}
					break;
				case IP_UUID:
					try {
						InetAddress ip = InetAddress.getLocalHost();
						uid = ip.toString().replace(ip.getHostName() + "/", "")
								+ UUID.randomUUID().toString().replace("-", "");
					} catch (Exception e) {
						log.error(Constants.Logs.ERROR_RETRIEVING_HOST_INFO, e);
						throw new BusinessException(Constants.Logs.ERROR_RETRIEVING_HOST_INFO, e);
					}
					break;
				case UUID_UUID:
					uid = UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "");
					break;
			}
		}

		return uid;
	}

	/**
	 * Transformation from Object to Json.
	 * 
	 * @param obj	object to transform
	 * @return String  json
	 */
	public static String toJSON(final Object obj) {
		return new Gson().toJson(obj);
	}

	public static String toJSONJackson(final Object obj) {
		String out;
		try {
			final ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.registerModule(new JavaTimeModule());
			objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
			objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
			objectMapper.setTimeZone(TimeZone.getDefault());
			objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
			out = objectMapper.writeValueAsString(obj);
		} catch(Exception ex) {
			log.error("Error while running to json jackson");
			throw new BusinessException(ex);
		}
		return out;
	}

}