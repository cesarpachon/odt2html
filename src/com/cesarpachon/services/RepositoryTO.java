/*
 * Created on 11/03/2011
 */
package com.cesarpachon.services;
import java.util.Properties;
import com.sun.star.io.IOException;
import com.cesarpachon.exception.IncompleteInformationException;

public class RepositoryTO 
{
	
	private String m_urlUnidades;
	private String m_errorReport;
	private String m_scormDirectory;
	private String m_imagesSubDirectory;
	private String m_templatePath;
	private String m_tocPath;
	private String m_manifestTemplateFile;
	private String m_indexPath;
	
	/**
	 * in the form 01, 02, 03, 04
	 */
	private String m_currUnit;
	
	private String m_urlPrintTemplate;
	private String m_urlPrintDirectory;
	
	
	public RepositoryTO()
	{
		
	}
	//-------------------------------
	
	
	public void loadFromProperties(Properties prop)
	{
		m_urlUnidades= prop.getProperty("urlUnidades"); //file:///C:/repositorios/sistemas_de_telecomunicaciones/diseño/módulo/
		m_errorReport=prop.getProperty("errorReport"); //C:\\repositorios\\sistemas_de_telecomunicaciones\\diseño\\módulo\\inspection.html
		m_scormDirectory=prop.getProperty("scormDirectory"); //C:\\repositorios\\sistemas_de_telecomunicaciones\\implementacion\\scorm\\
		m_imagesSubDirectory=prop.getProperty("imagesSubDirectory"); //\\img\\
		m_templatePath=prop.getProperty("templatePath"); //C:\\repositorios\\sistemas_de_telecomunicaciones\\implementacion\\src\\content_template.html
		m_tocPath=prop.getProperty("tocPath"); //C:\\repositorios\\sistemas_de_telecomunicaciones\\implementacion\\src\\toc_template.html	
		m_manifestTemplateFile = prop.getProperty("manifestTemplateFile");
		m_urlPrintTemplate = prop.getProperty("urlPrintTemplate");
		m_urlPrintDirectory =  prop.getProperty("urlPrintTemplate");
		m_indexPath = prop.getProperty("indexPath");
	}
	//---------------------------------


	public void setCurrentUnit(String m_currUnit) 
	{
		this.m_currUnit = m_currUnit;
	}
	//---------------------------------


	public String getCurrentUnit() 
	{
		return m_currUnit;
	}
	//---------------------------------
	
	public String getUrlPrintUnidad()
	{
		return m_urlPrintDirectory +  "unidad" + m_currUnit+ "/unidad" + m_currUnit+".odt";
	}
	//--------------------------------

	public String getManifestTemplateUnidad()
	{
		return m_scormDirectory + "unidad" + m_currUnit + "/"+ m_manifestTemplateFile;
	}
	//--------------------------------------

	public String getUrlUnidad() 
	{
		return m_urlUnidades + "unidad" + m_currUnit+ "/unidad" + m_currUnit+".odt";
	}
	//---------------------------------------
	
	/**
	 * builds a path to a odt in the subfolder "tablas", as follows: 
	 * urlUnidades/unidadCURR_UNIT/tablas/tablaTABLEID.odt
	 */
	public String getUrlTableSrc(Integer tableid)
	{
		return m_urlUnidades + "unidad" + m_currUnit + "/tablas/tabla"+tableid+".odt";
	}
	//------------------------------------

	/**
	 * builds a path to a html file in the root SCORM folder, as follows: 
	 * m_scormDirectory\\unidadCURR_UNIT\\tablaTABLEID.html
	 * 
	 * file:///D:/GeneratedDocs/PDFTest.html
	 * */
	public String getUrlTableHtml(Integer tableid)
	{
		//it is in the usual way.. we must change it to openoffice url way..
		String directoryTableHTML= 	m_scormDirectory + "\\unidad" + m_currUnit+"\\tabla"+tableid+".html";
		directoryTableHTML  =directoryTableHTML.replace("\\", "/");
		directoryTableHTML = "file:///" + directoryTableHTML;
		return directoryTableHTML;
	}
	//------------------------------------


	/**
	 * return the full path to scorm output folder, in the form: 
	 * SCORM_DIRECTORY\\unidadCURRENT_UNIT\
	 */
	public String getScormDirectoryUnidad() 
	{
		return 	m_scormDirectory + /*"/unidad" + m_currUnit+*/"/";
	}
	//---------------------------------------


	public String getErrorReportPath() 
	{
		return this.m_errorReport;
	}
	//---------------------------------


	public String getTemplatePath() 
	{
		return m_templatePath;
	}
	//---------------------------------


	public String getTocPath() 
	{
		return m_tocPath;
	}
	//---------------------------------


	public String getScormZIPPath() 
	{
		return m_scormDirectory + "unidad"+m_currUnit+ ".zip";
	}
	//---------------------------------
	
	public String getURLPrintTemplate()
	{
		return m_urlPrintTemplate;
	}
	//---------------------------------


	/**
	 * @return the m_indexPath
	 * @throws IncompleteInformationException 
	 */
	public String getIndexPath() throws IncompleteInformationException 
	{
		if(m_indexPath == null)
		{
			throw new IncompleteInformationException("RepositoryTO:getIndexPath is null. check 'indexPath' in your property file. it must point to a valid file. i.e= indexPath=C:\\repositorios\\principios_administrativos\\implementacion\\src\\index_template.html");
		}		
		return m_indexPath;
	}

}
//---------------------------------
