/*
 * Created on 21/05/2010
 */
package com.cesarpachon.openoffice.textdocument;

/*
 * this is a block of text who has the same style. 
 */
public class TextPortion 
{
	private String m_content;
	private String m_charStyle;
	
	public TextPortion(){};
	
	public TextPortion(String charStyleName, String content)
	{
		setContent(content);
		setCharStyleName(charStyleName);
	}
	//---------------------

	public void setCharStyleName(String charStyleName) 
	{
		m_charStyle = charStyleName;
	}
	//---------------------

	public void setContent(String content)
	{
		m_content = content;
	}
	//---------------------
	
	public String getContent()
	{
		return m_content;
	}
	//---------------------------
	
	public String getCharStyleName()
	{
		return m_charStyle;
	}
	//---------------------------
}
