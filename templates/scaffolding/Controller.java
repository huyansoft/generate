package ${packageName};

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ${domainClass};
import cn.com.daydayup.mvc.ActionException;
import cn.com.daydayup.mvc.RequestContext;
import cn.com.daydayup.util.ResourceUtils;

public class ${className}Action extends BaseAction{
	private static final Log log = LogFactory.getLog(UserAction.class);

	public void list(RequestContext ctx) {
		try {
			ctx.request().setAttribute("${propertyName}List", ${className}.INSTANCE.list());
			ctx.request().setAttribute("count", ${className}.INSTANCE.totalCount());
			ctx.forward("/WEB-INF/${propertyName}/list.vm");
		} catch (ServletException e) {
			e.printStackTrace();
			throw new ActionException(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			throw new ActionException(e.getMessage());
		}
	}

	public void create(RequestContext ctx) {
		try {
			ctx.forward("/WEB-INF/${propertyName}/create.vm");
		} catch (ServletException e) {
			e.printStackTrace();
			throw new ActionException(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			throw new ActionException(e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	public void save(RequestContext ctx) {
		${className} ${propertyName} = ctx.form(${className}.class);
		try {
			if (!${className}.INSTANCE.save(ctx, ${propertyName})) {
				ctx.flash().put("${propertyName}Instance", ${propertyName});
				redirect("create");
			} else {
				ctx.message("default.created.message", ResourceUtils.ui("${propertyName}.label"));
				ctx.flash().put("id", ${propertyName}.getId() + "");
				redirect("show");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void show(RequestContext ctx) {
		try {
			long id = ctx.param("id", -1);
			${className} ${propertyName} = ${className}.INSTANCE.get(id);
			if (null == ${propertyName}) {
				ctx.message("default.not.found.message", id, ResourceUtils.ui("${propertyName}.label"));
				redirect("list");
			} else {
				ctx.request().setAttribute("${propertyName}Instance", ${propertyName});
				ctx.forward("/WEB-INF/${propertyName}/show.vm");
			}
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void edit(RequestContext ctx) {
		try {
			long id = ctx.param("id", -1);
			${className} ${propertyName} = ${className}.INSTANCE.get(id);
			if (null == ${propertyName}) {
				ctx.message("default.not.found.message", id, ResourceUtils.ui("${propertyName}.label"));
				redirect("list");
			} else {
				ctx.request().setAttribute("${propertyName}Instance", ${propertyName});
				ctx.forward("/WEB-INF/${propertyName}/edit.vm");
			}
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void update(RequestContext ctx) {
		try {
			long id = ctx.param("id", -1);
			${className} ${propertyName} = ${className}.INSTANCE.get(id);
			if (null == ${propertyName}) {
				ctx.message("default.not.found.message", id, ResourceUtils.ui("${propertyName}.label"));
				redirect("list");
			} else {
				${propertyName} = ctx.form(${className}.class);
				${propertyName}.update();
				ctx.message("default.updated.message", ResourceUtils.ui("${propertyName}.label"));
				ctx.flash().put("id", ${propertyName}.getId() + "");
				redirect("show");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void delete(RequestContext ctx) {
		try {
			long id = ctx.param("id", -1L);
			${className} ${propertyName} = ${className}.INSTANCE.get(id);
			if (null == ${propertyName}) {
				ctx.message("default.not.found.message", id, ResourceUtils.ui("${propertyName}.label"));
				redirect("list");
			} else {
				if (${propertyName}.delete()) {
					ctx.message("default.deleted.message", ResourceUtils.ui("${propertyName}.label"), id);
				} else {
					ctx.message("default.not.found.message", ResourceUtils.ui("${propertyName}.label"), id);
				}
				redirect("list");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
