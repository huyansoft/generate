package com.huyan.utils;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.security.Timestamp;
import java.util.Date;

import org.codehaus.plexus.util.StringUtils;

/**
 * @Description 用来生成页面输入组件的velocity工具
 * @author huyansoft@gmail.com
 * @date 2012-12-26
 */
public class RenderEditorTool {
	
	private final static String INPUT = "<input type=\"%s\" value=\"%s\" name=\"%s\">";
	
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
			
			String classSimpleName = StringUtils.uncapitalise(clazz.getSimpleName());
			String valueStr = "$!"+classSimpleName+"Instance."+property;
			String nameStr = property;
			
			Type fieldType = field.getGenericType();
			if(fieldType == Boolean.TYPE) {
				return String.format(INPUT, "checkbox", valueStr, nameStr);
			}else if(fieldType == Integer.TYPE || fieldType == Long.TYPE || fieldType == String.class) {
				return String.format(INPUT, "text", valueStr, nameStr);
			}else if (fieldType == Date.class || fieldType == Timestamp.class) {
				//TODO 需要增加Bootstrap日期插件
				return String.format(INPUT, "text", valueStr, nameStr);
			}else if (fieldType == File.class) {
				return String.format(INPUT, "file", valueStr, nameStr);
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
