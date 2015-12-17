/*
 * Created on 22/05/2010
 */
package co.edu.virtualhumboldt.inspector;

import java.io.IOException;
import java.util.LinkedList;

import co.edu.virtualhumboldt.html.HtmlGenerator;
import co.edu.virtualhumboldt.html.HtmlGeneratorSimple;
import edu.co.virtualhumboldt.exception.IncompleteInformationException;

public class Title 
{
	public static int g_titleCount = 0;
	
	private String m_name;
	private Integer m_level;
	private String m_anchorName;
	//the page where the title is 
	private String m_page;
	
	private LinkedList<Title> m_children; 
	
	public Title(String name, Integer level, String page)
	{
		this.m_page = page;
		this.m_name = name;
		this.m_level = level;
		this.m_anchorName = "title_"+ Integer.toString(g_titleCount++);
		m_children = new LinkedList<Title>();
	}
	//---------------------------------------
	
	public void addChildren(Title child)
	{
		m_children.add(child);
	}
	//----------------------------------
	
	
	public void dumpTableOfContents(HtmlGeneratorSimple gen) throws IOException, IncompleteInformationException
	{
		gen.openDiv("normal");
		gen.openParagraph();
		gen.write("["+m_level.toString()+"]");
		for(int i=0; i<m_level.intValue(); i++)
		{
			gen.write("--*");
		}
		gen.write(m_name);
		gen.writeHiperlink("(ver)", this.m_page+"#"+getAnchorName());
		
		
		//validate common errors: starting with numbers, finishing with dots.
		//if(m_name.matches("[0-9][a-zA-Z][0-9a-zA-Z]*"))
		
		if(m_name.length()>0)
		{
			String prefix = Character.toString(m_name.charAt(0));
			if(prefix.matches("[0-9]"))
			{
				gen.openSpan("error");
				gen.write("error: los titulos no deben llevar numeración manual");
				gen.closeSpan();
			}
		}
		else
		{
			gen.openSpan("error");
			gen.write("error: se ha marcado un espacio vacío como título.");
			gen.closeSpan();
			
		}
		
	
		if(m_name.endsWith("."))
		{
			gen.openSpan("error");
			gen.write("error: los titulos no deben terminar con punto");
			gen.closeSpan();
		}
		gen.closeParagraph();
		gen.closeDiv();
		
		for(Title child:this.m_children)
		{
			child.dumpTableOfContents(gen);
		}
		
	}
	//------------------------------------------------

	public boolean isBrother(Title mLastAddedTitle) 
	{
		return m_level.compareTo(mLastAddedTitle.m_level) == 0;
	}
	//------------------------------------------------

	public String getAnchorName() 
	{
		return m_anchorName;
	}
	//------------------------------------------------

	/**
	 * ORIGINAL TOC VERSION, just a plain list of hiperlinks (with styles)
	 * @return
	 */
	public String getTableOfContents0() 
	{
		StringBuffer buff = new StringBuffer();
		
		//dont dump level zero
		if(m_level.intValue() > 0)
		{
			buff.append("<div class=\'toc_level_");
			buff.append(m_level.toString());
			buff.append("\'>");
			buff.append("<a href='#' ");
			buff.append(" onclick=\"showContent('");
			String page = m_page.replace(".html", "");
			buff.append(page);
			buff.append("');\">");
			buff.append(this.m_name);
			buff.append("</a>");
			buff.append("</div>\n");
		}
		
		for(Title child:this.m_children)
		{
			buff.append(child.getTableOfContents());
		}
		return buff.toString();
	}
	//----------------------------
	
	/**
	 * new version of table of contents. 
	 * it exports as anidated UL LI lists:
	 * <ul id="nav">
		<!--toc_section_intro-->
		<li><a id="content00"    onclick="showContent('content00');">Intro</a>
		<ul>
			<!--<li><a id="content00"  onclick="showContent('content00');">Title </a></li>-->
			<li><a id="content001" onclick="showContent('content001');">Welcome</a></li>
			<li><a id="content01"  onclick="showContent('content01');">Introduction to the Course </a></li>
			<li><a id="content02"  onclick="showContent('content02');">Meet the Author </a></li>
		</ul>
	</li>
	</ul>
	 * @return
	 */
	public String getTableOfContents() 
	{
		StringBuffer buff = new StringBuffer();


		if(m_level.intValue() > 0)
		{

		String page = m_page.replace(".html", "");
		
		//<li><a id="content00"    onclick="showContent('content00');">Intro</a>
		buff.append("<li><a id='");
		buff.append(page);
		buff.append("' onclick=\"showContent('");
		buff.append(page);
		buff.append("');\">");
		buff.append(this.m_name);
		buff.append("</a>\n");
		}
		
		if(this.m_children.size() >0)
		{
			
			if(m_level.intValue() > 0)
					buff.append("<ul>\n");
			
			for(Title child:this.m_children)
			{
				buff.append(child.getTableOfContents());
			}
			
			if(m_level.intValue() > 0)
				buff.append("</ul>\n");
			
		}
		buff.append("</li>\n");
		return buff.toString();
	}
	//----------------------------
	
	/**
	 * outputs something like this:
	 * parent.g_toc_manager.addItem("content00", "content00");  
		parent.g_toc_manager.addItem("content00", "content001");  
		parent.g_toc_manager.addItem("content00", "content01");  
		parent.g_toc_manager.addItem("content00", "content02");  
	 * 
	 */
	public String getTableOfContentsJS(String parent) 
	{
		StringBuffer buff = new StringBuffer();


		if(m_level.intValue() > 0)
		{

		String page = m_page.replace(".html", "");
		
		buff.append("parent.g_toc_manager.addItem('");
		buff.append(page);
		buff.append("', '");
		buff.append(page);
		buff.append("');\n");
		}
		
		
		
		if(this.m_children.size() >0)
		{
			
			if(parent == null)
				parent = m_name;
			
			for(Title child:this.m_children)
			{
				buff.append(child.getTableOfContentsJS(parent));
			}
			
			
		}
		return buff.toString();
	}
	//----------------------------

	

}
