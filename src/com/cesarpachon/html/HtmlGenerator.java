/*
 * Created on 4/03/2011
 */
package co.edu.virtualhumboldt.html;

import java.io.IOException;
import java.io.Writer;

import edu.co.virtualhumboldt.exception.IncompleteInformationException;


public abstract class HtmlGenerator
{
	public static final String LISTA_VINETAS = "lista_vinetas";
	//public static final String VIDEO = "video";
	
	protected boolean m_is_vineta;
	protected boolean m_wait_for_close_vineta;

	public HtmlGenerator()
	{
		m_is_vineta = false;
		m_wait_for_close_vineta = false;
	}
	//-----------------------------------
	
	/**
	 * get the writer. each subclass would use a different type of writer.
	 * @throws IOException 
	 * @throws IncompleteInformationException 
	 */
	public abstract Writer getWriter() throws IOException, IncompleteInformationException;

	/**
	 * standard <hX> block, where X is the level passed as param: 1, 2, 3: h1, h2, h3
	 * @throws IncompleteInformationException 
	 */
	public void writeTitle(int level, String content) throws IOException, IncompleteInformationException
	{
		getWriter().write("<h"+level+">");
		getWriter().write(content);
		getWriter().write("</h"+level+">\n");
	}
	//------------------------------
	
	/**
	 * standard p block.
	 * @throws IncompleteInformationException 
	 */
	public void writeParagraph(String content) throws IOException, IncompleteInformationException
	{
		//if(!m_is_vineta)
		getWriter().write("<p>\n");
		if(m_is_vineta) getWriter().write("<li>");
		getWriter().write(content);
		if(m_is_vineta) getWriter().write("</li>");
		getWriter().write("</p>\n");
	}
	//------------------------------
	
	public void openDiv(String classname) throws IOException, IncompleteInformationException 
	{
		boolean generate_ul = false;
		boolean generate_no_ul = false;
		
		boolean first_vineta = false;
		
		
		/*if(VIDEO.equals(classname))
		{
			getWriter().write("<div class='video'>\n");
			
			
			
			getWriter().write("</div>\n");
			return;
		}*/
		
		if(LISTA_VINETAS.equals(classname))
		{
			if(!m_is_vineta)
			{
				m_is_vineta = true;
				first_vineta = true;
				generate_ul = true;
			}
		}
		else
		{
			m_is_vineta = false;
		}
		
		if(m_wait_for_close_vineta && !m_is_vineta)
		{
			m_wait_for_close_vineta = false;
			generate_no_ul = true;
		}

		if(generate_ul){
			getWriter().write("<div class=\""+classname+"\">\n");
			getWriter().write("<ul>\n");
		}
		if(generate_no_ul) getWriter().write("</ul></div>\n");

		
		if(!m_is_vineta || (m_is_vineta && first_vineta))
		{
			if(!generate_ul)
				getWriter().write("<div class=\""+classname+"\">\n");
		}
		
	}
	//-----------------------------
	
	public void closeDiv() throws IOException, IncompleteInformationException 
	{
		if(m_is_vineta)
		{
			m_wait_for_close_vineta = true;
		}
		else
		{
			getWriter().write("</div>\n");
		}
		
	}
	//-----------------------------

	public void openSpan(String classname) throws IOException, IncompleteInformationException 
	{
		getWriter().write("<span class=\""+classname+"\">");
	}
	//-----------------------------
	
	public void closeSpan() throws IOException, IncompleteInformationException 
	{
		getWriter().write("</span>");
	}
	//-----------------------------

	public void openParagraph() throws IOException, IncompleteInformationException 
	{
		if(m_is_vineta)
			getWriter().write("<li>\n");
		else
			getWriter().write("<p>\n");
	}
	//--------------------------------------
	
	public void closeParagraph() throws IOException, IncompleteInformationException 
	{
		if(m_is_vineta)
			getWriter().write("</li>\n");
		else
			getWriter().write("</p>\n");
	}
	//--------------------------------------

