package cn.turing.firecontrol.device.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class MapUtils {
	
	
	public static Object mapToObject(Map<String, Object> map, Class<?> beanClass) throws Exception {    
		          if (map == null)   
		              return null;    
		    
		          Object obj = beanClass.newInstance();  
		    
		          BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());    
		          PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();    
		          for (PropertyDescriptor property : propertyDescriptors) {  
		             Method setter = property.getWriteMethod();    
		              if (setter != null) {
		                  setter.invoke(obj, map.get(property.getName()));
		              }  
		          }  
		    
		          return obj;  
		      } 

	public static Map objectToMap(Object obj) {
		try {
			Class type = obj.getClass();
			Map returnMap = new HashMap();
			BeanInfo beanInfo = Introspector.getBeanInfo(type);
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (int i = 0; i < propertyDescriptors.length; i++) {
				PropertyDescriptor descriptor = propertyDescriptors[i];
				String propertyName = descriptor.getName();
				if (!propertyName.equals("class")) {
					Method readMethod = descriptor.getReadMethod();
					Object object = new Object[0];
					Object result = readMethod.invoke(obj, object);
					if (result == null) {
						continue;
					}
					// 判断是否为 基础类型
					// String,Boolean,Byte,Short,Integer,Long,Float,Double
					// 判断是否集合类，COLLECTION,MAP
					if (result instanceof String || result instanceof Boolean || result instanceof Byte
							|| result instanceof Short || result instanceof Integer || result instanceof Long
							|| result instanceof Float || result instanceof Double || result instanceof Enum) {
						if (result != null) {
							returnMap.put(propertyName, result);
						}
					} else if (result instanceof Collection) {
						Collection<?> lstObj = arrayToMap((Collection<?>) result);
						returnMap.put(propertyName, lstObj);

					} else if (result instanceof Map) {
						Map<Object, Object> lstObj = mapToMap((Map<Object, Object>) result);
						returnMap.put(propertyName, lstObj);
					} else {
						Map mapResult = objectToMap(result);
						returnMap.put(propertyName, mapResult);
					}

				}
			}
			return returnMap;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	private static Map<Object, Object> mapToMap(Map<Object, Object> orignMap) {
		Map<Object, Object> resultMap = new HashMap<Object, Object>();
		for (Entry<Object, Object> entry : orignMap.entrySet()) {
			Object key = entry.getKey();
			Object resultKey = null;
			if (key instanceof Collection) {
				resultKey = arrayToMap((Collection) key);
			} else if (key instanceof Map) {
				resultKey = mapToMap((Map) key);
			} else {
				if (key instanceof String || key instanceof Boolean || key instanceof Byte || key instanceof Short
						|| key instanceof Integer || key instanceof Long || key instanceof Float
						|| key instanceof Double || key instanceof Enum) {
					if (key != null) {
						resultKey = key;
					}
				} else {
					resultKey = objectToMap(key);
				}
			}

			Object value = entry.getValue();
			Object resultValue = null;
			if (value instanceof Collection) {
				resultValue = arrayToMap((Collection) value);
			} else if (value instanceof Map) {
				resultValue = mapToMap((Map) value);
			} else {
				if (value instanceof String || value instanceof Boolean || value instanceof Byte
						|| value instanceof Short || value instanceof Integer || value instanceof Long
						|| value instanceof Float || value instanceof Double || value instanceof Enum) {
					if (value != null) {
						resultValue = value;
					}
				} else {
					resultValue = objectToMap(value);
				}
			}

			resultMap.put(resultKey, resultValue);
		}
		return resultMap;
	}

	public static Collection<Object> arrayToMap(Collection<?> lstObj) {
		ArrayList arrayList = new ArrayList();
		for (Object t : lstObj) {
			if (t instanceof Collection) {
				Collection result = arrayToMap((Collection) t);
				arrayList.add(result);
			} else if (t instanceof Map) {
				Map result = mapToMap((Map) t);
				arrayList.add(result);
			} else {
				if (t instanceof String || t instanceof Boolean || t instanceof Byte || t instanceof Short
						|| t instanceof Integer || t instanceof Long || t instanceof Float || t instanceof Double
						|| t instanceof Enum) {
					if (t != null) {
						arrayList.add(t);
					}
				} else {
					if (t != null) {
						Object result = objectToMap(t);
						arrayList.add(result);
					}
				}
			}
		}
		return arrayList;
	}


	/**
	 * 实体类对象转换成Map
	 * @param obj
	 * @return
	 */
	public static Map<String, Object> convertObjToMap(Object obj) {
		Map<String, Object> reMap = new HashMap<String, Object>();
		if (obj == null)
			return null;
		Field[] fields = obj.getClass().getDeclaredFields();
		try {
			for (int i = 0; i < fields.length; i++) {
				try {
					Field f = obj.getClass().getDeclaredField(
							fields[i].getName());
					f.setAccessible(true);
					Object o = f.get(obj);
					reMap.put(fields[i].getName(), o);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return reMap;

	}



}
