package accordion;

import accordion.AccordionBean;
import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.Resolution;

/** Interface for accordions. In order to use, have an ActionBean implement 
 *  this interface, then follow the mixin pattern using an instance of 
 *  AccordionBean (delegate Resolutions to the AccordionBean).
 *  
 *  @author Chase Putnam (chasedputnam@gmail.com)
 *  @version $Id: AccordionActionBean.java,v 1.2 2012/01/27 21:01:52 cputnam Exp $
 */
public interface AccordionActionBean extends ActionBean {
	
	/** Get the bean used to store parameters */
	AccordionBean getAccordion();
	
	/** Return a display-only accordion section (for ajax) */
	Resolution displayAccordion();
	
	/** Return an editable accordion section (for ajax) */
	Resolution editAccordion();
	
	/** Return a new accordion section (for ajax) */
	Resolution newAccordion();
	
	/** Delete the accordion specified by accordion.itemId */
	Resolution deleteAccordion();
	
	/** Save form from editable accordion, display edit section on failure, 
	 *  read-only on success */
	//Resolution saveAccordion();

}