/*
 * Created on 6/05/2010
 */
package co.edu.virtualhumboldt.openoffice.textdocument;

import java.io.IOException;
import java.util.Collection;

import com.sun.star.graphic.XGraphic;
import com.sun.star.style.XStyle;

import edu.co.virtualhumboldt.exception.IncompleteInformationException;

public interface ITextDocumentVisitor 
{
	void onUsedStyle(String styleName, XStyle styleSrv) throws Exception;
	void onParagraph(String styleName, Collection<TextPortion> portions) throws Exception;
	void onPlainParagraph(String paraStyleName, String content) throws Exception;
	void onEnterTable() throws IOException, IncompleteInformationException;
	void onEnterCell()throws IOException, IncompleteInformationException;
	void onExitCell()throws IOException, IncompleteInformationException;
	void onExitTable()throws IOException, IncompleteInformationException;
	void onGraphic(TextGraphic graphic);

}
