package com.huyan.code;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.ToolManager;
import org.codehaus.plexus.util.StringUtils;

/**
 * @goal generate-view
 * @description 用来生成视图文件的mojo。该目标根据项目中的已经定义的POJO来生成对应的视图文件。
 * @author huyansoft@gmail.com
 * @date 2012-12-24
 */
public class GenerateViewMojo extends AbstractMojo {

	/** 
	* @parameter expression="${domain}" default-value=""
	*/
	private String domain;

	/** 
	* 模板路径 
	* @parameter expression="${template}" default-value="${basedir}/templates/scaffolding"
	*/
	private File template;

	/** 
	* @parameter expression="${baseDir}" default-value="${basedir}"
	*/
	private File baseDir;

	/*  
	* <p>Title: execute</p> 
	* <p>Description: </p> 
	* @throws MojoExecutionException
	* @throws MojoFailureException 
	* @see org.apache.maven.plugin.Mojo#execute() 
	*/
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			if (StringUtils.isBlank(domain)) {
				getLog().info("Domain class not specified. Please enter:");
				BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
				domain = stdin.readLine();
			}

			if (!template.exists()) {
				throw new MojoExecutionException("ERROR:template path : " + template.getCanonicalPath()
						+ " is not exists!");
			} else if (!template.isDirectory()) {
				throw new MojoExecutionException("ERROR:template path : " + template.getCanonicalPath()
						+ " is not a directory!");
			} else {
				
				Object pojoObj = Class.forName(domain).newInstance();
				generateViews(pojoObj.getClass());
			}
			getLog().info(template.getCanonicalPath());
		} catch (IOException e) {
			throw new MojoExecutionException("ERROR:template path is not exists!");
		} catch (ClassNotFoundException cnfe) {
			throw new MojoExecutionException("ERROR:Class Not Found:" + domain);
		} catch (IllegalAccessException iae) {
			throw new MojoExecutionException("ERROR:" + iae.getMessage());
		} catch (InstantiationException ie) {
			throw new MojoExecutionException("ERROR:" + ie.getMessage());
		}
	}

	public static void main(String[] args) {
		Object pojoObj;
		try {
			pojoObj = Class.forName("com.huyan.code.GenerateViewMojo").newInstance();
//			String packageStr = pojoObj.getClass().getPackage().getName();
//			String className = pojoObj.getClass().getSimpleName();
//			String domainDirName = className.substring(0, 1).toLowerCase() + className.substring(1);
//			File viewsDir = new File("d:\\WEB-INF" + File.separator + domainDirName);
//			if(!viewsDir.exists()) {
//				viewsDir.mkdirs();
//			}
//			System.out.println(domainDirName);
//			Velocity.init("src/main/resources/velocity.properties");
			
			VelocityEngine ve = new VelocityEngine();
			ve.init("src/main/resources/velocity.properties");
			ve.addProperty("file.resource.loader.path", "");
			Template template = ve.getTemplate("create.vm");
			
			ToolManager manager = new ToolManager();
			manager.setVelocityEngine(ve);
			manager.configure("src/main/resources/velocity-toolbox.xml");
			Context context = manager.createContext();
			System.out.println("!!!!!!" + context.containsKey("test"));
			for(Object obj:context.getKeys()) {
				System.out.println("key:" + obj);
				//System.out.println("value:" + context.get(obj.toString()));
			}
			
			
			
			List<String> names = new ArrayList<String>();
			names.add("a");
			names.add("b");
			names.add("c");
			names.add("d");
			context.put("props", names);
			context.put("foreach", "#foreach");
			context.put("end", "#end");
			context.put("propertyName", "$class");
			context.put("aaa","bbb");
			File output = new File("d:\\list.java");
			if(!output.exists()) {
				output.createNewFile();
			}
			FileOutputStream outStream = new FileOutputStream(output);
			OutputStreamWriter writer = new OutputStreamWriter(outStream,"UTF-8");;
			BufferedWriter sw = new BufferedWriter(writer);
			template.merge(context, writer);
			sw.flush();
			
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	private void generateViews(Class domainClass) throws MojoExecutionException {
		try {
			String domainClassName = domainClass.getSimpleName();
			
			//视图文件输出路径
			String destDirName = baseDir.getCanonicalPath() + File.separator + "src" + File.separator + "main"
					+ File.separator + "webapp" + File.separator + "WEB-INF" + File.separator
					+ StringUtils.uncapitalise(domainClassName);
			//构建视图文件夹
			File viewsDir = new File(destDirName);
			if(!viewsDir.exists()) {
				viewsDir.mkdirs();
				getLog().info("create directory : " + viewsDir.getCanonicalPath());
			}
			
			for(String viewTemplateName : getTemplateNames()) {
				getLog().info("create view : " + viewTemplateName);
				generateView(domainClass, viewTemplateName, viewsDir.getCanonicalPath());
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/** 
	* @Description: 获取所有的模板文件名
	* @return
	* @throws MojoExecutionException
	* @author huyansoft@gmail.com
	* @date 2013-1-7 上午10:15:37  
	*/
	private String[] getTemplateNames() throws MojoExecutionException {
		String [] templateNames = null;
		IOFileFilter fileFilter1 = new SuffixFileFilter(".vm");
		IOFileFilter fileFilter2 = new NotFileFilter(DirectoryFileFilter.INSTANCE);
		FilenameFilter filenameFilter = new AndFileFilter(fileFilter1, fileFilter2);
		try {
			templateNames = new File(template.getCanonicalPath()).list(filenameFilter);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return templateNames;
	}
	
	/** 
	* @Description: 生成视图文件
	* @param domainClass domain类
	* @param templateName 模板文件名称
	* @param destDir 目标文件夹
	* @throws MojoExecutionException
	* @author huyansoft@gmail.com
	* @date 2013-1-7 上午10:17:00  
	*/
	private void generateView(Class domainClass, String templateName, String destDir) throws MojoExecutionException {
		try {
			String propertyName = StringUtils.uncapitalise(domainClass.getSimpleName());
			
			//构建velocity模板引擎
			VelocityEngine ve = new VelocityEngine();
			ve.addProperty("file.resource.loader.path", template.getCanonicalPath());
			ve.init(template.getCanonicalPath() + File.separator + "velocity.properties");
			Template vTemplate = ve.getTemplate(templateName);
			//加载velocity tools
			ToolManager manager = new ToolManager();
			manager.setVelocityEngine(ve);
			manager.configure(template.getCanonicalPath() + File.separator + "velocity-toolbox.xml");
			Context context = manager.createContext();
			
			List<String> names = new ArrayList<String>();
			for(String name : listFields(domainClass.newInstance()).keySet()) {
				names.add(name);
			}
			context.put("domainClass", domainClass.getName());
			context.put("propertyName", propertyName);
			context.put("props", names);
			context.put("operator_1", "#");
			context.put("operator_2", "$");
			File output = new File(destDir + File.separator + templateName);
			if(!output.exists()) {
				output.createNewFile();
			}
			FileOutputStream outStream = new FileOutputStream(output);
			OutputStreamWriter writer = new OutputStreamWriter(outStream,"UTF-8");;
			BufferedWriter sw = new BufferedWriter(writer);
			vTemplate.merge(context, writer);
			sw.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	/** 
	* @Description: 获得一个实体的所有属性
	* @param obj
	* @return
	* @author huyansoft@gmail.com
	* @date 2013-1-7 上午10:18:36  
	*/
	@SuppressWarnings("unchecked")
	private Map<String, Object> listFields(Object obj) {
		try {
			Map<String, Object> props = BeanUtils.describe(obj);
			props.remove("class");
			return props;
		} catch (Exception e) {
			throw new RuntimeException("Exception when Fetching fields of " + this);
		}
	}

}
