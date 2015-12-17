/*
 * Created on 30/11/2010
 */
package com.cesarpachon.util;

public class StringUtils {

	public static String replaceHTMLQuotes(String contenido)
	{
		     //á &aacute; é &eacute; í &iacute; ó &oacute; ú &uacute; ñ &ntilde; 
		     //Á &Aacute; É &Eacute; Í &Iacute; Ó &Oacute; Ú &Uacute; Ñ &Ntilde;
		     //" &quot;  < &lt; > &gt;
		     String nuevo = contenido.replaceAll("á", "&aacute;");
		     nuevo = nuevo.replaceAll("é", "&eacute;");
		     nuevo = nuevo.replaceAll("í", "&iacute;");
		     nuevo = nuevo.replaceAll("ó", "&oacute;");
		     nuevo = nuevo.replaceAll("ú", "&uacute;");
		     nuevo = nuevo.replaceAll("ñ", "&ntilde;");
		     nuevo = nuevo.replaceAll("Á", "&Aacute;");
		     nuevo = nuevo.replaceAll("É", "&Eacute;");
		     nuevo = nuevo.replaceAll("Í", "&Iacute;");
		     nuevo = nuevo.replaceAll("Ó", "&Oacute;");
		     nuevo = nuevo.replaceAll("Ú", "&Uacute;");
		     nuevo = nuevo.replaceAll("Ñ", "&Ntilde;");
		     nuevo = nuevo.replaceAll("ü", "&uuml;");
		     nuevo = nuevo.replaceAll("", "&lsquo;");
		     nuevo = nuevo.replaceAll("", "&rsquo;");
		     nuevo = nuevo.replaceAll("", "&rsquo;");
		     nuevo = nuevo.replaceAll("", "&rdquo;");
		     nuevo = nuevo.replace("¿", "&iquest;");
		     
		     
		      
		     //nuevo = nuevo.replaceAll(" \"", "&quot;");
		     //nuevo = nuevo.replaceAll("\" ", "&quot;");
		     //nuevo = nuevo.replaceAll("<", "&lt;");
		     //nuevo = nuevo.replaceAll(">", "&gt;");
		     return nuevo; 
	}
	
}
