package com.huyan.utils;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.security.Timestamp;
import java.util.Date;

/**
 * @Description 用来生成页面输入组件的velocity工具
 * @author huyansoft@gmail.com
 * @date 2012-12-26
 */
public class RenderEditorTool {
	
	private final static String INPUT = "<input type=\"%s\" value=\"\" id=\"%s\" name=\"%s\">";
	
	/**
	* @param className
	* @param property
	* @return
	* @author huyansoft@gmail.com
	* @date 2012-12-26 上午9:45:02  
	*/
	public String render(String className, String property){
		try {
			Class clazz = Class.forName(className);
			Field field = clazz.getDeclaredField(property);
			
			Type fieldType = field.getGenericType();
			if(fieldType == Boolean.TYPE) {
				return String.format(INPUT, "checkbox", property, property);
			}else if(fieldType == Integer.TYPE || fieldType == Long.TYPE || fieldType == String.class) {
				return String.format(INPUT, "text", property, property);
			}else if (fieldType == Date.class || fieldType == Timestamp.class) {
				//TODO 需要增加Bootstrap日期插件
				return String.format(INPUT, "text", property, property);
			}else if (fieldType == File.class) {
				return String.format(INPUT, "file", property, property);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return "";
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
			return "";
		} catch (SecurityException e) {
			e.printStackTrace();
			return "";
		}
		return "";
	}
}
