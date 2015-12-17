/*
 * Created on 5/05/2010
 */
package com.cesarpachon.openoffice.spreadsheet;

import java.util.HashMap;
import java.util.LinkedList;

import com.cesarpachon.inspector.TextDocumentInspectorToSCORM;
import com.cesarpachon.openoffice.BaseOpenOfficeDocument;

import com.sun.star.beans.Property;
import com.sun.star.beans.PropertyValue;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertySet;
import com.sun.star.beans.XPropertySetInfo;
import com.sun.star.beans.XTolerantMultiPropertySet;
import com.sun.star.container.NoSuchElementException;
import com.sun.star.container.XEnumeration;
import com.sun.star.container.XEnumerationAccess;
import com.sun.star.container.XNameAccess;
import com.sun.star.container.XNameContainer;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.frame.XStorable;
import com.sun.star.io.IOException;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.IndexOutOfBoundsException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.lang.XComponent;
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.sheet.XSpreadsheetDocument;
import com.sun.star.sheet.XSpreadsheets;
import com.sun.star.style.XStyle;
import com.sun.star.style.XStyleFamiliesSupplier;
import com.sun.star.text.XParagraphCursor;
import com.sun.star.text.XText;
import com.sun.star.text.XTextContent;
import com.sun.star.text.XTextCursor;
import com.sun.star.text.XTextDocument;
import com.sun.star.text.XTextRange;
import com.sun.star.uno.AnyConverter;
import com.sun.star.uno.UnoRuntime;

public class SpreadsheetDocument extends BaseOpenOfficeDocument
{
	//the main service
	 private XComponent m_xComponent;
	 //their interfaces
	 
	//the whole text of the document (service)
	 private XSpreadsheetDocument m_xSpreadsheetDocument;
	 
	 private XSpreadsheets m_xSpreadsheets; 
	 	
	 private XSpreadsheet m_current_xSpreadsheet;
	 
	public SpreadsheetDocument(String url) throws Exception 
	{
		 PropertyValue[] loadProps = new PropertyValue[0];
		 m_xComponent = m_xComponentLoader.loadComponentFromURL(url, "_blank", 0, loadProps);
		 
	}
	//-------------------------------
	
	/**
	 * the service who handles the whole text of the document
	 */
	private XSpreadsheetDocument getXSpreadsheetDocument()
	{
		if(m_xSpreadsheetDocument == null)
		{
			m_xSpreadsheetDocument = (XSpreadsheetDocument)UnoRuntime.queryInterface(
					XSpreadsheetDocument.class, m_xComponent);
		}
		return m_xSpreadsheetDocument;
	}
	//--------------------------------------
	
	
	
	public XSpreadsheets getXSpreadsheets()
	{
		
		if(m_xSpreadsheets == null)
		{
			m_xSpreadsheets = getXSpreadsheetDocument().getSheets();
		}
		return m_xSpreadsheets;
	}
	//--------------------------------------

	public XSpreadsheet setCurrentSheet(String name) throws NoSuchElementException, WrappedTargetException
	{
		Object sheet = getXSpreadsheets().getByName(name);
	    m_current_xSpreadsheet = (XSpreadsheet)UnoRuntime.queryInterface(XSpreadsheet.class, sheet);
	    return m_current_xSpreadsheet;
	}
	//--------------------------------------------
	
	public void close() 
	{
		// TODO Auto-generated method stub
		//here.. we must close the xframe.. 
		m_xComponent.dispose();
	}
	//-------------------------------------------

	public String getCellContentAsString(XSpreadsheet sheet, int column, int row) throws IndexOutOfBoundsException 
	{
		String formula = sheet.getCellByPosition(column, row).getFormula();
		return formula.replace("'", "");
	}
	//------------------------------------------------

	public boolean getCellContentAsBool(XSpreadsheet sheet, int i, int row) throws IndexOutOfBoundsException 
	{
		String formula = getCellContentAsString(sheet, i, row);
		boolean flag = Boolean.parseBoolean(formula);
		return flag; 
	}
	//------------------------------------------------
	
	
	
}
//--------------------------------------------------
