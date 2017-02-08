package org.stenio.common.util;

import org.apache.http.Consts;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import java.nio.charset.Charset;

public class JSONEntity extends StringEntity {

	private static final String MEDIA_TYPE = "application/json";

	public JSONEntity(String json) {
		super(json, ContentType.create(MEDIA_TYPE, Consts.UTF_8));
	}

	public JSONEntity(String json, String encoding){
		super(json, ContentType.create(MEDIA_TYPE, Charset.forName(encoding)));
	}

}
