/*
 * Created on 17/03/2011
 */
package com.cesarpachon.openoffice;

import com.sun.star.beans.Property;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertySet;
import com.sun.star.beans.XPropertySetInfo;
import com.sun.star.lang.WrappedTargetException;

public abstract class OpenOfficeUtils 
{

	public static void dumpPropertySet(XPropertySet propertySet) throws UnknownPropertyException, WrappedTargetException 
	{
		XPropertySetInfo setinfo = propertySet.getPropertySetInfo();
		Property[] properties = setinfo.getProperties();
		for(Property prop:properties)
		{
			System.out.println("property found: "+prop.Name + "= "+propertySet.getPropertyValue(prop.Name));
		}	
	}
	
}
