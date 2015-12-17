/*
 * Created on 12/03/2011
 */
package com.cesarpachon.inspector;

import java.io.IOException;
import java.util.Collection;

import com.cesarpachon.openoffice.textdocument.TextDocument;
import com.cesarpachon.openoffice.textdocument.TextGraphic;
import com.cesarpachon.openoffice.textdocument.TextPortion;
import com.cesarpachon.util.LogConsole;

import com.sun.star.graphic.XGraphic;
import com.sun.star.style.XStyle;

public class TextDocumentInspectorToConsole extends TextDocumentInspectorBase
{
	TextDocument m_docsource;

	public TextDocumentInspectorToConsole(TextDocument docsource) 
	{
		m_docsource = docsource;
	}

	@Override
	public void onUsedStyle(String styleName, XStyle styleSrv) throws Exception 
	{
	  LogConsole.getInstance().log("Inspector:onUsedStyle: "+ styleName);
	}
	//--------------------------------------

	@Override
	public void onParagraph(String styleName, Collection<TextPortion> portions)
			throws Exception 
	{
	  LogConsole.getInstance().log("Inspector:onParagraph: ("+ styleName+")");
	}
	//--------------------------------------

	@Override
	public void onPlainParagraph(String paraStyleName, String content)
			throws Exception 
	{
		
	  LogConsole.getInstance().log("Inspector:onPlainParagraph: ("+ paraStyleName+"):"+ content);
	}
	//--------------------------------------

	@Override
	public void beginInspection() throws Exception 
	{
		LogConsole.getInstance().log("inspector: begin inspection");
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
	  LogConsole.getInstance().log("Inspector:onGraphic");
	}


}
//--------------------------------------
