package accordion;

import net.sourceforge.stripes.action.Resolution;

public interface AccordionWithFileActionBean extends AccordionActionBean {
	/** Project/class specific method to handle an uploaded file. */
	Resolution accordion_fileSubmit();
	
	/** Project/class specific method to handle an file deletion. */
	Resolution accordion_fileDelete();
	
	Resolution accordion_renderFileInput();
}
