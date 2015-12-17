/*
 * Created on 6/05/2010
 */
package com.cesarpachon.html;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;


/*
 * this class is the responsable of generate html files 
 */
public class HtmlGeneratorSimple extends HtmlGenerator
{
	private String m_title; 
	private String m_path;
	private BufferedWriter m_writer;
	
	
	public HtmlGeneratorSimple(String title, String path)
	{
		m_title = title; 
		m_path = path;
	}
	//------------------------
	
	public Writer getWriter(){return m_writer;}

	
	/*
	 * create the file, open the writers streams and write the basic html header
	 */
	public void open() throws IOException
	{
		m_writer = new BufferedWriter(new FileWriter(new File(m_path)));	
		m_writer.write("<html>\n");
		m_writer.write("<header>\n");
		m_writer.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charsetISO 8859-1\" />\n");
		m_writer.write("<link href=\"file:///C://repositorios/referencia/implementacion/scorm/style.css\" rel=\"stylesheet\" type=\"text/css\" />\n");

		m_writer.write("</header>\n");
		m_writer.write("<body>\n");
	}
	//------------------------------
	
	
	
	
	
	/*
	 * closes the html and body tags, close the streams.
	 */
	public void close() throws IOException
	{
		if(m_writer == null) 
			{
			 System.out.println("trying to close a non opened htmlGenerator!");
			 return;
			} 
		m_writer.write("</body>\n");
		m_writer.write("</html>\n");
		m_writer.close();
	}
	//-------------------------


	
	
}
//---------------------------------------

