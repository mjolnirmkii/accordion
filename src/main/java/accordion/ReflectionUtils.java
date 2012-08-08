package accordion;

import java.lang.reflect.Field;
import java.util.List;

import net.sourceforge.stripes.util.ResolverUtil;
import net.sourceforge.stripes.util.ResolverUtil.Test;

/** Set of utilities for reflection operations
 * 
 * @author Chase Putnam (chasedputnam@gmail.com)
 * @version $Id: ReflectionUtils.java,v 1.5 2012/03/13 18:00:18 cputnam Exp $
 *
 */
public class ReflectionUtils {
	/** Gets a list by reflectively calling the getter of the passed collectionName.
	 * 
	 * @param clazz  Instance of the object 
	 * @return the  list that is a property of the object
	 * @throws Exception  if an exception was thrown trying to access the property
	 */
	public static List<?> getListByCollectionName(Object object, String collectionName) throws Exception {
		try {
			return (List<?>) getProperty(object, collectionName);
		} catch (Exception e) {
			throw new Exception("Error accessing property " + collectionName + ".", e);
		}
	}

	/** Constructs the getter for a given class.
	 * 
	 * @param propertyName  the name of the property to get
	 * @return the name of the getter
	 */
	public static String constructGetterName (String propertyName) {
		return "get" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
	}

	/** Constructs a new object using the empty, no-args constructor for the given class.
	 *  First, finds the class by searching through the package,
	 *  then returns a new instance of that class.
	 *  
	 *  (Ex. Simple name of a class is [List] vs. FQN [java.util.List])
	 * 
	 * @param className  the "simple name" of the class
	 * @param packageName  the package in which to look
	 * @return  new instance of the class
	 */
	public static Object constructNew (final String className, String packageName) {
		ResolverUtil<Object> resolver = new ResolverUtil<Object>()
				.find(new Test() {

					public boolean matches(Class<?> type) {
						if (type.getSimpleName().equals(className)) {
							return true;
						}
						return false;
					}
					
				}, packageName);
		Object newInstance = null;
		for (Class<?> clazz : resolver.getClasses()) {
			try {
				newInstance = clazz.newInstance();
			} catch (Exception e) {
				System.err.println("Warning: could not instantiate new " + className + ":");
				e.printStackTrace();
			}
		}
		return newInstance;
	}
	
	/** Dynamically calls the getter for given property
	 * 
	 * @param object  the object with a desired property
	 * @param propertyName  name of the property to get
	 * @return  null if an exception occurs, otherwise the property
	 */
	public static Object getProperty (Object object, String propertyName) {
		if (object == null) {
			return null;
		}
		String[] words = propertyName.split("\\.");
		Object workingObject = object;
		if (words.length > 1) {
			for (String word : words) {
				workingObject = getProperty(workingObject, word);
			}
			return workingObject;
		}
		// length == 1
		words = propertyName.split("[\\[\\]]");
		if (words.length > 1) {
			for (String word : words) {
				try {
					int index = Integer.parseInt(word, 10);
					workingObject = workingObject.getClass().getMethod("get", Integer.TYPE).invoke(workingObject, index);	
				} catch (NumberFormatException e) {
					workingObject = getProperty(workingObject, word); 
				} catch (Exception e) {
					return null;
				}
			}
			return workingObject;
		}
		try {
			return object.getClass().getMethod(constructGetterName(propertyName)).invoke(object);
		} catch (Exception e) {
			return null;
		}
	}
	
	/** Convenience function that evaluates the property name up to the final period.
	 *  Ex. getParentProperty(object, obj.foo[2].bar) will return the 2nd foo item.
	 * 
	 * @param object object with the desired property
	 * @param propertyName name of the property
	 * @return  the parent object of the given property
	 */
	public static Object getParentProperty (Object object, String propertyName) {
		String parentPropertyName = propertyName.replaceFirst(".[^.]*$", "");
		return getProperty(object, parentPropertyName);
	}
	
	/** Gives the type of the property that is references by the given propertyName */
	public static Class<?> getTypeOfProperty(Class<?> workingClass, String propertyName) {
		String[] words = propertyName.split("\\.");
		if (words.length > 1) {
			for (String word : words) {
				workingClass = getTypeOfProperty(workingClass, word);
			}
			return workingClass;
		}
		// length == 1
		words = propertyName.split("[\\[\\]]");
		if (words.length > 1) {
			for (String word : words) {
				try {
					Integer.parseInt(word, 10);
					workingClass = workingClass.getTypeParameters()[0].getClass();	
				} catch (NumberFormatException e) {
					workingClass = getTypeOfProperty(workingClass, word); 
				} catch (Exception e) {
					return null;
				}
			}
			return workingClass;
		}
		try {
			return workingClass.getMethod(constructGetterName(propertyName)).getReturnType();
		} catch (Exception e) {
			return null;
		}
	}
	
	/** Tells whether the object is an instance of the given class 
	 *  Useful for taglibs, b/c instanceof is not (currently) provided in EL. 
	 */
	public static boolean instanceOf(Object object, Class<?> clazz) {
		try {
			if (clazz.isInstance(object)) {
				return true;
			}
			return false;
		} catch (Exception e) {
			return false; // assume false
		}
	}
	
	public static Field findField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
		Class <?> workingClass = clazz;
		while (workingClass != null) {
			try {
				return workingClass.getDeclaredField(fieldName);
			} catch (NoSuchFieldException e) {
				workingClass = workingClass.getSuperclass();
			}
		}
		throw new NoSuchFieldException();
	}
}
