package accordion;


import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.Tag;

import net.sourceforge.stripes.action.UrlBinding;

public class AccordionFileTag implements Tag {
	private static final String FILE_RENDER_EVENT = "accordion_renderFileInput";
	private Tag parent;
	private PageContext pageContext;
	private String modelProperty;
	private String actionBeanProperty;
	
	public int doEndTag() throws JspException {
		return SKIP_BODY;
	}

	public int doStartTag() throws JspException {
		JspWriter w = pageContext.getOut();
		try {
			w.print("<iframe ");
			w.print("id=\"");
			// generate random iframe id...
			String iframeId = UUID.randomUUID().toString();
			w.print(iframeId);
			w.print("\" scrolling=\"no\" ");
			w.print("class=\"accordion_fileIframe\" frameborder=\"0\" src=\"");
			AccordionActionBean actionBean = (AccordionActionBean) pageContext.getVariableResolver().resolveVariable("actionBean");
			w.print(((HttpServletRequest) pageContext.getRequest()).getContextPath());
			w.print(actionBean.getClass().getAnnotation(UrlBinding.class).value());
			w.print("?");
			w.print(FILE_RENDER_EVENT);
			w.print("&accordion.id="); // reusing id variable -- oh noes!
			w.print(iframeId);
			w.print("&accordion.collectionName=");
			w.print(actionBean.getAccordion().getCollectionName());
			w.print("&accordion.itemId=");
			if (actionBean.getAccordion().getItemId() != null) {
				w.print(actionBean.getAccordion().getItemId());
			}
			else {
				w.print(ReflectionUtils.getListByCollectionName(actionBean, actionBean.getAccordion().getCollectionName()).size()); // new accordion
			}
			w.print("&accordion.actionBeanProperty=");
			w.print(getActionBeanProperty());
			w.print("&accordion.modelProperty=");
			w.print(getModelProperty());
			w.print("\" />");
		} catch (Exception e) {
			throw new JspException(e);
		}
		return EVAL_PAGE;
	}

	public Tag getParent() {
		return parent;
	}

	public void release() {}

	public void setPageContext(PageContext context) {
		pageContext = context;
	}

	public void setParent(Tag parent) {
		this.parent = parent;
	}

	public String getModelProperty() {
		return modelProperty;
	}

	public void setModelProperty(String modelProperty) {
		this.modelProperty = modelProperty;
	}

	public String getActionBeanProperty() {
		return actionBeanProperty;
	}

	public void setActionBeanProperty(String actionBeanProperty) {
		this.actionBeanProperty = actionBeanProperty;
	}
}
