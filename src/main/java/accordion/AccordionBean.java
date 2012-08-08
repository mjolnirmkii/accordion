package accordion;

import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.integration.spring.SpringHelper;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.ValidationErrors;

/** Follows the mixin pattern, to emulate multiple inheritance.
 * 
 *  A utility bean, that becomes bound to an ActionBean, which should delegate
 *  its AccordionActionBean implementation an instance of this (AccordionBean).
 * 
 * @author Chase Putnam (chasedputnam@gmail.com)
 * @version $Id: AccordionBean.java,v 1.7 2012/07/31 15:45:38 cputnam Exp $
 *
 */
public class AccordionBean implements AccordionActionBean {

	public static final String ACCORDION_BASE_PREFIX = "/jsp/accordion/";
	private static final String ACCORDION_RENDER_PAGE = "/jsp/accordion/accordion_render.jsp";
	
	@SpringBean("accordionService")
	private AccordionManager manager;
	
	private boolean customHeader;
	private String prefix;
	private String id;
	private String headerProperty;
	private String headerJsp;
	private String contentJsp;
	private String collectionName;
	private Integer itemId;
	private AccordionActionBean actionBeanInstance; // instance of the actionBean this class is working with;
	private Object parentObject;
	
	/** Constructor to assist in configuring accordion beans via spring */
	public AccordionBean() {}
	
	/** Creates an accordion utility object bound to the given ActionBean.
	 * 
	 * @param actionBeanInstance  the instance of the actionBean (pass this)
	 * @param ht  HibernateTemplate for persisting
	 * @param parentObject  an Object to be persisted on saves
	 */
	public AccordionBean(AccordionActionBean actionBeanInstance, Object parentObject) {
		this.setActionBeanInstance(actionBeanInstance);
		this.setParentObject(parentObject);
		SpringHelper.injectBeans(this, actionBeanInstance.getContext());
	}
	
	
	
	/** Returns a display-only accordion section (for ajax) */
	public Resolution displayAccordion() {
		setupHeaderJsp();
		setupDisplayJsp();
		return new ForwardResolution (getAccordionRenderPage());
	}

	/** Displays an editable accordion section (for ajax) */
	public Resolution editAccordion() {
		setupHeaderJsp();
		setupEditJsp();
		return new ForwardResolution (getAccordionRenderPage());
	}
	
	/** Called by an attempt to save an accordion, 
	 *  returns edit section w/ errors on failure, 
	 *  display-only on success (for ajax). Also checks for empty accordion. */
	public Resolution saveAccordion() {
		try {
			if (ReflectionUtils.getListByCollectionName(getActionBeanInstance(), collectionName).size() == itemId) {
				ValidationErrors errors = new ValidationErrors();
			    errors.addGlobalError(new LocalizableError("accordion.emptyAccordion"));
			    getActionBeanInstance().getContext().setValidationErrors(errors);
				return getActionBeanInstance().editAccordion();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			getManager().persistEntity(getParentObject());
		} catch (Throwable t) {
			ValidationErrors errors = new ValidationErrors();
	        errors.addGlobalError(new LocalizableError("accordion.saveFailed"));
	        getActionBeanInstance().getContext().setValidationErrors(errors);
	        t.printStackTrace();
	        return getActionBeanInstance().editAccordion();
		}
		return getActionBeanInstance().displayAccordion();
	}

	/** Returns an accordion section for a new item (for ajax) */
	public Resolution newAccordion() {
		itemId = null;
		setupHeaderJsp();
		setupEditJsp();
		return new ForwardResolution (getAccordionRenderPage());
	}
	
	/** Deletes an object associated w/ an accordion instance.
	 *  Note that client-side, accordion.js handles renumbering of indices.
	 */
	public Resolution deleteAccordion() {
		/** TODO: compatibility w/ maps, sets? */
		try {
			getManager().deleteAccordionItem(this, getItemId().intValue());
			return new StreamingResolution("text/html", "success");
		} catch (Throwable t) {
			t.printStackTrace();
			return new StreamingResolution("text/html", "failure");
		}
	}

	/** Returns path to the header jsp. If headerJsp is null, initializes it.
	 * 
	 * @return  webapp-relative path to jsp
	 */
	public String getHeaderJsp() {
		if (headerJsp == null) {
			setupHeaderJsp();
		}
		
		return headerJsp;
	}

	public void setHeaderJsp(String headerJsp) {
		this.headerJsp = headerJsp;
	}

	/** Returns path to the content jsp. If contentJsp is null, initializes it to the edit jsp.
	 * 
	 * @return  webapp-relative path to content jsp
	 */
	public String getContentJsp() {
		if (contentJsp == null) {
			setupEditJsp();
		}
		return contentJsp;
	}

	public void setContentJsp(String contentJsp) {
		this.contentJsp = contentJsp;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	public String getHeaderProperty() {
		return headerProperty;
	}

	public void setHeaderProperty(String headerProperty) {
		this.headerProperty = headerProperty;
	}
	
	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isCustomHeader() {
		return customHeader;
	}

	public void setCustomHeader(boolean customHeader) {
		this.customHeader = customHeader;
	}

	public AccordionActionBean getActionBeanInstance() {
		return actionBeanInstance;
	}

	public Object getParentObject() {
		return parentObject;
	}

	/** Superfluous, simply for completeness of interface implementation. */
	public AccordionBean getAccordion() {
		return this;
	}

	public void setContext(ActionBeanContext context) {
		getActionBeanInstance().setContext(context);
	}

	public ActionBeanContext getContext() {
		return getActionBeanInstance().getContext();
	}

	/* utility methods : start */
	public void setupHeaderJsp() {
		if (isCustomHeader()) {
			setHeaderJsp(AccordionBean.ACCORDION_BASE_PREFIX + getPrefix() + "header.jsp");
		}
		else {
			setHeaderJsp("/jsp/accordion/accordion_generic_header.jsp");
		}
	}
	
	public void setupDisplayJsp() {
		setContentJsp(AccordionBean.ACCORDION_BASE_PREFIX + getPrefix() + "display.jsp");
	}
	
	public void setupEditJsp() {
		setContentJsp(AccordionBean.ACCORDION_BASE_PREFIX + getPrefix() + "edit.jsp");
	}
	/* utility methods : end */

	public String getAccordionRenderPage() {
		return ACCORDION_RENDER_PAGE;
	}

	public AccordionManager getManager() {
		return manager;
	}

	public void setManager(AccordionManager manager) {
		this.manager = manager;
	}

	public void setActionBeanInstance(AccordionActionBean actionBeanInstance) {
		this.actionBeanInstance = actionBeanInstance;
	}

	public void setParentObject(Object parentObject) {
		this.parentObject = parentObject;
	}
}
