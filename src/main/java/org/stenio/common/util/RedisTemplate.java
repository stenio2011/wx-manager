package org.stenio.common.util;


import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;

/**
 * @author bjhexin3 2017年1月19日
 * @version 1.0
 *
 */
public class RedisTemplate {

	private static final String HOST = ConfigUtil.getString("redis.host");

	private static final int PORT = ConfigUtil.getInt("redis.port");

	private static final int TIMEOUT = ConfigUtil.getInt("redis.timeout");

	private static final String PASSWORD = ConfigUtil.getString("redis.password");

	private static JedisPool pool = new JedisPool(new JedisPoolConfig(), HOST, PORT, TIMEOUT,
			StringUtil.isEmpty(PASSWORD) ? null : PASSWORD);

	public static String get(String key) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.get(key);
		} catch (Exception e) {
			return null;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public static <T> T get(String key, Class<T> type) {
		String value = get(key);
		if(value == null){
			return null;
		}
		return JsonUtil.parse(value, type);
	}

	public static String set(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.set(key, value);
		} catch (Exception e) {
			return null;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public static String setex(String key, int seconds, String value) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.setex(key, seconds, value);
		} catch (Exception e) {
			return null;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public static <T> String set(String key, T obj) {
		String json = JsonUtil.toJson(obj);
		return set(key, json);
	}

	public static <T> String setex(String key, int seconds, T obj) {
		String json = JsonUtil.toJson(obj);
		return setex(key, seconds, json);
	}

	public static Long del(String key) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.del(key);
		} catch (Exception e) {
			return -1L;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}
	
	public static Set<String> keys(String pattern) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.keys(pattern);
		} catch (Exception e) {
			return new HashSet<>();
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}
	
	public static Long ttl(String key) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.ttl(key);
		} catch (Exception e) {
			return -1L;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}
}
