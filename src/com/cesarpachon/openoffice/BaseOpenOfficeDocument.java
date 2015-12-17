/*
 * Created on 8/03/2011
 */
package co.edu.virtualhumboldt.openoffice;

import java.io.FileOutputStream;
import java.io.IOException;


import com.sun.star.beans.Property;
import com.sun.star.beans.PropertyValue;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertySet;
import com.sun.star.bridge.UnoUrlResolver;
import com.sun.star.bridge.XUnoUrlResolver;
import com.sun.star.comp.helper.Bootstrap;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.graphic.XGraphic;
import com.sun.star.graphic.XGraphicProvider;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.lang.XServiceInfo;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;

public class BaseOpenOfficeDocument 
{
	
	protected static XComponentContext m_oComponentContext = null;
	
	//servicio desktop, y sus interfaces
	protected static Object m_oDesktop = null; 
	protected static XComponentLoader m_xComponentLoader = null; 
	
	
	
	
	
	
	public static void initConnection() throws Exception 
	{
		if(m_xComponentLoader != null)
		{
			throw new Exception("Connection already initialized!!");
		}
		
		 XComponentContext xcomponentcontext = Bootstrap.createInitialComponentContext(null);
		 
		  // create a connector, so that it can contact the office
		  XUnoUrlResolver urlResolver = UnoUrlResolver.create(xcomponentcontext);
		 
		  Object initialObject = urlResolver.resolve("uno:socket,host=localhost,port=8100;urp;StarOffice.ServiceManager");
		 
		  XMultiComponentFactory xOfficeFactory = (XMultiComponentFactory) UnoRuntime.queryInterface(
		      XMultiComponentFactory.class, initialObject);
		 
		  // retrieve the component context as property (it is not yet exported from the office)
		  // Query for the XPropertySet interface.
		  XPropertySet xProperySet = (XPropertySet) UnoRuntime.queryInterface( 
		      XPropertySet.class, xOfficeFactory);
		 
		  // Get the default context from the office server.
		  Object oDefaultContext = xProperySet.getPropertyValue("DefaultContext");
		 
		  // Query for the interface XComponentContext.
		  m_oComponentContext = (XComponentContext) UnoRuntime.queryInterface(XComponentContext.class, oDefaultContext);
		 
		  // now create the desktop service
		  // NOTE: use the office component context here!
		  m_oDesktop = xOfficeFactory.createInstanceWithContext("com.sun.star.frame.Desktop", m_oComponentContext);
	
		  m_xComponentLoader = (XComponentLoader)
          UnoRuntime.queryInterface(XComponentLoader.class,m_oDesktop);
		 
	}
	//--------------------------
	
	public BaseOpenOfficeDocument() throws Exception
	{
		if(m_xComponentLoader == null)
		{
			initConnection();
		}
	}
	//---------------------------------
	
	
	
   /*
    * creates a new PropertyValue array, with a size of one,
    * and a single PropertyValue instance with the given values.	
    */
   public PropertyValue[] makePropertyValue(String name, Object value)
   {
	   PropertyValue[] properties = new PropertyValue[1];
	   properties[0] = new PropertyValue();
	   properties[0].Name = name;
	   properties[0].Value = value;
	   return properties;
   }
   //-------------------------------------------------------------
   
   
   public void DumpPropertySet(XPropertySet xPropSet) throws UnknownPropertyException, WrappedTargetException
   {
	   System.out.println("DumpPropertySet begins: ---------------");

	   	Property[] props = xPropSet.getPropertySetInfo().getProperties();
	   	for(Property prop:props)
	   	{
	   		System.out.print(prop.Name + ":");
	   		System.out.print("type: " + prop.Type);
	  
	   		
	   		System.out.print(" value:");

	   		//this is a known bug.. access to this property raise a runtime exception
	   		if("ParaChapterNumberingLevel".equals(prop.Name))
	   			continue;

	   		try{
	   			System.out.println(xPropSet.getPropertyValue(prop.Name));
	   		}catch(Error e)
	   		{
	   			System.out.println("ERROR:"+e);
	   		}
	   	}
	   
	   System.out.println("DumpPropertySet ends ---------------");
   }
   //-------------------------------------------------------------
   
   /**
    * save the current xGraphic object to the given path
 * @param height 
 * @param width 
 * @throws com.sun.star.uno.Exception 
 * @throws IOException 
    */
   public void exportGraphicToLocalFile(XGraphic xGraphic, String url) 
   throws com.sun.star.uno.Exception, IOException
   {
	   System.out.println("BaseOpenOfficeDocument:ExportGraphic: init exporting to "+ url);
	   XMultiComponentFactory xMCF = m_oComponentContext.getServiceManager();
	   Object graphicProviderObject = xMCF.createInstanceWithContext("com.sun.star.graphic.GraphicProvider", m_oComponentContext);
	   XGraphicProvider xGraphicProvider = (XGraphicProvider) 
	   			UnoRuntime.queryInterface(XGraphicProvider.class, graphicProviderObject);

	   PropertyValue[] properties = new PropertyValue[2];
	   properties[0] = new PropertyValue();
	   properties[0].Name = "MimeType";
	   properties[0].Value = "image/jpeg";
	   
	   /*properties[1] = new PropertyValue();
	   properties[1].Name = "URL";
	   properties[1].Value = url;
	   xGraphicProvider.storeGraphic(xGraphic, properties);
	   */
	   
	   ByteArrayXStream xTarget = new ByteArrayXStream(); 
	   properties[1] = new PropertyValue();
	   properties[1].Name = "OutputStream";
	   properties[1].Value = xTarget;
	   xGraphicProvider.storeGraphic(xGraphic, properties);
	// Close the output and return the result
	   xTarget.closeOutput();
	   xTarget.flush();

	   byte[] result = xTarget.getBuffer(); 
	   
	   FileOutputStream fos = new FileOutputStream(url);
	   fos.write(result);
	   fos.flush();
	   fos.close();
	   
	   System.out.println("BaseOpenOfficeDocument:ExportGraphic: export ends");
	   
   }
   //-------------------------------------------------------------
   
   
   public static void dumpSupportedServiceNames(Object service)
   {
	   XServiceInfo xInfo = (XServiceInfo) UnoRuntime.queryInterface(
               XServiceInfo.class, service);
	   
	   String names[] = xInfo.getSupportedServiceNames();
  	 //if (xInfo.supportsService("com.sun.star.text.TextTable")) 
	   
	   System.out.println("BaseOpenOfficeDocument:dumpSupportedServiceNames:");
	   System.out.println(" service implementation name: "+ xInfo.getImplementationName());
	   for(String name:names)
	   {
		   System.out.println("- "+ name);
	   }
	   
   }
   //--------------------------------------------------------					
	
	
}
