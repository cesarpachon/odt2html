/*
 * Created on 5/05/2010
 */
package com.cesarpachon.openoffice.textdocument;

import java.util.HashMap;
import java.util.LinkedList;

import com.cesarpachon.inspector.TextDocumentInspectorToSCORM;
import com.cesarpachon.openoffice.BaseOpenOfficeDocument;
import com.cesarpachon.openoffice.OpenOfficeUtils;
import com.cesarpachon.util.LogConsole;

import com.sun.star.beans.Property;
import com.sun.star.beans.PropertyValue;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertySet;
import com.sun.star.beans.XPropertySetInfo;
import com.sun.star.beans.XTolerantMultiPropertySet;
import com.sun.star.container.NoSuchElementException;
import com.sun.star.container.XContentEnumerationAccess;
import com.sun.star.container.XEnumeration;
import com.sun.star.container.XEnumerationAccess;
import com.sun.star.container.XNameAccess;
import com.sun.star.container.XNameContainer;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.frame.XFrame;
import com.sun.star.frame.XModel;
import com.sun.star.frame.XStorable;
import com.sun.star.graphic.XGraphic;
import com.sun.star.graphic.XGraphicProvider;
import com.sun.star.io.IOException;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.lang.XServiceInfo;
import com.sun.star.sheet.XSpreadsheetDocument;
import com.sun.star.style.BreakType;
import com.sun.star.style.XStyle;
import com.sun.star.style.XStyleFamiliesSupplier;
import com.sun.star.table.XCell;
import com.sun.star.text.XPageCursor;
import com.sun.star.text.XParagraphCursor;
import com.sun.star.text.XText;
import com.sun.star.text.XTextContent;
import com.sun.star.text.XTextCursor;
import com.sun.star.text.XTextDocument;
import com.sun.star.text.XTextFrame;
import com.sun.star.text.XTextFramesSupplier;
import com.sun.star.text.XTextGraphicObjectsSupplier;
import com.sun.star.text.XTextRange;
import com.sun.star.text.XTextRangeCompare;
import com.sun.star.text.XTextTable;
import com.sun.star.text.XTextTableCursor;
import com.sun.star.uno.AnyConverter;
import com.sun.star.uno.UnoRuntime;

public class TextDocument extends BaseOpenOfficeDocument
{
	//the main service
	 private XComponent m_xComponent;


	 //the whole text of the document (service)
	 private XTextDocument m_xTextDocument;
	 
	 	/*
	 	 * private XText m_xText;
	 	 * do m_xTextDocument.getText(). 
	 	*/
	 	private XEnumerationAccess m_xEnumerationAccess;
	 	
	/*
	 * keep objects XStyle (services). only those styles that are in use. 	
	 */
	private HashMap<String, XStyle> m_stylesInUse;
	 
	
	/**
	 * used for output operations
	 */
	XTextCursor m_xOutputTextCursor; 
	
	
	public TextDocument(String url) throws Exception
	{
		 PropertyValue[] loadProps = new PropertyValue[0];
		 m_xComponent = m_xComponentLoader.loadComponentFromURL(url, "_blank", 0, loadProps);
		 
	}
	//-------------------------------
	
	/**
	 * the service who handles the whole text of the document
	 */
	private XTextDocument getXTextDocument()
	{
		if(m_xTextDocument == null)
		{
			m_xTextDocument = (XTextDocument)UnoRuntime.queryInterface(
	            XTextDocument.class, m_xComponent);
		}
		return m_xTextDocument;
	}
	//--------------------------------------
	
	/**
	 * http://openoffice.2283327.n4.nabble.com/how-to-get-image-position-in-writer-td2927180.html
	 *  XNameAccess as = os.getGraphicObjects();
            String[] s = as.getElementNames();
           
            //Get the first image of the document
            Object o = (Object) as.getByName(s[0]); 
            
            http://blog-of-darius.blogspot.com/2011/03/extract-images-from-word-openoffice.html
	 * @throws WrappedTargetException 
	 * @throws NoSuchElementException 
	 * @throws UnknownPropertyException 
            
	 */
	protected XTextGraphicObjectsSupplier getTextGraphicsObjectSupplier() throws NoSuchElementException, WrappedTargetException, UnknownPropertyException
	{
		XTextGraphicObjectsSupplier os =
				  (XTextGraphicObjectsSupplier) UnoRuntime.queryInterface(
			XTextGraphicObjectsSupplier.class, m_xComponent);
		
		return os;
	}
	//--------------------------------------------
	
	
	/**
	 * UNUSED. NOT WORKING
	 * @param paragraphRange
	 * @return 
	 * @throws NoSuchElementException
	 * @throws WrappedTargetException
	 * @throws UnknownPropertyException
	 */
	protected TextGraphic getGraphicAtPosition(XTextRange paragraphRange) throws NoSuchElementException, WrappedTargetException, UnknownPropertyException
	{
		 XTextRangeCompare xtrc = (XTextRangeCompare)UnoRuntime.queryInterface(
                 XTextRangeCompare.class, paragraphRange.getText());

		
		XNameAccess nameAccess = getTextGraphicsObjectSupplier().getGraphicObjects();
		if (nameAccess != null && nameAccess.hasElements())
		{
			String[] names = nameAccess.getElementNames();
			
			if(names == null)
			{
				System.out.println("getGraphicAtPosition: why names are null?");
				return null;
			}
			
			for (int i = 0; i < names.length; i++)
			{
				XServiceInfo xsi = (XServiceInfo) UnoRuntime.queryInterface(
						 XServiceInfo.class, nameAccess.getByName(names[i]));
				
				if (xsi != null &&
				xsi.supportsService("com.sun.star.text.TextContent") &&
				xsi.supportsService("com.sun.star.text.TextGraphicObject"))
				{
					XPropertySet xProps = (XPropertySet) UnoRuntime.queryInterface(
							XPropertySet.class, nameAccess.getByName(names[i]));
					
					String url = (String)xProps.getPropertyValue("GraphicURL");
					//System.out.println("found image: "+names[i]+ " "+ url);
					
					Object objGraphic = nameAccess.getByName(names[i]);

					TextGraphic graphic = new TextGraphic(names[i], objGraphic);

					 try 
		                {
							if (xtrc.compareRegionStarts(graphic.getAnchor(), paragraphRange) == 0) 
							   {
								   System.out.println("ENCONTRAMOS LA IMAGEN!");
								  	//Object oGraphics = xProps.getPropertyValue("Graphic");
									//XGraphic xGraphic = UnoRuntime.queryInterface(XGraphic.class, oGraphics);
									return graphic;
								   
								   
							   }
						}
		                catch (IllegalArgumentException e) 
						{
		                	//System.out.println("exception: illegal argument.. comparing region start FindFrame method");
							// TODO Auto-generated catch block
							//e.printStackTrace();
						}
					
					
				}
			}	
		}
		return null;
	}
	//-------------------------------------------
	


