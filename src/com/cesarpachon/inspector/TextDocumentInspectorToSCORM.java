/*
 * Created on 6/05/2010
 */
package com.cesarpachon.inspector;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import com.cesarpachon.html.HtmlGeneratorPaginated;
import com.cesarpachon.html.HtmlGeneratorSimple;
import com.cesarpachon.openoffice.textdocument.TextDocument;
import com.cesarpachon.openoffice.textdocument.TextGraphic;
import com.cesarpachon.openoffice.textdocument.TextPortion;
import com.cesarpachon.services.RepositoryTO;
import com.cesarpachon.util.FileUtils;
import com.cesarpachon.util.LogConsole;
import com.cesarpachon.util.StringUtils;

import com.sun.star.beans.XPropertySet;
import com.sun.star.graphic.XGraphic;
import com.sun.star.style.XStyle;
import com.sun.star.uno.UnoRuntime;

import com.cesarpachon.exception.IncompleteInformationException;

public class TextDocumentInspectorToSCORM extends TextDocumentInspectorBase 
{

	
	
	TextDocument m_document;
	HtmlGeneratorSimple m_htmlErrorReporter;
	HtmlGeneratorPaginated m_htmlContent;
	
		
	Title m_root;
	Title m_current_title;
	Title m_last_added_title;
	
	
	//pagination manager
	int m_currPage = 1;
	String m_currPageName = "content.html";

	
	private String m_pageprefix;


	private String m_images_subdirectory;

	private String m_imageprefix;

	
	private RepositoryTO m_repository;
	
	public TextDocumentInspectorToSCORM(TextDocument document, 
			RepositoryTO repository
			/*HtmlGeneratorSimple errorReporter, 
			String contentDirectory,  String imagesSubDirectory,
			String template_path,String toc_path,  String index_path,
			String pageprefix, String imageprefix, String unitid*/)
	{
		
		m_repository = repository;
		
		/*String imagesSubDirectory = "\\img\\";
		String index_path = m_repository.getIndexPath();
		String errorReport = m_repository.getErrorReportPath();
		String contentDirectory = m_repository.getScormDirectoryUnidad();
		String manifest_file= m_repository.getManifestTemplateUnidad();
		*/
		
		
		m_currPage = -1; 
		m_document = document;
		m_htmlErrorReporter = new HtmlGeneratorSimple("Reporte de errores", m_repository.getErrorReportPath());

		m_images_subdirectory = "\\img\\";
		m_pageprefix = "content";
		m_imageprefix = "image";
		
	}
	//--------------------------------
	
	private void openPage() throws IOException, IncompleteInformationException
	{
		m_currPage++;
		if(m_currPage<100)
		{
			if(m_currPage < 10)
			{
				m_currPageName = m_pageprefix + "0"+m_currPage+".html";
			}
			else
			{
				m_currPageName=m_pageprefix + m_currPage+".html";
			}
		}
		else
		{
			m_currPageName = m_pageprefix +m_currPage+".html";
		}
		
		m_htmlContent.open(m_currPageName, m_currPage);
		
	}
	//----------------------------------
	
	public void beginAntyPiracyInspection() throws Exception
	{
		m_htmlErrorReporter.writeTitle(2, "revisión anti-plagios");
		m_htmlErrorReporter.writeParagraph("los siguientes párrafos necesitan verificar su originalidad o que hayan sido citados adecuadamente:");
		m_document.visitAllPlainParagraphs(this);
		
	}
	//----------------------------------
	
	
	public void beginInspection() throws Exception
	{
		m_htmlErrorReporter.open();
		m_htmlErrorReporter.openDiv("bloque1");

		m_htmlErrorReporter.writeTitle(1, "Reporte de inspección");

		//1. inspect general styles list
		m_htmlErrorReporter.writeTitle(2, "1: revisión de estilos usados");
		
		
		m_htmlErrorReporter.writeParagraph("la lista de estilos aceptados es: ");
		String styles_lst ="";
		for(String style:g_validStylesNames.keySet())
		{
			styles_lst += style + ",";
		}
		m_htmlErrorReporter.writeParagraph(styles_lst);
		
		m_htmlErrorReporter.writeParagraph("la lista de estilos encontrados en el documento es: ");
		m_document.visitUsedStyles(this);
		
		
		
	}
	//---------------------------------
	
