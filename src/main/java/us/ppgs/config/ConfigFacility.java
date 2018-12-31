package us.ppgs.config;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import us.ppgs.config.dao.ConfigDao;
import us.ppgs.config.dao.ConfigException;
import us.ppgs.config.dao.ConfigPair;

@Service
public class ConfigFacility {

	private static ConfigDao configDao;

	@Autowired
	public ConfigFacility(ConfigDao configDao) {
		ConfigFacility.configDao = configDao;
	}
	
	private static Map<String, ConfigPair> cacheMap = null;
	private static Object mutex = new Object();
	
	public static Integer getInteger(String name) {
		String s = get(name);
		return s == null ? null : new Integer(s);
	}
	
	public static Integer getInteger(String name, int dflt) {
		return new Integer(get(name, Integer.toString(dflt)));
	}
	
	public static int getInt(String name) {
		return Integer.parseInt(get(name));
	}
	public static int getInt(String name, int dflt) {
		return Integer.parseInt(get(name, Integer.toString(dflt)));
	}

	public static String get(String name) {

		return get(name, (String) null);
	}

	public static String get(String name, String dflt) {
		ConfigPair cp = getConfigPair(name, dflt);
		return cp == null ? null : cp.getValue();
	}

	public static ConfigPair getConfigPair(String name, String dflt) {
		
		ConfigPair cp = getCacheMap().get(name);
		if (cp == null) {
			if (dflt == null) {
				// note that it was requested
				get("_requested." + name, "");
				return null;
			}
			else {
				// value was not in cache, force db refresh
				synchronized(mutex) {
					cacheMap = null;
					// make sure another thread hasn't beat us to the save
					cp = getCacheMap().get(name);
					if (cp == null) {
						try {
							configDao.add(name, dflt);
						}
						catch (ConfigException e) { // shouldn't happen but I want to know if it does
							e.printStackTrace();
						}
						cacheMap = configDao.getCache();
						cp = cacheMap.get(name);
					}
				}
			}
		}
		
		// multi_line clobs are not cached, should this go off of multi-line val?
		if (cp.getValue() == null) {
			cp.setValue(configDao.getValue(name));
		}

		return cp;
	}

	public static Map<String, ConfigPair> getCacheMap() {
		synchronized(mutex) {
			if (cacheMap == null) {
				cacheMap = configDao.getCache();
			}
			
			return cacheMap;
		}
	}

	public static <T> T get(String name, Class<T> valueClass) {
		T value = null;
		try {
			String strValue = get(name);
			if (strValue == null) {
				return null;
			}
			
			ObjectMapper m = new ObjectMapper();
			value = m.readValue(strValue, valueClass);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return value;
	}
	
	public static void add(String name) throws ConfigException {
		add(name, "");
	}

	public static void add(String name, String value) throws ConfigException {
		configDao.add(name, value);
		synchronized(mutex) {
			cacheMap = null;
		}
	}

	public static <T> void add(String name, Object value) throws ConfigException {
		add(name, marshall(value));
	}

	public static void delete(int id) throws ConfigException {
		configDao.delete(id);
		synchronized(mutex) {
			cacheMap = null;
		}
	}

	public static void rename(int id, String nameTo) throws ConfigException {
		configDao.rename(id, nameTo);
		synchronized(mutex) {
			cacheMap = null;
		}
	}

	public static void save(int id, String value) {
		configDao.save(id, value);
		synchronized(mutex) {
			cacheMap = null;
		}
	}

	public static void save(String name, String value) {
		configDao.save(name, value);
		synchronized(mutex) {
			cacheMap = null;
		}
	}
	
	public static <T> void save(String name, T value) {
			save(name, marshall(value));
	}
	
	private static <T> String marshall(T value) {

		ObjectMapper om = new ObjectMapper();
		try {
			return om.writerWithDefaultPrettyPrinter().writeValueAsString(value);
		}
		catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
