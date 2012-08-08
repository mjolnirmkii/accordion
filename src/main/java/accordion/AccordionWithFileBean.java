package accordion;

import net.sourceforge.stripes.action.FileBean;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;

public class AccordionWithFileBean extends AccordionBean {
	public static final String ACCORDION_FILE_RENDER_PAGE = "/jsp/accordion/accordion_file.jsp";
	private String actionBeanProperty;
	private String modelProperty;

	private boolean isPropertyFileBean(String propertyName) {
		if (ReflectionUtils.getTypeOfProperty(getActionBeanInstance().getClass(), getAccordion().getCollectionName() + "[0]." + propertyName) == FileBean.class) {
			return true;
		}
		return false;
	}
	
	@Override
	public Resolution editAccordion() {
		for (String property : getContext().getValidationErrors().keySet()) {
			if (!isPropertyFileBean(property)) {
				getContext().getResponse().setHeader("Accordion-Validation-Error", "true");
			}
		}
		return super.editAccordion();
	}
	
	public Resolution deleteAccordion() {
		/** TODO: compatibility w/ maps, sets? */
		try {
			getManager().deleteAccordionItem(this, getItemId().intValue());
			return new StreamingResolution("text/html", "success");
		} catch (IndexOutOfBoundsException e) {
			return new StreamingResolution("text/html", "success"); // report success when deleting a non-existant item.
		} catch (Throwable t) {
			t.printStackTrace();
			return new StreamingResolution("text/html", "failure");
		}
	}
	
	public AccordionWithFileBean(AccordionActionBean actionBeanInstance, Object parentObject) {
		super(actionBeanInstance, parentObject);
	}

	public Resolution accordion_renderFileInput() {
		return new ForwardResolution(ACCORDION_FILE_RENDER_PAGE);
	}

	public String getActionBeanProperty() {
		return actionBeanProperty;
	}

	public void setActionBeanProperty(String actionBeanProperty) {
		this.actionBeanProperty = actionBeanProperty;
	}

	public String getModelProperty() {
		return modelProperty;
	}

	public void setModelProperty(String modelProperty) {
		this.modelProperty = modelProperty;
	}
}
