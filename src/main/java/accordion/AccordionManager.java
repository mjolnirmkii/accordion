package accordion;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class AccordionManager {

	private static final Log logger = LogFactory.getLog(AccordionManager.class);
	
	@Autowired
    private HibernateTemplate ht;
	
    /**
     * Saves entities to the database
     */
	public <T> T persistEntity(T entity) throws ServiceException
    {
        try 
        {
        	ht.saveOrUpdate(entity);
            return entity;
        } 
        catch (Throwable t) 
        {
            logger.error(t);
            throw new ServiceException("Error saving or updating entity " + entity, t);
        }
    }

	public void deleteAccordionItem(AccordionBean accordionBean, int index) throws Exception {
		List<?> list = accordion.ReflectionUtils.getListByCollectionName(accordionBean.getActionBeanInstance(), accordionBean.getCollectionName());
		list.remove(index);
		persistEntity(accordionBean.getParentObject());
	}
}