	public void end() throws IOException, IncompleteInformationException
	{
		m_htmlErrorReporter.closeDiv();
		m_htmlErrorReporter.close();
		m_htmlContent.close();
		m_document.close();
	}
	//---------------------------------

	@Override
	public void onUsedStyle(String styleName, XStyle styleSrv) throws Exception
	{
		//check if style is in the g_validStylesNames list. 
		if(g_validStylesNames.containsKey(styleName))
		{
			m_htmlErrorReporter.writeParagraph("<b>"+styleName+":</b> OK");
		}
		else
		{
			if(styleName != null && styleName.length()>2 && !styleName.startsWith("Heading"))
			{
				String lastChar = Character.toString(styleName.charAt(styleName.length()-1));
				if(lastChar.matches("[0-9]"))
				{
					styleName = styleName.substring(0, styleName.length()-1);
				}
			}
			if(g_validStylesNames.containsKey(styleName) || g_specialStylesNames.containsKey(styleName))
			{
				m_htmlErrorReporter.writeParagraph("<b>"+styleName+":</b> OK");
			}
			else
			{
				m_htmlErrorReporter.writeParagraph("<b>"+styleName+":</b> ERROR: no es un estilo válido.");
			}
		}
	}
	//-----------------------------------
	
	public void beginContentGeneration() throws Exception
	{
		
		//openPage();
		m_htmlContent = new HtmlGeneratorPaginated("Contenido generado", m_repository.getTemplatePath(), m_repository.getScormDirectoryUnidad());
		//m_htmlContent.open();
		//m_htmlContent.openDiv("bloque1");
		
		m_document.visitAllParagraphs(this);
		
		//report structure
		m_htmlErrorReporter.writeTitle(2, "Estructura del documento:");
		if(m_root != null)
		{
			m_root.dumpTableOfContents(m_htmlErrorReporter);
		}
		
		m_htmlErrorReporter.writeTitle(2, "Lista de tablas:");
		for(Table table:m_tables.values())
		{
			table.dumpTableIndex(m_htmlErrorReporter);
		}
		
		m_htmlErrorReporter.writeTitle(2, "Lista de Imágenes:");
		for(Image imagen:m_images.values())
		{
			imagen.dumpImageIndex(m_htmlErrorReporter);
		}
		
		//open the toc template, and fill it with the structure.
		LogConsole.getInstance().log("beginContentGeneration: openning m_toc_path: "+ m_repository.getTocPath());
		String toc_template = FileUtils.getFileContent(m_repository.getTocPath());
		

		String toc_text = m_root.getTableOfContents();
		toc_text = StringUtils.replaceHTMLQuotes(toc_text);
		toc_template = toc_template.replaceAll("_TOC_", toc_text);
		
		//now, the JSTOC
		toc_text = m_root.getTableOfContentsJS(null);
		//toc_text = StringUtils.replaceHTMLQuotes(toc_text);
		toc_template = toc_template.replaceAll("_TOCJS_", toc_text);
		
		
		FileUtils.saveToFile(toc_template, m_repository.getScormDirectoryUnidad() + "toc.html");
		
		
		//open the index_template, replace the token words and create a new index.html file
		LogConsole.getInstance().log("beginContentGeneration: openning m_index_path: "+ m_repository.getIndexPath());
		String index_text = FileUtils.getFileContent(m_repository.getIndexPath());
		index_text = index_text.replace("_CONTENT_NUM_PAGES_", m_htmlContent.getNumPages()-1+"");
		index_text = index_text.replace("_UNIT_ID_", m_repository.getCurrentUnit());
		index_text = StringUtils.replaceHTMLQuotes(index_text);
		FileUtils.saveToFile(index_text, m_repository.getScormDirectoryUnidad() + "index.html");
		
		exportTablesPagesAndPics();
		
		
	}
	//--------------------------------------------
	
