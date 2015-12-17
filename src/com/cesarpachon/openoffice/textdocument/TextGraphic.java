package co.edu.virtualhumboldt.openoffice.textdocument;

import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertySet;
import com.sun.star.graphic.XGraphic;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.text.XTextContent;
import com.sun.star.text.XTextRange;
import com.sun.star.uno.UnoRuntime;

/**
 * a wrapper over a openoffice graphic service and interfaces.
 * @author CESAR
 *
 */
public class TextGraphic 
{
	/**
	 * graphic service
	 */
	private Object m_oGraphicService;
	private XTextContent m_xTextContent;
	private XPropertySet m_xPropSet;
	private XGraphic m_xGraphic;
	private String m_name;
	
	public TextGraphic(String name, Object oGraphicService)
	{
		if(name == null) throw new NullPointerException("name of TextGraphic can not be null");
		m_name = name;
		m_oGraphicService = oGraphicService;
	}
	//-----------------------
	
	public String getName()
	{
		return m_name;
	}
	//------------------------
	
	public boolean equals(Object tg)
	{
		if(TextGraphic.class.isInstance(tg))
		{
			TextGraphic tgb = (TextGraphic)tg;
			return m_name.equals(tgb.getName());
		}
		else return false;
	}
	//----------------------
	
	public int hashCode()
	{
		return m_name.hashCode();
	}
	//-------------------------
	
	public XTextContent getXTextContent()
	{
		if(m_xTextContent == null)
		{
			m_xTextContent = UnoRuntime.queryInterface(XTextContent.class, m_oGraphicService);
		}
		return m_xTextContent;
	}
	//----------------------------
	

	public XPropertySet getXPropertySet()
	{
		if(m_xPropSet == null)
		{
			m_xPropSet = UnoRuntime.queryInterface(XPropertySet.class, getXTextContent());
		}
		return m_xPropSet;
	}
	//----------------------------
	
	public XGraphic getXGraphic() throws UnknownPropertyException, WrappedTargetException
	{
		if(m_xGraphic == null)
		{
			Object oGraphics = getXPropertySet().getPropertyValue("Graphic");
			m_xGraphic = UnoRuntime.queryInterface(XGraphic.class, oGraphics);
		}
		return m_xGraphic;
	}
	//----------------------------

	public XTextRange getAnchor() 
	{
		XTextContent xtextcontent = getXTextContent();
		XTextRange anchor = xtextcontent.getAnchor();
		return anchor;
	}
	//---------------------------

	public Integer getWidth() throws UnknownPropertyException, WrappedTargetException 
	{
		XPropertySet xPropSet = getXPropertySet();
		return (Integer)xPropSet.getPropertyValue("Width");
		
	}
	//----------------------------

	public Integer getHeight() throws UnknownPropertyException, WrappedTargetException 
	{
		XPropertySet xPropSet = getXPropertySet();
		return (Integer)xPropSet.getPropertyValue("Height");
	}
	//--------------------------------
	
	
}
