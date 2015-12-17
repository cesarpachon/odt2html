/*
 * Created on 22/05/2010
 */
package co.edu.virtualhumboldt.inspector;

import java.io.IOException;

import co.edu.virtualhumboldt.html.HtmlGenerator;
import co.edu.virtualhumboldt.html.HtmlGeneratorSimple;
import edu.co.virtualhumboldt.exception.IncompleteInformationException;

public class Table 
{
	String m_rotule;
	Integer m_number;
	String m_title;
	String m_page;
	
	
	public Table(String rotule, Integer number, String title, String page)
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

	
	public void dumpTableIndex(HtmlGeneratorSimple gen) throws IOException, IncompleteInformationException
	{
		gen.openParagraph();
		gen.write(m_rotule);
		gen.write(" ");
		gen.write(m_number.toString());
		gen.write(" ");
		gen.write(m_title);
		gen.write(" ");
		gen.writeHiperlink("(ver)", m_page+"#table_"+m_number);
		//gen.write(" ");
		gen.closeParagraph();
	}
	//-----------------------------

	
}
//----------------------------