	/*
	 * iterate over the m_tables collection, for each item, it will try to open 
	 * a odt document called "id".odt, in the "tables" subfolder. 
	 * if the document exist, it will export it as html, in the form "tableID.html"
	 * and also it will export a tableID.png pic. 
	 */
	private void exportTablesPagesAndPics() throws Exception
	{
		for(Table table:m_tables.values())
		{
			LogConsole.getInstance().log("exporting table " + table.getNumber());
			String urlTableODT = m_repository.getUrlTableSrc(table.getNumber());
			
			LogConsole.getInstance().log("opening table doc: "+ urlTableODT);
			TextDocument doc = new TextDocument(urlTableODT);
			
			doc.exportAsHTML(m_repository.getUrlTableHtml(table.getNumber()));
			
			doc.close();
		}
	}
	//-----------------
	
	private String dumpParagraphHtmlContent(Collection<TextPortion> portions) throws IOException, IncompleteInformationException 
	{
		StringBuffer buff = new StringBuffer();
		
		for(TextPortion portion:portions)
		{
			String charStyleName = portion.getCharStyleName();
			
			//discard some charStylenames..
			if(charStyleName!= null && (charStyleName.equals("Fuente de párrafo predeter.") ||
					charStyleName.equals("Bullet Symbols") ||  
					charStyleName.equals("Internet link")))
			{
				charStyleName = null;
			}
			
			if(charStyleName != null)
			{
				//this is a specially formated portion. homologate style name. 

				String htmlStyleName = "error";
				if(g_validCharStylesNames.containsKey(portion.getCharStyleName()))
				{
					//get the html style name from the hash table
					htmlStyleName = g_validCharStylesNames.get(portion.getCharStyleName());
					m_htmlContent.openSpan(htmlStyleName);
					buff.append(portion.getContent());
					m_htmlContent.write(portion.getContent());
					m_htmlContent.closeSpan();
				}
				else
				{
					m_htmlContent.openSpan(htmlStyleName);
					m_htmlContent.write("{estilo invalido de caracter: "+portion.getCharStyleName()+"}");
					buff.append(portion.getContent());
					m_htmlContent.write(portion.getContent());
					m_htmlContent.closeSpan();
				}
				

			}
			else
			{	
				//this is a normal portion. just dump it out. 
				m_htmlContent.write(portion.getContent());
				buff.append(portion.getContent());
				
			}
		}
		
		String text = buff.toString();
		return text;
	}
	//------------------------------------------------------

	
	private void onWordTableOrImage(Collection<TextPortion> portions) throws Exception
	{
		if(portions.size()==0)
		{
			String msg = "IGNORING TABLE OR IMAGE: TextDocumentInspect:onWordTableOrImage: portions.size == 0";
			System.out.println(msg);
			LogConsole.getInstance().log(msg);
			return;
			//throw new IncompleteInformationException(msg);
			
		}
		if(portions.size() < 2)
		{
			//mmhh.. maybe a embed image?? 
			Iterator<TextPortion> iter = portions.iterator();
			TextPortion portion = iter.next();
			
			System.out.println("one size "+portions.size()+" portion content: "+portion.getContent());
			
			System.out.println("UGLY HACK!! parsing the string to extract image id..");
			String content = portion.getContent();
			if(content.startsWith("Imagen "))
			{
				int index = content.indexOf(":");
				if(index > -1)
				{
					String title = content.substring(index+1);
					String id = content.substring(7, index);
					onImage("Imagen", id, title);
				}
			}
			
			/*
			m_htmlContent.openDiv("error");
			m_htmlContent.writeParagraph("el epigrafe tiene un numero incorrecto de campos, se esperaban 3: "+portions.size());
			dumpParagraphHtmlContent(portions);
			m_htmlContent.closeDiv();
			m_htmlErrorReporter.writeParagraph("se encontró una epigrafe con un número incorrecto de campos, se esperaban 3: "+portions.size());
			for(TextPortion portion:portions)
			{
				m_htmlErrorReporter.writeParagraph("estilo: "+ portion.getCharStyleName()+ " contenido: "+ portion.getContent());
			}
			*/
			return;
		}
		//else
		//{
			Iterator<TextPortion> iter = portions.iterator();
			String rotule = iter.next().getContent();
			//String number = iter.next().getContent();
			//String title = iter.next().getContent();
			
			rotule = rotule.trim();
			
			if(rotule.equals("Tabla"))
			{
				onTable(portions);
			}
			else if(rotule.equals("Imagen") || rotule.equals("Image"))
			{
				
				//String rotule = iter.next().getContent();
				String number = iter.next().getContent();
				String title = iter.next().getContent();

				onImage(rotule, number, title);
			}
			else
			{
				m_htmlContent.openDiv("error");
				m_htmlContent.writeParagraph("el epigrafe tiene un rotulo desconocido: "+rotule);
				dumpParagraphHtmlContent(portions);
				m_htmlContent.closeDiv();
				m_htmlErrorReporter.writeParagraph("se encontró una epigrafe con un rotulo desconocido: "+rotule);
		
			}
			
		//}
	}
	//---------------------------------
	
