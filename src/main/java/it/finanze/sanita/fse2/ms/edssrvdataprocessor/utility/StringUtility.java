/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;

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
	
	/**
	 * Returns {@code true} if the String passed as parameter is null or empty.
	 * 
	 * @param str	String to validate.
	 * @return		{@code true} if the String passed as parameter is null or empty.
	 */
	public static boolean isNullOrEmpty(final String str) {
		return str == null || str.isEmpty();
	}

}