	public void writeAnchor(String anchorName) throws IOException, IncompleteInformationException 
	{
		getWriter().write("<a name=\""+anchorName+"\"></a>");
	}
	//--------------------------------------

	public void writeHiperlink(String name, String url) throws IOException, IncompleteInformationException 
	{
		getWriter().write("<a href=\""+url+"\">"+name+"</a>");
	}
	//--------------------------------------

	public void writeHorizontalLine() throws IOException, IncompleteInformationException 
	{
		getWriter().write("<hr>\n");
	}
	//--------------------------------------

	/**
	 * just outs the text
	 * @throws IncompleteInformationException 
	 */
	public void write(String content) throws IOException, IncompleteInformationException
	{
		getWriter().write(content);
	}
	//------------------------------
	
	public void writeImage(String id, String m_images_subdirectory, String m_imageprefix, int widthpx, int heightpx) throws IOException, IncompleteInformationException 
	{
		String imagename = m_imageprefix + id + ".jpg";
		writeImage(imagename, m_images_subdirectory, widthpx, heightpx);
	}

	public void writeImage(String imagename, String m_images_subdirectory, int widthpx, int heightpx) throws IOException, IncompleteInformationException 
	{
		String path =m_images_subdirectory;
		if(path.startsWith("\\"))
		{
			path = path.substring(1);
		}
		//m_images_subdirectory.replaceFirst("\\", "");
		path = path.replace("\\", "/");
		path += imagename; 
		
		getWriter().write("<img src='");
		getWriter().write(path);
		getWriter().write("' onclick='parent.showImage(\""+path+"\");' ");
		getWriter().write(" width='");
		getWriter().write(Integer.toString(widthpx));
		getWriter().write("px' height='");
		getWriter().write(Integer.toString(heightpx));
		getWriter().write("px' ");
		getWriter().write("></img>\n");
	}
	//---------------------------------

	public void writeTable(String id) throws IOException, IncompleteInformationException 
	{
		getWriter().write("<span ");
		getWriter().write(" onclick='window.open(\"tabla"+id+".html\");' >");
		getWriter().write("<img src='img/icon_tabla.png' alt='click para ver la tabla' />");
		getWriter().write("<br>");
		getWriter().write("Click para ver la tabla");
		getWriter().write("</span>\n");
	}
	//---------------------------------

	
	
	/**
	 * expected link in the form: 		
	 * http://www.youtube.com/watch?v=R3-OcZF8-Fc&feature=fvw
	 * output in the form (notice that url must be reformated)
	 * <iframe title="YouTube video player" width="480" height="390" src="http://www.youtube.com/embed/R3-OcZF8-Fc" frameborder="0" allowfullscreen></iframe>
	 * @throws IOException 
	 * @throws IncompleteInformationException 
	 */
	public void writeYoutubeIframe(String link) throws IOException, IncompleteInformationException
	{
		link = link.replace("watch?v=", "embed/");
		
		int index = link.indexOf("&feature=");
		if(index > -1)
		{
			link = link.substring(0, index);
		}
		
		
		getWriter().write("<iframe title=\"YouTube video player\" width=\"480\" height=\"390\" src=\"");
		getWriter().write(link);
		getWriter().write("\" frameborder=\"0\" allowfullscreen></iframe>");
	}
	//------------------------------------
	
	
	public void openTable() throws IOException, IncompleteInformationException 
	{
		getWriter().write("<table border='1'>");
	}
	//------------------------------

	public void closeTable() throws IOException, IncompleteInformationException 
	{
		getWriter().write("</table>");
	}
	//------------------------------
	
	public void openRow() throws IOException, IncompleteInformationException 
	{
		getWriter().write("<tr>");
	}
	//------------------------------

	public void closeRow() throws IOException, IncompleteInformationException 
	{
		getWriter().write("</tr>");
	}
	//------------------------------

	public void openCell() throws IOException, IncompleteInformationException 
	{
		getWriter().write("<td>");
	}
	//------------------------------

	public void closeCell() throws IOException, IncompleteInformationException 
	{
		getWriter().write("</td>");
	}
	//------------------------------
}