	/*
	 * when a table is found, it comes with three portions: 
	 * portion 0 is the rotule name "Tabla".
	 * portion 1 is number of the table. 
	 * portion 2 is the title of the table. 
	 */
	private void onTable(Collection<TextPortion> portions) throws Exception
	{
		if(portions.size() != 3)
		{
			m_htmlContent.openDiv("error");
			m_htmlContent.writeParagraph("la tabla tiene un numero incorrecto de campos, se esperaban 3: "+portions.size());
			dumpParagraphHtmlContent(portions);
			m_htmlContent.closeDiv();
			m_htmlErrorReporter.writeParagraph("se encontró una tabla con un número incorrecto de campos, se esperaban 3: "+portions.size());
			return;
		}
		
		Iterator<TextPortion> iter = portions.iterator();
		String rotule = iter.next().getContent();
		String number = iter.next().getContent();
		String title = iter.next().getContent();
		
		
		
		m_htmlContent.openDiv("tabla");
		m_htmlContent.writeAnchor("table_"+number);
		
		m_htmlContent.writeTable(number);
		
		
		/*TABLAS*/
		m_htmlContent.writeParagraph(rotule + " "+ number + ": "+ title);
		
		/*
	    m_htmlContent.writeParagraph("rotulo:"+rotule);
		m_htmlContent.writeParagraph("numero:"+number);
		m_htmlContent.writeParagraph("titulo:"+title);
		*/
		
		//new way.. just a link to popup the html table in other window..
		
		m_htmlContent.closeDiv();
		
		Table table = new Table(rotule, Integer.parseInt(number), title, m_currPageName);
		m_tables.put(table.getNumber(), table);
		
		
	}
	//------------------------------------------------------
	
	
	private void onImage(String rotule, String number, String title) throws Exception
	{
		
		XPropertySet xPropSet = m_document.exportImage(number, this.m_repository.getScormDirectoryUnidad() + m_images_subdirectory, m_imageprefix);
		
		Integer widthmm = (Integer)xPropSet.getPropertyValue("Width");
		Integer heightmm = (Integer)xPropSet.getPropertyValue("Height");
		
		System.out.println("widht mm: "+ widthmm + " height mm " + heightmm);
		//float aspect = widthmm.floatValue() / heightmm.floatValue();
		int MAX_WIDTH = 200;
		int MAX_HEIGHT = 128;
		int width = MAX_WIDTH, height = MAX_HEIGHT;
		if(widthmm > heightmm)
		{
			height = (int)( (heightmm.floatValue()*MAX_WIDTH)/widthmm.floatValue());
		}
		else
		{
			width = (int)( (widthmm.floatValue()*MAX_HEIGHT)/heightmm.floatValue());
		}

		Image image = new Image(rotule, Integer.parseInt(number), title, m_currPageName);
		
		if(!m_images.containsKey(image.getNumber()))
		{
			m_images.put(image.getNumber(), image);
			 
			m_htmlContent.openDiv("imagen");
			m_htmlContent.writeAnchor("imagen_"+number);
			m_htmlContent.writeImage(number, m_images_subdirectory, m_imageprefix, width, height);
			//m_htmlContent.writeParagraph("rotulo:"+rotule);
			//m_htmlContent.writeParagraph("numero:"+number);
			//m_htmlContent.writeParagraph("titulo:"+title);
			if(rotule!=null || title != null)
			{
				m_htmlContent.write("<p>");
				if(rotule != null) m_htmlContent.write(rotule + " "+ number);
				if(rotule != null && title!=null) m_htmlContent.write(": ");
				if(title!=null) m_htmlContent.write(title);
				m_htmlContent.write("</p>");
				
			}
			m_htmlContent.closeDiv();
			
		}
		else
		{
			LogConsole.getInstance().log("TextDocumentInspectorToSCORM:onImage: skipping export of image "+ image.getNumber() + ". Image already exported.");
		}
		
	}
	//------------------------------------------------------
	
	
	private void onVideo(Collection<TextPortion> portions) throws Exception
	{
		Iterator<TextPortion> iter = portions.iterator();
		String link = iter.next().getContent();
		
		
		m_htmlContent.openDiv("video");
		
		m_htmlContent.writeYoutubeIframe(link);	
		
		m_htmlContent.closeDiv();
		
		
	}
	//------------------------------------------------------
	
