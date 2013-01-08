package com.huyan.code;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.ToolManager;
import org.codehaus.plexus.util.StringUtils;

/**
 * @goal generate-controller
 * @Description 生成控制层代码
 * @author huyansoft@gmail.com
 * @date 2013-1-2
 */
public class GenerateControllerMojo extends AbstractMojo {

	/**
	* @parameter expression="${domain}"
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

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			if (StringUtils.isBlank(domain)) {
				getLog().info("Domain class is not specified. Please enter:");
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
				generateController(pojoObj.getClass());
			}
		} catch (IOException e) {
			throw new MojoExecutionException("ERROR:template path is not exists!");
		} catch (ClassNotFoundException e) {
			throw new MojoExecutionException("ERROR:Class Not Found:" + domain);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	private void generateController(Class domainClass) throws IOException {

		try {
			String packageName = domainClass.getPackage().getName();
			if (packageName.lastIndexOf(".") > 0) {
				packageName = packageName.substring(0, packageName.lastIndexOf("."))+".action";
			}
			String className = domainClass.getSimpleName();
			String propertyName = StringUtils.uncapitalise(className);
			String templateName = "Controller.java";
			String separator = File.separator;
			StringBuffer destDir = new StringBuffer();
			destDir.append(baseDir.getCanonicalPath());
			destDir.append(separator);
			destDir.append("src");
			destDir.append(separator);
			destDir.append("main");
			destDir.append(separator);
			destDir.append("java");
			destDir.append(separator);
			for (String str : StringUtils.split(packageName, ".")) {
				destDir.append(str);
				destDir.append(separator);
			}

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
			context.put("packageName", packageName);
			context.put("propertyName", propertyName);
			context.put("className", className);
			context.put("domainClass", domainClass.getName());
			
			//构建控制层文件夹
			File viewsDir = new File(destDir.toString());
			if(!viewsDir.exists()) {
				viewsDir.mkdirs();
				getLog().info("create directory : " + viewsDir.getCanonicalPath());
			}
			File output = new File(destDir.toString() + className + "Action.java");
			if (!output.exists()) {
				output.createNewFile();
			}
			FileOutputStream outStream = new FileOutputStream(output);
			OutputStreamWriter writer = new OutputStreamWriter(outStream, "UTF-8");
			;
			BufferedWriter sw = new BufferedWriter(writer);
			vTemplate.merge(context, writer);
			sw.flush();
		} catch (IOException e) {
			getLog().error(e);
			throw e;
		}
	}
}
