/*
 * Created on 12/03/2011
 */
package com.cesarpachon.services;

import com.cesarpachon.inspector.TextDocumentInspectorToODT;
import com.cesarpachon.openoffice.textdocument.TextDocument;
import com.cesarpachon.util.LogConsole;

/**
 * opens two documents: 
 * docsource = the document with the unit content
 * doctemplate = the document to be used as output template 
 * (it will be copied/pasted first, then opened the copied version, 
 * so the original will remain untouched)
 * @author Milo :D
 */
public class PDFModuleGenerator 
{
	
	TextDocument m_docsource;
	TextDocument m_doctemplate;
	
	
	public PDFModuleGenerator()
	{
		
	}
	//-----------------------------
	
	
	/**
	 * repositoryTO must have been set to a workable current unit
	 * @throws Exception 
	 */
	public void execute() throws Exception
	{
    throw new UnsupportedOperationException(); 
    /*
		LogConsole.getInstance().log("PDFModuleGenerator: iniciando");
		
		LogConsole.getInstance().log("abriendo documento fuente..");
		String urlUnit = repository.getUrlUnidad();
		m_docsource = new TextDocument(urlUnit);

		LogConsole.getInstance().log("abriendo documento plantilla..");
		String urlTemplate = repository.getURLPrintTemplate();
		m_doctemplate = new TextDocument(urlTemplate);
		
		LogConsole.getInstance().log("pasando contenido a plantilla..");
		TextDocumentInspectorToODT inspector = new TextDocumentInspectorToODT(m_docsource, m_doctemplate);
		inspector.beginInspection();
		*/
		

	}
	//---------------------------------
	
}
//--------------------------
