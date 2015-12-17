/*
 * Created on 12/03/2011
 */
package co.edu.virtualhumboldt.inspector;

import java.io.IOException;
import java.util.Collection;

import co.edu.virtualhumboldt.openoffice.textdocument.TextDocument;
import co.edu.virtualhumboldt.openoffice.textdocument.TextGraphic;
import co.edu.virtualhumboldt.openoffice.textdocument.TextPortion;
import co.edu.virtualhumboldt.util.LogConsole;

import com.sun.star.graphic.XGraphic;
import com.sun.star.style.XStyle;

public class TextDocumentInspectorToODT extends TextDocumentInspectorBase
{
	TextDocument m_docsource;
	TextDocument m_doctemplate; 

	public TextDocumentInspectorToODT(
			TextDocument docsource,
			TextDocument doctemplate) 
	{
		m_docsource = docsource;
		m_doctemplate = doctemplate;
	}

	@Override
	public void onUsedStyle(String styleName, XStyle styleSrv) throws Exception 
	{
		
	}
	//--------------------------------------

	@Override
	public void onParagraph(String styleName, Collection<TextPortion> portions)
			throws Exception 
	{
		
	}
	//--------------------------------------

	@Override
	public void onPlainParagraph(String paraStyleName, String content)
			throws Exception 
	{
		
	}
	//--------------------------------------

	@Override
	public void beginInspection() throws Exception 
	{
		LogConsole.getInstance().log("TextDocumentInspectorToODT: begin inspection");
		
		//open a text cursor in the template document
		m_doctemplate.initOutputTextCursor();
		
		m_doctemplate.writePlainText("hola a todos");
	}
	//-----------------------------------------

	@Override
	public void onEnterTable() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEnterCell() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onExitCell() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onExitTable() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGraphic(TextGraphic graphic) {
		// TODO Auto-generated method stub
		
	}


}
//--------------------------------------
