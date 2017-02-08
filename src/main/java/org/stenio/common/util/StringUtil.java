/*
 * Copyright (c) 1997, 2016, 网易公司版权所有. All rights reserved. 
 */
package org.stenio.common.util;

/**
 * @author bjhexin3 2017年1月19日
 * @version 1.0
 *
 */
public class StringUtil {

	public static boolean isEmpty(String str) {
		if (str == null) {
			return true;
		}
		if ("".equals(str)) {
			return true;
		}
		return false;
	}

	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}
}