	/**
	 * 
	 */
	private void onTitle(String styleName, String content)
	{
		
		System.out.println("TextDocumentInspector:onTitle: nuevo titulo: "+ styleName + ": "+ content);
			Integer level = g_structureStyles.get(styleName);
			if(m_root == null)
			{
				
				System.out.println("TextDocumentInspector:onTitle: nuevo root");

				m_root = new Title("contenido", new Integer(0), m_currPageName);
				Title.g_titleCount= 0; 
				m_current_title = m_root;
				m_last_added_title =new Title(content, level, m_currPageName);
				m_root.addChildren(m_last_added_title);
				
			}
			else
			{
				Title title = new Title(content, level, m_currPageName);
				
				if(title.isBrother(m_last_added_title))
				{
					System.out.println("TextDocumentInspector:onTitle: nuevo hermano");

					m_current_title.addChildren(title);
					m_last_added_title = title;
				}
				else 
				{
					System.out.println("TextDocumentInspector:onTitle: nuevo hijo");

					m_last_added_title.addChildren(title);
					m_current_title = m_last_added_title;
					m_last_added_title = title;
				}
			}
			
		
	}
	//------------------------------------------------------
	
	@Override
	/**
	 * this callback must be used to integrate with yahoo search services.
	 * the idea here is to split paragraph into sentences (using dots as separators)
	 * then, for each sentence, perform a search (but only over sentences with more than 10 words). 
	 */
	public void onPlainParagraph(String styleName, String content) throws Exception 
	{
		//0. avoid empty or very short paragraphs
		if(content.length()<100)return;
		//1. split content into sentences, using dot as separator:
		String[] sentences =content.split("\\.");
		
		//2. for each sentence, invoke yahoo service (discard too short sentences)
		for(int i=0; i<sentences.length;i++)
		{
			String sentence = sentences[i];
			if(sentence.length()<100) break;
			
  	}
	}
	//-------------------------------------------
	
	
	public void onGraphic(TextGraphic graphic) 
	{
		System.out.println("onGraphic");
		
		try {
			
			
			
			m_document.exportGraphicToLocalFile(graphic.getXGraphic(), m_repository.getScormDirectoryUnidad() + m_images_subdirectory+ graphic.getName()+".jpg");
			
			
			Integer widthmm = graphic.getWidth();
			Integer heightmm = graphic.getHeight();
			
			System.out.println("widht mm: "+ widthmm + " height mm " + heightmm);
			//float aspect = widthmm.floatValue() / heightmm.floatValue();
			int MAX_WIDTH = 400;
			int MAX_HEIGHT = 400;
			int width = MAX_WIDTH, height = MAX_HEIGHT;
			if(widthmm > heightmm)
			{
				height = (int)( (heightmm.floatValue()*MAX_WIDTH)/widthmm.floatValue());
			}
			else
			{
				width = (int)( (widthmm.floatValue()*MAX_HEIGHT)/heightmm.floatValue());
			}

				 
				m_htmlContent.openDiv("imagen");
				//m_htmlContent.writeAnchor("imagen_"+number);
				m_htmlContent.writeImage(graphic.getName()+".jpg", m_images_subdirectory, width, height);
					
				m_htmlContent.closeDiv();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	//-------------------------------------------
	
	@Override
	public void onParagraph(String styleName, Collection<TextPortion> portions) throws Exception 
	{
		//write the paragraph to the current page. AFTER write, check if it there is need to open a new page. 
		
	
		/*
		 * a veces, al editar un mismo documento en word y en openoffice, se duplican los estilos.
		 * los estilos duplicados se reconocen porque se les agrega un número al final (pero cuidado!
		 * hay que considerar el caso especial de Heading1, Heading2..)
		 */
		if(styleName != null && styleName.length()>2 && !styleName.startsWith("Heading"))
		{
			String lastChar = Character.toString(styleName.charAt(styleName.length()-1));
			if(lastChar.matches("[0-9]"))
			{
				styleName = styleName.substring(0, styleName.length()-1);
			}
		}
		
		String htmlStyleName = "error";
		if(g_validStylesNames.containsKey(styleName))
		{
			//get the html style name from the hash table
			htmlStyleName = g_validStylesNames.get(styleName);
			
			//check if it is a title style. 
			if(g_structureStyles.containsKey(htmlStyleName))
			{
				if(g_structureStyles.get(htmlStyleName).intValue()<= MIN_STYLE_LEVEL_CREATE_PAGE)
				{
					//CREATE A NEW PAGE!! take care.. it would be the FIRST page
					openPage();
				}
			}
			
			m_htmlContent.openDiv(htmlStyleName);
			m_htmlContent.openParagraph();
			String content = dumpParagraphHtmlContent(portions);
			
			//is this a structure (title) style?
			if(g_structureStyles.containsKey(htmlStyleName))
			{
				//write a anchor name
				onTitle(htmlStyleName, content);
				m_htmlContent.writeAnchor(m_last_added_title.getAnchorName());
			}

			m_htmlContent.closeParagraph();
			m_htmlContent.closeDiv();
		}
		else if(g_skippableStylesNames.contains(styleName))			
		{
			System.out.println("found skippable style: "+ styleName+ ". just skipping it..");
			return;
		}
		else
		{
			
			/*
			 * en OpenOffice, las tablas tienen estilo "table". pero en word,
			 * quedan como estilo "epigrafe" y no son distinguibles de las imágenes.
			 */
			if("Table".equals(styleName))
			{
				onTable(portions);
			}
			else if("Epígrafe".equals(styleName) || "Imagen".equals(styleName) || "Image".equals(styleName))
			{
				//documento de word. puede ser una tabla o una imagen..
				onWordTableOrImage(portions);
			}
			else if("video".equals(styleName))
			{
				onVideo(portions);
			}
			else
			{
				m_htmlContent.openDiv(htmlStyleName);
				m_htmlContent.writeParagraph("{estilo invalido: "+styleName+"}");
				m_htmlContent.openParagraph();
				dumpParagraphHtmlContent(portions);
				m_htmlContent.closeParagraph();
				m_htmlContent.closeDiv();
			}
			
		}
	
		
	}
	//---------------------------------

	@Override
	public void onEnterTable() throws IOException, IncompleteInformationException 
	{
		m_htmlContent.openTable();
		m_htmlContent.openRow();
	}

	@Override
	public void onEnterCell() throws IOException, IncompleteInformationException 
	{
		m_htmlContent.openCell();
	}

	@Override
	public void onExitCell() throws IOException, IncompleteInformationException 
	{
		m_htmlContent.closeCell();
	}

	@Override
	public void onExitTable() throws IOException, IncompleteInformationException 
	{
		m_htmlContent.closeRow();
		m_htmlContent.closeTable();
	}
	//---------------------------------
}
//---------