	/**
	 * text enumeration access
	 * @return
	 */
	private XEnumerationAccess getXEnumerationAccess()
	{
		if(m_xEnumerationAccess == null)
		{
		   m_xEnumerationAccess= (XEnumerationAccess)
		   UnoRuntime.queryInterface(XEnumerationAccess.class, getXTextDocument().getText());//o al text?
		
		   
		}
		return m_xEnumerationAccess;
	}
	//----------------------------------------------
	
	/*private XParagraphCursor getXParagraphCursor()
	{
		 // Get the XParagraphCursor interface of the document cursor
	    XParagraphCursor xParaCursor = (XParagraphCursor) 
	    UnoRuntime.queryInterface(
	        XParagraphCursor.class, mxDocCursor);
	}
	//--------------------------------------------------
	*/
	
	public void dumpListOfStyles(ITextDocumentVisitor InspectorVisitor) throws Exception
	{
		XStyleFamiliesSupplier familiesSupplier = (XStyleFamiliesSupplier)
        UnoRuntime.queryInterface(XStyleFamiliesSupplier.class,getXTextDocument());

		XNameAccess familynames = familiesSupplier.getStyleFamilies();
		
				
		//but, we are interested in a very specific kind of style: ParagraphStyles..
		
		// Access the 'ParagraphStyles' Family
        XNameContainer xFamily = (XNameContainer) UnoRuntime.queryInterface( 
            XNameContainer.class, familynames.getByName("ParagraphStyles"));
        
        
        m_stylesInUse = new HashMap<String, XStyle>();
        
        for(String element:xFamily.getElementNames())
        {
        	/*
        	 * Each style is a com.sun.star.style.Style and supports the interface 
        	 * com.sun.star.style.XStyle that inherits from com.sun.star.container.XNamed. 
        	 */
        	XStyle style = (XStyle) UnoRuntime.queryInterface( 
        			XStyle.class, xFamily.getByName(element));
            
        	//only dump used styles.. store into a hashmap
        	if(style.isInUse())
        	{
        		m_stylesInUse.put(element, style);
        		
        		System.out.print("found style: "+ element);
            	//System.out.print(" is in use: "+style.isInUse());
            	System.out.println(" is user defined: "+style.isUserDefined());
            	
            	InspectorVisitor.onUsedStyle(element, style);
            	
        	}
        	
        }
	}
	//-----------------------------
	
	
	/**
	 * the same as dumpParagraphsStyles, but only dumps plain text into visitor
	 */
	public void dumpParagraphsPlain(ITextDocumentVisitor visitor) throws Exception
	{
		   // Get an enumeration of paragraphs.
	    XEnumeration xParaEnum = getXEnumerationAccess().createEnumeration();
	    
	    
	    // Creat a text cursor and go the beginning.
	    //XTextCursor xTextCursor = this.m_xTextDocument.getText().createTextCursor();
	    //xTextCursor.gotoStart(false);
	    
	    
	    // Set up a paragraph cursor and point it to the first paragarph.
	    // when the enumeration gets a new element, select that paragraph using the cursor.
	    
	    XParagraphCursor xParagraphCursor = null;
	    //xParagraphCursor = (XParagraphCursor) UnoRuntime.queryInterface(XParagraphCursor.class, xTextCursor );
	    
	    
	    while ( xParaEnum.hasMoreElements() )
	    {
	    	//System.out.println("----");
	    	XTextContent xParagraph = (XTextContent)
	        UnoRuntime.queryInterface(XTextContent.class,xParaEnum.nextElement());
	    	   
	        //obtener el nombre de estilo a partir del enumerado: 
	    	XPropertySet xParagraphProps = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, xParagraph);
	    	if(xParagraphProps.getPropertySetInfo().hasPropertyByName("ParaStyleName"))
	    	{
	    		//este paragraph si es un parrafo de texto
	    		String paraStyleName = xParagraphProps.getPropertyValue("ParaStyleName").toString();
	            String sText = xParagraph.getAnchor().getString();

	    		visitor.onPlainParagraph(paraStyleName, sText);
	    	}
	       }
	  } 
		//----------------------------------------------------------

	
	public void dumpParagraphsStyles(ITextDocumentVisitor visitor) throws Exception
	{
		
		//testing..
		//getTextGraphicsObjectSupplier();
		
	   // Get an enumeration of paragraphs.
    XEnumeration xParaEnum = getXEnumerationAccess().createEnumeration();
    
      while ( xParaEnum.hasMoreElements() )
    {
    	Object obj = xParaEnum.nextElement();
    	
    	XTextContent xParagraph = (XTextContent) UnoRuntime.queryInterface(XTextContent.class,obj);
             
    	//http://wiki.services.openoffice.org/wiki/Documentation/DevGuide/Text/Iterating_over_Text
    	 // Get a reference to the next paragraphs XServiceInfo interface. TextTables
        // are also part of this
        // enumeration access, so we ask the element if it is a TextTable, if it
        // doesn't support the
        // com.sun.star.text.TextTable service, then it is safe to assume that it
        // really is a paragraph
    	 XServiceInfo xInfo = (XServiceInfo) UnoRuntime.queryInterface(
                 XServiceInfo.class, obj);
    	 if (xInfo.supportsService("com.sun.star.text.TextTable")) 
    	 {
    		 	System.out.println("encontré una tabla!");
    		 	//pedirle a la tabla.. una interfaz para interar sobre fragmentos???
    		 	//XTextTable xTextTable = (XTextTable) UnoRuntime.queryInterface(XTextTable.class,obj);
    		 	visitAllParagraphsWithinTable(xParagraph, visitor);
    		 	continue;
    	 }

    	 
    	 
    	 /**
    	  * THIS IS JUST A TEST. IT DOESNT SEEM TO WORK.
    	  * 2013: idea.. check if this range match the range of some graphic element.
    	  */
    	 //XTextRange paragraphRange = xParagraph.getAnchor();
    	 //getGraphicAtPosition(paragraphRange);
    	 
    	XPropertySet xPropSet = UnoRuntime.queryInterface(XPropertySet.class, xParagraph); 
    	if(xPropSet.getPropertySetInfo().hasPropertyByName("Graphic"))
    	{
       	 Object oGraphics = xPropSet.getPropertyValue("Graphic");
       	 //print(oGraphics);
    	}
    	  
    
    		//obtener el nombre de estilo a partir del cursor: 
          		//XPropertySet xCursorProps = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, xParagraphCursor);
	            //String paraStyleName = xCursorProps.getPropertyValue("ParaStyleName").toString();
	            
    			//obtener el nombre de estilo a partir del enumerado: 
    			XPropertySet xParagraphProps = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, xParagraph);
    			
    			/* 
    			 * not usefull to detect changes of page. all breatkTypes are zero, unless the last one: PAGE_BEFORE
    			//found property: BreakType of type Type[com.sun.star.style.BreakType]
    			if(xParagraphProps.getPropertySetInfo().hasPropertyByName("BreakType"))
    			{
    				BreakType breakType= (BreakType) UnoRuntime.queryInterface(BreakType.class, xParagraphProps.getPropertyValue("BreakType"));
    				switch(breakType.getValue())
    				{
    				case BreakType.PAGE_AFTER_value: 
    					System.out.println("BREAK TYPE: PAGE_AFTER");
    					break;
    				case BreakType.PAGE_BEFORE_value:
    					System.out.println("BREAK TYPE: PAGE_BEFORE");
    					break;
    				case BreakType.PAGE_BOTH_value:
    					System.out.println("BREAK TYPE: PAGE_BOTH");
    					break;
    				case BreakType.COLUMN_AFTER_value: 
    					System.out.println("BREAK TYPE: COLUMN_AFTER");
    					break;
    				case BreakType.COLUMN_BEFORE_value:
    					System.out.println("BREAK TYPE: COLUMN_BEFORE");
    					break;
    				case BreakType.COLUMN_BOTH_value:
    					System.out.println("BREAK TYPE: COLUMN_BOTH");
    					break;
    					
    				default:
    					System.out.println("BREAK TYPE: other " + breakType.getValue());
    				}
    			}
    			*/
        			
    			
    			if(xParagraphProps.getPropertySetInfo().hasPropertyByName("ParaStyleName"))
    			{
    				//este paragraph si es un parrafo de texto
        			String paraStyleName = xParagraphProps.getPropertyValue("ParaStyleName").toString();
    	            //weeeell... how about the character styles?
    	            XEnumerationAccess xParaEnumerationAccess = (XEnumerationAccess)
                    UnoRuntime.queryInterface(XEnumerationAccess.class, xParagraph);
    	            
    	            if(xParaEnumerationAccess != null)
    	            {
    	            	
    	            	visitPortions(xParaEnumerationAccess, paraStyleName, visitor);
    	            }
    	            else
    	            {
    				//este paragraph es otra cosa (tabla, imagen?)noop.. los paragraph tabla llegan al visitor con paraStyleName = "Table". 
    				System.out.println("OTRA COSA");
    	            }
	            }
    			else
    			{
    				System.out.println("some paragraph content without ParaStyleName property.. what is this?"); 
    			}
       }
     
  } 
	//------------------------------------------------------------
	static int g_orphanGraphicCounter=0;
	
	private void visitPortions(XEnumerationAccess xParaEnumerationAccess, 
								String paraStyleName, 
								ITextDocumentVisitor visitor)
		throws Exception 
	{
		LinkedList<TextPortion> portions = new LinkedList<TextPortion>();
    	
        //this is a normal text paragraph
       	XEnumeration xPortionEnumeration = xParaEnumerationAccess.createEnumeration();
       	while ( xPortionEnumeration.hasMoreElements() ) 
       	{
           	//com.sun.star.text.TextPortion 
           	Object textportion = xPortionEnumeration.nextElement();
               // output of all parts from the paragraph with different at  tributes
           	
           	//the content: 
           	XTextRange xWord = (XTextRange) UnoRuntime.queryInterface(XTextRange.class,textportion);
           	   String content = xWord.getString();
               
               
               XPropertySet xParagraphProps = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, textportion);
   			  if(xParagraphProps.getPropertySetInfo().hasPropertyByName("ParaStyleName"))
   			  {
   				paraStyleName = xParagraphProps.getPropertyValue("ParaStyleName").toString();
   			  }
   			
   			  		
   			 //dumpSupportedServiceNames(textportion);
   			
   			
               //OpenOfficeUtils.dumpPropertySet(xParagraphProps);
   			
               
               //the character style name: 
               
               String charStyleName = null; 
               XPropertySet textProps = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, textportion); 
               Object oName = textProps.getPropertyValue("CharStyleNames");
               // its a sequence of strings, not one string
               if (AnyConverter.isArray(oName))
               {
               	Object [] values = (Object[]) AnyConverter.toArray(oName);
               	for (int i = 0 ; i <values.length; i++)
                   {
               		charStyleName = values[i].toString();
               		System.out.println("stylename: "+charStyleName);
                   }
               }
              
              //oki.. how about create an array of text portion, style name? 
               //System.out.println("styleName: "+ charStyleName+ " content: "+ content);
               
               //avoid adding empty first portions..
               
               boolean isFrame = false;
               String strTextPortionType = null; 
               String strTextFrame = null; 
               
               
               if(xParagraphProps.getPropertySetInfo().hasPropertyByName("TextPortionType"))
               {
            	   strTextPortionType = xParagraphProps.getPropertyValue("TextPortionType").toString();
            	   isFrame = "Frame".equals(strTextPortionType);
               }
               if(xParagraphProps.getPropertySetInfo().hasPropertyByName("TextFrame"))
            	   {            		   
            		   strTextFrame =  xParagraphProps.getPropertyValue("TextFrame").toString();
            		 System.out.println("has TextFrame property: "+ strTextFrame);  
            		 
            	   }
            	
               
               System.out.println("strTextPortionType "+ strTextPortionType + " strTextFrame "+ strTextFrame);
               
               
               
               //this.DumpPropertySet(xParagraphProps);
               
               if((/*portions.size() == 0 && */content.length() == 0) || isFrame)
               {
               	//System.out.println("discarding empty portion");
            	   //what if this is the frame we are looking for?
            	   
            	   //is a frame?
        			  
                          XTextFrame xtf = findFrame(xWord);
                          if(xtf != null)
                          {
                              System.out.println("Got a frame!");
                              System.out.println("Text from Frame : " + xtf.getText().getString()); 
                              //ok.. here wouqld be images.. 
                              
                  			XEnumerationAccess xParaEnumerationAccess2 = (XEnumerationAccess)
             				UnoRuntime.queryInterface(XEnumerationAccess.class, xtf.getText());
                  			if(xParaEnumerationAccess2 != null)
                  			{
                  				System.out.println("visiting portions whitin frame..");
                  				visitPortions(xParaEnumerationAccess2, null, visitor);
                  			}
                          }
                          else //xtf is null.. this is not a frame.. maybe a untitled image?
                          {
                        	  System.out.println("null frame.. maybe a untitled image? content:"+content + " isframe: "+ isFrame);
                                                  	
                          	TextGraphic graphic = getGraphicAtPosition(xWord);
                          	if(graphic !=null)
                          		visitor.onGraphic(graphic);
                          	//g_orphanGraphicCounter++;
                          	
                        	  System.out.println("finishing xcontentenum ");
                               
                        	  
                          }
      				  
        			  
        			
     			  
            	   
               }
               else
               {
                   TextPortion portion = new TextPortion(charStyleName, content);
                   portions.add(portion);
               }
       	}
       System.out.println("TextDocument:visitPortions paraStyleName "+ paraStyleName);
       	visitor.onParagraph(paraStyleName, portions);
	}
	//-------------------------------------------------------------------
	
	/**
	 * obj must support service com.sun.star.text.TextTable, interfaces XTable and XTextContent
	 * http://wiki.services.openoffice.org/wiki/API/Samples/Java/Writer/TextDocumentStructure
	 * @throws Exception 
	 */
	private void visitAllParagraphsWithinTable(XTextContent xParagraphTable, ITextDocumentVisitor visitor) 
			throws Exception 
	{
		visitor.onEnterTable();
		
		XTextTable xTextTable = (XTextTable)
        	UnoRuntime.queryInterface(XTextTable.class, xParagraphTable);
        
		String[] cellnames = xTextTable.getCellNames();
		for(String cellname:cellnames)
		{
			System.out.println("cellname "+ cellname);
			XCell cell = xTextTable.getCellByName(cellname);
			//System.out.println("cell get formula: " +cell.getFormula());
			
			//XTextTableCursor xtexttablecursor = xTextTable.createCursorByCellName(cellname);
			//xtexttablecursor.
			
			
			XText xCellText = (XText) UnoRuntime.queryInterface(XText.class, xTextTable.getCellByName(cellname));
			System.out.println(xCellText.getString());
			
			//String paraStyleName = xParagraphProps.getPropertyValue("ParaStyleName").toString();
			
			XEnumerationAccess xParaEnumerationAccess = (XEnumerationAccess)
            				UnoRuntime.queryInterface(XEnumerationAccess.class, xCellText);
            if(xParaEnumerationAccess != null)
            {
            	visitor.onEnterCell();
            	visitPortions(xParaEnumerationAccess, null, visitor);
            	visitor.onExitCell();
            }
            else
            {
            	System.out.println("ERROR: TextDocument.visitAllParagraphsWithinTable: xParaEnumerationAccess is null");
            }
            
		}
		
		visitor.onExitTable();
		

	}
	//----------------------------------------------------------
	
	public void dumpParagraphsStyles2(ITextDocumentVisitor visitor) throws Exception
	{
		XParagraphCursor xParaCursor = (XParagraphCursor) 
		UnoRuntime.queryInterface(XParagraphCursor.class, getXTextDocument().getText().createTextCursor());
		
		xParaCursor.gotoStart(false);
		//iterar sobre los parrafos con este cursor..
		do
		{
			//seleccionar todo el parrafo
			xParaCursor.gotoStartOfParagraph(false);
			xParaCursor.gotoEndOfParagraph(true);

			 XPropertySet xCursorProps = (XPropertySet) UnoRuntime.queryInterface(
		                XPropertySet.class, xParaCursor);
		            
		            String paraStyleName = xCursorProps.getPropertyValue("ParaStyleName").toString();
		            System.out.println("paraStyleName = "+ paraStyleName);
		          //  String ParaAutoStyleName = xCursorProps.getPropertyValue("ParaAutoStyleName").toString();
		          //  System.out.println("ParaAutoStyleName = "+ ParaAutoStyleName);
		            String stext = xParaCursor.getText().getString();
		            
		            System.out.println(stext);
		            System.out.println("-----");
		            //visitor.onParagraph(paraStyleName, stext);
		}
		while(xParaCursor.gotoNextParagraph(false));
	
		
		/*xParagraphCursor.gotoNextParagraph(false);
		xParagraphCursor.gotoStartOfParagraph(false);
		xParagraphCursor.gotoEndOfParagraph(true);
		XPropertySet xCursorProps = (XPropertySet)UnoRuntime.queryInterface(XPropertySet.class, xParagraphCursor);
		xCursorProps.setPropertyValue("CharPosture", com.sun.star.awt.FontSlant.ITALIC);
		xCursorProps.setPropertyValue("CharWeight", new Float(com.sun.star.awt.FontWeight.BOLD));
		xCursorProps.setPropertyValue("CharHeight", 20);
		xCursorProps.setPropertyValue("CharColor", new Integer(0x007f007f));
		xCursorProps.setPropertyValue("CharFontName", "Arial");
		xCursorProps.setPropertyValue("ParaAdjust", ParagraphAdjust.CENTER);
		*/
	}
	//-----------------------------
	

	
	public void dumpParagraphsStyles0(ITextDocumentVisitor visitor) throws Exception
	{
        
        // the enumeration contains all paragraph form the document
        XEnumeration xParagraphEnumeration = null;
        xParagraphEnumeration = getXEnumerationAccess().createEnumeration();
		XTextContent xParagraph = null;
        XTextRange xWord = null;
        XEnumerationAccess xParaEnumerationAccess = null;
        XEnumeration xPortionEnumeration = null;

        while ( xParagraphEnumeration.hasMoreElements() ) 
        {
            // get the next paragraph
            xParagraph = (com.sun.star.text.XTextContent)
                UnoRuntime.queryInterface(XTextContent.class,xParagraphEnumeration.nextElement());
            
            // you need the method getAnchor to a TextRange -> to manipulate
            // the paragraph
            String sText = xParagraph.getAnchor().getString();
            
            //System.out.println("---------inicio parrafo ----------");
            //System.out.println(sText);
            //System.out.println("---------fin parrafo ----------");
            
            //does sText has multiple line breaks? if so, that will help to detect titles..
            boolean multiline = sText.split("\n").length > 1;
            
            // create a cursor from this paragraph
            com.sun.star.text.XTextCursor xParaCursor = null;
            xParaCursor = xParagraph.getAnchor().getText().createTextCursor();
            
            
            
            //what is the style of this paragraph?
            // Access the property set of the cursor selection
            XPropertySet xCursorProps = (XPropertySet) UnoRuntime.queryInterface(
                XPropertySet.class, xParaCursor);
            
            String paraStyleName = xCursorProps.getPropertyValue("ParaStyleName").toString();
            System.out.println("paraStyleName = "+ paraStyleName);
            String ParaAutoStyleName = xCursorProps.getPropertyValue("ParaAutoStyleName").toString();
            System.out.println("ParaAutoStyleName = "+ ParaAutoStyleName);
            
            Object CharStyleNames = xCursorProps.getPropertyValue("CharStyleName");
            System.out.println("CharStyleName = "+CharStyleNames);
            
            /*for(String name:CharStyleNames)
            {
            	System.out.print(","+name);
            }
            System.out.println();
            --*/
            
            /*XPropertySetInfo propsetinfo = xCursorProps.getPropertySetInfo();
			Property[] properties = propsetinfo.getProperties();
            for(Property prop:properties)
            {
            	System.out.println("found property: "+ prop.Name + " of type "+ prop.Type);
            }
            this will print:
            found property: ParaSplit of type Type[boolean]
found property: BorderDistance of type Type[long]
found property: ParaFirstLineIndent of type Type[long]
found property: CharAutoEscapement of type Type[boolean]
found property: CharBackTransparent of type Type[boolean]
found property: TextSection of type Type[com.sun.star.text.XTextSection]
found property: ParaAdjust of type Type[short]
found property: CharRotation of type Type[short]
found property: ParaAutoStyleName of type Type[string]
found property: ParaIsNumberingRestart of type Type[boolean]
found property: CharFontNameComplex of type Type[string]
found property: HyperLinkTarget of type Type[string]
found property: CharFontStyleNameAsian of type Type[string]
found property: RubyIsAbove of type Type[boolean]
found property: TopBorder of type Type[com.sun.star.table.BorderLine]
found property: ParaIsAutoFirstLineIndent of type Type[boolean]
found property: SnapToGrid of type Type[boolean]
found property: CharOverline of type Type[short]
found property: CharFontPitchComplex of type Type[short]
found property: DropCapFormat of type Type[com.sun.star.style.DropCapFormat]
found property: LeftBorder of type Type[com.sun.star.table.BorderLine]
found property: CharCrossedOut of type Type[boolean]
found property: CharWeight of type Type[float]
found property: ParaKeepTogether of type Type[boolean]
found property: BottomBorderDistance of type Type[long]
found property: BreakType of type Type[com.sun.star.style.BreakType]
found property: OutlineLevel of type Type[short]
found property: CharFontNameAsian of type Type[string]
found property: CharStyleNames of type Type[[]string]
found property: CharFontCharSetComplex of type Type[short]
found property: ListId of type Type[string]
found property: NumberingStyleName of type Type[string]
found property: RubyText of type Type[string]
found property: CharFontFamilyComplex of type Type[short]
found property: TextFrame of type Type[com.sun.star.text.XTextFrame]
found property: PageDescName of type Type[string]
found property: CharPosture of type Type[com.sun.star.awt.FontSlant]
found property: ReferenceMark of type Type[com.sun.star.text.XTextContent]
found property: ParaLineSpacing of type Type[com.sun.star.style.LineSpacing]
found property: RightBorderDistance of type Type[long]
found property: ParaOrphans of type Type[byte]
found property: HyperLinkEvents of type Type[com.sun.star.container.XNameReplace]
found property: IsSkipHiddenText of type Type[boolean]
found property: CharFontCharSetAsian of type Type[short]
found property: CharFlash of type Type[boolean]
found property: Endnote of type Type[com.sun.star.text.XFootnote]
found property: ParaHyphenationMaxTrailingChars of type Type[short]
found property: CharOverlineHasColor of type Type[boolean]
found property: CharContoured of type Type[boolean]
found property: CharUnderline of type Type[short]
found property: CharFontFamily of type Type[short]
found property: CharFontStyleName of type Type[string]
found property: ParaLastLineAdjust of type Type[short]
found property: ParaConditionalStyleName of type Type[string]
found property: ParaIsHangingPunctuation of type Type[boolean]
found property: TextField of type Type[com.sun.star.text.XTextField]
found property: ParaIsCharacterDistance of type Type[boolean]
found property: PageStyleName of type Type[string]
found property: ParaHyphenationMaxLeadingChars of type Type[short]
found property: ParaHyphenationMaxHyphens of type Type[short]
found property: CharHeightComplex of type Type[float]
found property: CharStrikeout of type Type[short]
found property: DropCapWholeWord of type Type[boolean]
found property: ParaIsHyphenation of type Type[boolean]
found property: ParaStyleName of type Type[string]
found property: PageNumberOffset of type Type[short]
found property: CharRotationIsFitToLine of type Type[boolean]
found property: CharHeightAsian of type Type[float]
found property: ParaBackColor of type Type[long]
found property: CharKerning of type Type[short]
found property: NumberingLevel of type Type[short]
found property: ParaRightMargin of type Type[long]
found property: ParaWidows of type Type[byte]
found property: CharFontStyleNameComplex of type Type[string]
found property: CharNoHyphenation of type Type[boolean]
found property: CharRelief of type Type[short]
found property: HyperLinkName of type Type[string]
found property: NumberingStartValue of type Type[short]
found property: CharShadowed of type Type[boolean]
found property: DropCapCharStyleName of type Type[string]
found property: ContinueingPreviousSubTree of type Type[boolean]
found property: CharWeightAsian of type Type[float]
found property: BottomBorder of type Type[com.sun.star.table.BorderLine]
found property: DocumentIndex of type Type[com.sun.star.text.XDocumentIndex]
found property: CharEscapementHeight of type Type[byte]
found property: CharEscapement of type Type[short]
found property: CharFontFamilyAsian of type Type[short]
found property: CharOverlineColor of type Type[long]
found property: CharScaleWidth of type Type[short]
found property: CharWeightComplex of type Type[float]
found property: CharHidden of type Type[boolean]
found property: ParaUserDefinedAttributes of type Type[com.sun.star.container.XNameContainer]
found property: ListLabelString of type Type[string]
found property: ParaBackGraphicURL of type Type[string]
found property: ParaLineNumberStartValue of type Type[long]
found property: IsSkipProtectedText of type Type[boolean]
found property: CharCaseMap of type Type[short]
found property: CharCombineIsOn of type Type[boolean]
found property: CharCombineSuffix of type Type[string]
found property: UnvisitedCharStyleName of type Type[string]
found property: Cell of type Type[com.sun.star.table.XCell]
found property: CharFontName of type Type[string]
found property: CharEmphasis of type Type[short]
found property: CharLocale of type Type[com.sun.star.lang.Locale]
found property: CharUnderlineColor of type Type[long]
found property: ParaTopMargin of type Type[long]
found property: CharStyleName of type Type[string]
found property: CharAutoKerning of type Type[boolean]
found property: CharBackColor of type Type[long]
found property: ParaIsForbiddenRules of type Type[boolean]
found property: Footnote of type Type[com.sun.star.text.XFootnote]
found property: CharPostureAsian of type Type[com.sun.star.awt.FontSlant]
found property: ParaBackTransparent of type Type[boolean]
found property: VisitedCharStyleName of type Type[string]
found property: ParaVertAlignment of type Type[short]
found property: ParaBottomMargin of type Type[long]
found property: WritingMode of type Type[short]
found property: CharUnderlineHasColor of type Type[boolean]
found property: CharAutoStyleName of type Type[string]
found property: LeftBorderDistance of type Type[long]
found property: CharFontPitch of type Type[short]
found property: RubyAdjust of type Type[short]
found property: CharFontCharSet of type Type[short]
found property: CharFontPitchAsian of type Type[short]
found property: CharLocaleComplex of type Type[com.sun.star.lang.Locale]
found property: CharCombinePrefix of type Type[string]
found property: ParaExpandSingleWord of type Type[boolean]
found property: ParaIsConnectBorder of type Type[boolean]
found property: RightBorder of type Type[com.sun.star.table.BorderLine]
found property: CharWordMode of type Type[boolean]
found property: NumberingRules of type Type[com.sun.star.container.XIndexReplace]
found property: ParaLeftMargin of type Type[long]
found property: ParaRegisterModeActive of type Type[boolean]
found property: ParaChapterNumberingLevel of type Type[byte]
found property: DocumentIndexMark of type Type[com.sun.star.text.XDocumentIndexMark]
found property: ParaBackGraphicLocation of type Type[com.sun.star.style.GraphicLocation]
found property: RubyCharStyleName of type Type[string]
found property: HyperLinkURL of type Type[string]
found property: CharColor of type Type[long]
found property: ParaLineNumberCount of type Type[boolean]
found property: NumberingIsNumber of type Type[boolean]
found property: TextTable of type Type[com.sun.star.text.XTextTable]
found property: TextUserDefinedAttributes of type Type[com.sun.star.container.XNameContainer]
found property: CharHeight of type Type[float]
found property: CharPostureComplex of type Type[com.sun.star.awt.FontSlant]
found property: TopBorderDistance of type Type[long]
found property: ParaShadowFormat of type Type[com.sun.star.table.ShadowFormat]
found property: ParaBackGraphicFilter of type Type[string]
found property: CharLocaleAsian of type Type[com.sun.star.lang.Locale]
found property: ParaTabStops of type Type[[]com.sun.star.style.TabStop]
            
            */
            
            //System.out.println("TextDocument:dumpParagraphsStyles: ");
            
            // goto the start and end of the paragraph
            xParaCursor.gotoStart( false );
            xParaCursor.gotoEnd( true );
            
            
            //visitor.onParagraph(paraStyleName, sText);
            
            
            // The enumeration from the paragraphs contain parts from the
            // paragraph with a different attributes.
            xParaEnumerationAccess = (XEnumerationAccess)
                UnoRuntime.queryInterface(XEnumerationAccess.class, xParagraph);
            
            if(xParaEnumerationAccess != null)
            {
             //this is a normal text paragraph
            	xPortionEnumeration = xParaEnumerationAccess.createEnumeration();
                
            	
            	
                while ( xPortionEnumeration.hasMoreElements() ) 
                {
                	
                	//com.sun.star.text.TextPortion 
                	Object textportion = xPortionEnumeration.nextElement();
                    // output of all parts from the paragraph with different attributes
                    xWord = (XTextRange) UnoRuntime.queryInterface(XTextRange.class,textportion);
      
                    //XTolerantMultiPropertySet multiprop = (XTolerantMultiPropertySet) UnoRuntime.queryInterface(XTolerantMultiPropertySet.class,textportion);
                    
                    XPropertySet textProps = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, textportion); 
                    
                    //textProps.getPropertyValue("CharHeight");

                    //textProps.getPropertyValue("CharWeight");

                    
                    
                    
                    
                    /*
					XPropertySetInfo propsetinfo = textProps.getPropertySetInfo();
					Property[] properties = propsetinfo.getProperties();
                    for(Property prop:properties)
                    {
                    	System.out.println("found property: "+ prop.Name + " of type "+ prop.Type);
                    }
                    
                    this will print: 
                    found property: ParaSplit of type Type[boolean]
found property: BorderDistance of type Type[long]
found property: ParaFirstLineIndent of type Type[long]
found property: CharBackTransparent of type Type[boolean]
found property: CharAutoEscapement of type Type[boolean]
found property: ParaAdjust of type Type[short]
found property: TextSection of type Type[com.sun.star.text.XTextSection]
found property: ParaAutoStyleName of type Type[string]
found property: CharRotation of type Type[short]
found property: HyperLinkTarget of type Type[string]
found property: CharFontNameComplex of type Type[string]
found property: ParaIsNumberingRestart of type Type[boolean]
found property: RubyIsAbove of type Type[boolean]
found property: CharFontStyleNameAsian of type Type[string]
found property: TopBorder of type Type[com.sun.star.table.BorderLine]
found property: SnapToGrid of type Type[boolean]
found property: ParaIsAutoFirstLineIndent of type Type[boolean]
found property: CharOverline of type Type[short]
found property: LeftBorder of type Type[com.sun.star.table.BorderLine]
found property: DropCapFormat of type Type[com.sun.star.style.DropCapFormat]
found property: CharFontPitchComplex of type Type[short]
found property: CharWeight of type Type[float]
found property: CharCrossedOut of type Type[boolean]
found property: ParaKeepTogether of type Type[boolean]
found property: BottomBorderDistance of type Type[long]
found property: BreakType of type Type[com.sun.star.style.BreakType]
found property: TextPortionType of type Type[string]
found property: CharStyleNames of type Type[[]string]
found property: CharFontNameAsian of type Type[string]
found property: OutlineLevel of type Type[short]
found property: CharFontCharSetComplex of type Type[short]
found property: RubyText of type Type[string]
found property: NumberingStyleName of type Type[string]
found property: ListId of type Type[string]
found property: CharFontFamilyComplex of type Type[short]
found property: PageDescName of type Type[string]
found property: TextFrame of type Type[com.sun.star.text.XTextFrame]
found property: CharPosture of type Type[com.sun.star.awt.FontSlant]
found property: ReferenceMark of type Type[com.sun.star.text.XTextContent]
found property: ParaLineSpacing of type Type[com.sun.star.style.LineSpacing]
found property: RightBorderDistance of type Type[long]
found property: HyperLinkEvents of type Type[com.sun.star.container.XNameReplace]
found property: ParaOrphans of type Type[byte]
found property: CharFontCharSetAsian of type Type[short]
found property: Endnote of type Type[com.sun.star.text.XFootnote]
found property: CharFlash of type Type[boolean]
found property: CharOverlineHasColor of type Type[boolean]
found property: ParaHyphenationMaxTrailingChars of type Type[short]
found property: CharContoured of type Type[boolean]
found property: CharFontFamily of type Type[short]
found property: CharUnderline of type Type[short]
found property: ParaLastLineAdjust of type Type[short]
found property: CharFontStyleName of type Type[string]
found property: ParaIsHangingPunctuation of type Type[boolean]
found property: ParaConditionalStyleName of type Type[string]
found property: TextField of type Type[com.sun.star.text.XTextField]
found property: ParaIsCharacterDistance of type Type[boolean]
found property: ControlCharacter of type Type[short]
found property: ParaHyphenationMaxLeadingChars of type Type[short]
found property: PageStyleName of type Type[string]
found property: CharHeightComplex of type Type[float]
found property: ParaHyphenationMaxHyphens of type Type[short]
found property: InContentMetadata of type Type[com.sun.star.text.XTextContent]
found property: DropCapWholeWord of type Type[boolean]
found property: CharStrikeout of type Type[short]
found property: ParaIsHyphenation of type Type[boolean]
found property: CharRotationIsFitToLine of type Type[boolean]
found property: PageNumberOffset of type Type[short]
found property: ParaStyleName of type Type[string]
found property: CharHeightAsian of type Type[float]
found property: CharKerning of type Type[short]
found property: ParaBackColor of type Type[long]
found property: ParaWidows of type Type[byte]
found property: ParaRightMargin of type Type[long]
found property: NumberingLevel of type Type[short]
found property: CharRelief of type Type[short]
found property: CharNoHyphenation of type Type[boolean]
found property: CharFontStyleNameComplex of type Type[string]
found property: HyperLinkName of type Type[string]
found property: CharShadowed of type Type[boolean]
found property: NumberingStartValue of type Type[short]
found property: DropCapCharStyleName of type Type[string]
found property: CharWeightAsian of type Type[float]
found property: ContinueingPreviousSubTree of type Type[boolean]
found property: BottomBorder of type Type[com.sun.star.table.BorderLine]
found property: DocumentIndex of type Type[com.sun.star.text.XDocumentIndex]
found property: CharEscapementHeight of type Type[byte]
found property: CharEscapement of type Type[short]
found property: Bookmark of type Type[com.sun.star.text.XTextContent]
found property: CharFontFamilyAsian of type Type[short]
found property: CharOverlineColor of type Type[long]
found property: CharScaleWidth of type Type[short]
found property: CharWeightComplex of type Type[float]
found property: CharHidden of type Type[boolean]
found property: ParaUserDefinedAttributes of type Type[com.sun.star.container.XNameContainer]
found property: ParaLineNumberStartValue of type Type[long]
found property: ParaBackGraphicURL of type Type[string]
found property: ListLabelString of type Type[string]
found property: CharCaseMap of type Type[short]
found property: IsStart of type Type[boolean]
found property: CharCombineIsOn of type Type[boolean]
found property: CharCombineSuffix of type Type[string]
found property: UnvisitedCharStyleName of type Type[string]
found property: CharFontName of type Type[string]
found property: Cell of type Type[com.sun.star.table.XCell]
found property: CharEmphasis of type Type[short]
found property: CharLocale of type Type[com.sun.star.lang.Locale]
found property: ParaTopMargin of type Type[long]
found property: CharUnderlineColor of type Type[long]
found property: CharStyleName of type Type[string]
found property: CharAutoKerning of type Type[boolean]
found property: CharBackColor of type Type[long]
found property: ParaIsForbiddenRules of type Type[boolean]
found property: Footnote of type Type[com.sun.star.text.XFootnote]
found property: CharPostureAsian of type Type[com.sun.star.awt.FontSlant]
found property: VisitedCharStyleName of type Type[string]
found property: ParaBackTransparent of type Type[boolean]
found property: ParaVertAlignment of type Type[short]
found property: ParaBottomMargin of type Type[long]
found property: WritingMode of type Type[short]
found property: CharUnderlineHasColor of type Type[boolean]
found property: CharAutoStyleName of type Type[string]
found property: LeftBorderDistance of type Type[long]
found property: RubyAdjust of type Type[short]
found property: CharFontPitch of type Type[short]
found property: CharFontCharSet of type Type[short]
found property: CharCombinePrefix of type Type[string]
found property: CharLocaleComplex of type Type[com.sun.star.lang.Locale]
found property: CharFontPitchAsian of type Type[short]
found property: ParaIsConnectBorder of type Type[boolean]
found property: ParaExpandSingleWord of type Type[boolean]
found property: IsCollapsed of type Type[boolean]
found property: RightBorder of type Type[com.sun.star.table.BorderLine]
found property: CharWordMode of type Type[boolean]
found property: NumberingRules of type Type[com.sun.star.container.XIndexReplace]
found property: ParaRegisterModeActive of type Type[boolean]
found property: ParaLeftMargin of type Type[long]
found property: DocumentIndexMark of type Type[com.sun.star.text.XDocumentIndexMark]
found property: ParaChapterNumberingLevel of type Type[byte]
found property: ParaBackGraphicLocation of type Type[com.sun.star.style.GraphicLocation]
found property: RubyCharStyleName of type Type[string]
found property: HyperLinkURL of type Type[string]
found property: ParaLineNumberCount of type Type[boolean]
found property: CharColor of type Type[long]
found property: NumberingIsNumber of type Type[boolean]
found property: TextTable of type Type[com.sun.star.text.XTextTable]
found property: TextUserDefinedAttributes of type Type[com.sun.star.container.XNameContainer]
found property: CharHeight of type Type[float]
found property: TopBorderDistance of type Type[long]
found property: CharPostureComplex of type Type[com.sun.star.awt.FontSlant]
found property: ParaShadowFormat of type Type[com.sun.star.table.ShadowFormat]
found property: ParaBackGraphicFilter of type Type[string]
found property: CharLocaleAsian of type Type[com.sun.star.lang.Locale]
found property: ParaTabStops of type Type[[]com.sun.star.style.TabStop]
                    
                    */
                    
                    String sWordString = xWord.getString();
                   
                    //System.out.println( "Content of the paragraph : " + sWordString );
                }	
            }
            else
            {
            	//this is another kind of content.. table, picture??
            	System.out.println("CONTENIDO NO TEXTUAL");
            }
            
            //visitor.onEndParagraph();
        }

	}
	//------------------------------------------

	public void close() 
	{
		// TODO Auto-generated method stub
		//here.. we must close the xframe.. 
		if(this.m_xTextDocument != null)
			this.m_xTextDocument.dispose();
		else
			System.out.println("TextDocument:close: m_xTextDocument is null.. why??");
	}
	//-------------------------------------------

	public void visitUsedStyles(ITextDocumentVisitor visitor) throws Exception
	{
		dumpListOfStyles(visitor);
	}
	//-------------------------------------------

	public void visitAllParagraphs(ITextDocumentVisitor visitor) throws Exception
	{
		this.dumpParagraphsStyles(visitor);
	}
	//---------------------------------------------------

	public void visitAllPlainParagraphs(ITextDocumentVisitor visitor) throws Exception
	{
		this.dumpParagraphsPlain(visitor);
	}
	//---------------------------

	public XPropertySet exportImage(String imageid, String dir_path, String prefix) throws com.sun.star.uno.Exception, java.io.IOException 
	{
		int pos = Integer.parseInt(imageid)-1;
		return exportImage(pos, dir_path, prefix);
	}
	//----------------------------------------
	
	/**
	 * importantisimo! el image id es el del autoenumerado del TEXTO. es decir, el titulo donde dice 
	 * "imagen 1", "imagen 2", "imagen 3". 
	 * las imagenes en la version original de este codigo se buscaban por NOMBRE ("gráficos1", "gráficos2"..)
	 * PEEEEROO.. resulta que durante la edición del documento, este nombre siempre es constante. o sea que 
	 * es muy posible que la primera imagen "Imagen 1: bla bla", corresponda a "Gráficos23". 
	 * la buena noticia es que "Gráficos23" aparecerá de primeras en la colección de gráficos. 
	 * o sea que la solución es NO buscar por nombre, sino por posición en la colección de gráficos. 
	 */
	public XPropertySet exportImage(int pos, String dir_path, String prefix) throws com.sun.star.uno.Exception, java.io.IOException 
	{
		XTextGraphicObjectsSupplier supplier = (XTextGraphicObjectsSupplier)
		UnoRuntime.queryInterface(XTextGraphicObjectsSupplier.class,m_xComponent);
		
		XNameAccess xnames = supplier.getGraphicObjects();
		
		String[] names = xnames.getElementNames();
		
		Object oGraphic = xnames.getByName(names[pos]);
		XTextContent xTextContent = UnoRuntime.queryInterface(XTextContent.class, oGraphic); 
		XPropertySet xPropSet = UnoRuntime.queryInterface(XPropertySet.class, xTextContent); 
		Object oGraphics = xPropSet.getPropertyValue("Graphic");
		XGraphic xGraphic = UnoRuntime.queryInterface(XGraphic.class, oGraphics);
        
	    exportGraphicToLocalFile(xGraphic, dir_path + prefix +pos+".jpg");
	    return xPropSet;
	}
	//-----------------------------------------------------
	

	/**
	    * http://www.oooforum.org/forum/viewtopic.phtml?t=73182
	 * @throws WrappedTargetException 
	 * @throws NoSuchElementException 
	 * @throws IllegalArgumentException 
	    */
	   XTextFrame findFrame(XTextRange xtr) throws NoSuchElementException, WrappedTargetException
	   {
	       XTextRangeCompare xtrc = (XTextRangeCompare)UnoRuntime.queryInterface(
	                       XTextRangeCompare.class,
	                       xtr.getText());

	        XTextFrame [] allFrames = getTextFrames();
	       //theLogger.log(Level.SEVERE, "Got frames " + allFrames.length);
	       for(int i=0; i< allFrames.length; i++) 
	       {
	    	   //System.out.println("a. getting frame anchor for "+i);
               XTextRange frameAnchor = allFrames[i].getAnchor();
               //System.out.println("b. getting frame anchor for "+i);
               //System.out.println("our anchor:"+ xtr.toString()+ " frame anchor:"+ frameAnchor.toString());
               
                try 
                {
					if (xtrc.compareRegionStarts(frameAnchor, xtr) == 0) 
					   {
						   //System.out.println("c. getting frame anchor for "+i);
					       
					       return allFrames[i];
					   }
				}
                catch (IllegalArgumentException e) 
				{
                	//System.out.println("exception: illegal argument.. comparing region start FindFrame method");
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
	        }
	      // theLogger.log(Level.SEVERE, "findFrame Returning null");
	       return null;
	   }
	   //---------------------------------------------
	   
	   
	   private XTextFrame[] getTextFrames() throws NoSuchElementException, WrappedTargetException 
	   {
	       XTextFramesSupplier xtfs = (XTextFramesSupplier) UnoRuntime.queryInterface(XTextFramesSupplier.class, m_xTextDocument);
	       XNameAccess xna = xtfs.getTextFrames();
	       String frameNames[] = xna.getElementNames();
	       XTextFrame[] returnVal = new XTextFrame[frameNames.length];

	       for (int i = 0; i < frameNames.length; i++) {
	               Object frame = xna.getByName(frameNames[i]);
	               XTextFrame xtf = (XTextFrame) UnoRuntime.queryInterface(XTextFrame.class, frame);
	               returnVal[i] = xtf;
	       }
	       return returnVal;
	   } 
	   //--------------------------------------------
	
	public void initOutputTextCursor() 
	{
		m_xOutputTextCursor = this.m_xTextDocument.getText().createTextCursor();
		m_xOutputTextCursor.gotoStart(false);	
	}
	//----------------------------

	/**
	 * uses m_xOutputTextCursor, see initOutputTextCursor method.
	 */
	public void writePlainText(String text) 
	{
		
	}
	//-----------------------------------

	/**
	 * export the current document to html, using the openoffice builtin functionality. 
	 * url must be in the form: file:///D:/GeneratedDocs/PDFTest.html
	 * @throws IOException 
	 */
	public void exportAsHTML(String urlTableHtml) throws IOException 
	{
		LogConsole.getInstance().log("TextDocument:exportAsHTML: urlTableHTML: "+ urlTableHtml);
		
		//XModel model = (XModel) UnoRuntime.queryInterface(XModel.class, m_xTextDocument);
		//XFrame frame = model.getCurrentController().getFrame();
		//frame.activate();
		XStorable storable = (XStorable) UnoRuntime.queryInterface(XStorable.class, m_xComponent);
		
		PropertyValue[] props = new PropertyValue[1];
		
		props[0] = new PropertyValue();
		props[0].Name = "FilterName";
		props[0].Value = "HTML";
		/*props[1] = new PropertyValue();
		props[1].Name = "CompressionMode";
		props[1].Value = "1";
		*/
		storable.storeToURL(urlTableHtml, props); 
	}
	//------------------------------------
	
	
	
	
	
	
}
//--------------------------------------------------
