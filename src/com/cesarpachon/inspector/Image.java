/*
 * Created on 22/05/2010
 */
package co.edu.virtualhumboldt.inspector;

import java.io.IOException;

import co.edu.virtualhumboldt.html.HtmlGenerator;
import edu.co.virtualhumboldt.exception.IncompleteInformationException;

public class Image 
{
	String m_rotule;
	Integer m_number;
	String m_title;
	String m_page;
	
	
	public Image(String rotule, Integer number, String title, String page)
	{
		m_rotule = rotule;
		m_number = number;
		m_title = title;
		m_page = page;
	}
	//----------------------------


	public Integer getNumber() 
	{
		return m_number;
	}
	//-----------------------------

	
	public void dumpImageIndex(HtmlGenerator gen) throws IOException, IncompleteInformationException
	{
		gen.openParagraph();
		gen.write(m_rotule);
		gen.write(" ");
		gen.write(m_number.toString());
		gen.write(" ");
		gen.write(m_title);
		gen.write(" ");
		gen.writeHiperlink("(ver)", m_page+"#imagen_"+m_number);
		//gen.write(" ");
		gen.closeParagraph();
	}
	//-----------------------------

	
}
//----------------------------
