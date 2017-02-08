package org.stenio.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author bjhexin3 2017年1月19日
 * @version 1.0
 *
 */
public class JsonUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);

	public static <T> String toJson(T t) {
		ObjectMapper om = new ObjectMapper();
		try {
			return om.writeValueAsString(t);
		} catch (JsonProcessingException e) {
			logger.error("transform to json string error");
			return null;
		}
	}

	public static <T> T parse(String json, Class<T> type) {
		ObjectMapper om = new ObjectMapper();
		try {
			return om.readValue(json, type);
		} catch (IOException e) {
			logger.error("invalid json. json string : {} -- type : {}", json, type.getName());
			return null;
		}
	}

}
