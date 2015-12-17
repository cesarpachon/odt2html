/*
 * Created on 6/05/2010
 */
package co.edu.virtualhumboldt.html;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import co.edu.virtualhumboldt.util.FileUtils;
import co.edu.virtualhumboldt.util.StringUtils;
import edu.co.virtualhumboldt.exception.IncompleteInformationException;


/*
 * this class is the responsable of generate html files 
 */
public class HtmlGeneratorPaginated extends HtmlGenerator
{
	private String m_title; 
	private String m_template_path;
	private String m_output_path;
	private StringWriter m_writer;
	
	private String m_output_file;
	private int m_page_number;
	
	
	/**
	 * pages must be generated in output_path in the form pageXX.html
	 * taking template_path as base. the keywords to replace are:
	 * _CONTENT_
	 * _PAGE_
	 * 
	 * @param title
	 * @param template_path path to the template: full path including filename
	 * @param output_path path to output the pages: directory path, including last slash
	 */
	public HtmlGeneratorPaginated(String title, String template_path, String output_path)
	{
		m_title = title; 
		m_template_path = template_path;
		m_output_path = output_path;
	}
	//------------------------
	
	public Writer getWriter() throws IOException, IncompleteInformationException
	{
		if(m_writer == null)
		{
			System.out.println("HTMLGeneratorPaginated:getWriter: is null! open a default page");
			this.open("default000.html", 0);
		}	
		return m_writer;
	}
	
	/*
	 * create the file, open the writers streams and write the basic html header
	 */
	public void open(String output_file, int page_number) throws IOException, IncompleteInformationException
	{
		if(m_writer != null) close();
		
		m_output_file = output_file;
		m_page_number = page_number;
		
		/*m_writer = new BufferedWriter(new FileWriter(new File(m_path)));	*/
		m_writer = new StringWriter(1024);
		
	}
	//------------------------------
	
	
	
	
	
	
	
	
	
	/**
	 * 1. open the template file. load into a string.
	 * 2. replace the _CONTENT_ tag with the m_writer content.
	 * 3. replace the _PAGE_ tag with the m_page number field.
	 * 4. save the string to the output file.
	 * 5. set the m_writer to null. 
	 * @throws IncompleteInformationException 
	 */
	public void close() throws IOException, IncompleteInformationException
	{
		if(m_writer == null) 
		{
			 System.out.println("trying to close a non opened htmlGenerator!");
			 return;
		}
		String templateContent = FileUtils.getFileContent(m_template_path);
		
		String content = m_writer.toString();
		templateContent = templateContent.replace("_CONTENT_", content);
		templateContent = templateContent.replace("_PAGE_", ""+m_page_number);
		
		templateContent = StringUtils.replaceHTMLQuotes(templateContent);
		
		FileUtils.saveToFile(templateContent, m_output_path + m_output_file);
		
		m_writer = null;
	}
	//-------------------------

	public void closeDiv() throws IOException 
	{
		if(m_is_vineta)
		{
			m_wait_for_close_vineta = true;
		}
		else
		{
			m_writer.write("</div>\n");
		}
		
	}
	//-----------------------------

	public int getNumPages() 
	{
		return m_page_number+1;
	}


	

	

}
//---------------------------------------

