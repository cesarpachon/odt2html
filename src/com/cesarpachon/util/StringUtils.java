/*
 * Created on 30/11/2010
 */
package com.cesarpachon.util;

public class StringUtils {

	public static String replaceHTMLQuotes(String contenido)
	{
		     //� &aacute; � &eacute; � &iacute; � &oacute; � &uacute; � &ntilde; 
		     //� &Aacute; � &Eacute; � &Iacute; � &Oacute; � &Uacute; � &Ntilde;
		     //" &quot;  < &lt; > &gt;
		     String nuevo = contenido.replaceAll("�", "&aacute;");
		     nuevo = nuevo.replaceAll("�", "&eacute;");
		     nuevo = nuevo.replaceAll("�", "&iacute;");
		     nuevo = nuevo.replaceAll("�", "&oacute;");
		     nuevo = nuevo.replaceAll("�", "&uacute;");
		     nuevo = nuevo.replaceAll("�", "&ntilde;");
		     nuevo = nuevo.replaceAll("�", "&Aacute;");
		     nuevo = nuevo.replaceAll("�", "&Eacute;");
		     nuevo = nuevo.replaceAll("�", "&Iacute;");
		     nuevo = nuevo.replaceAll("�", "&Oacute;");
		     nuevo = nuevo.replaceAll("�", "&Uacute;");
		     nuevo = nuevo.replaceAll("�", "&Ntilde;");
		     nuevo = nuevo.replaceAll("�", "&uuml;");
		     nuevo = nuevo.replaceAll("�", "&lsquo;");
		     nuevo = nuevo.replaceAll("�", "&rsquo;");
		     nuevo = nuevo.replaceAll("�", "&rsquo;");
		     nuevo = nuevo.replaceAll("�", "&rdquo;");
		     nuevo = nuevo.replace("�", "&iquest;");
		     
		     
		      
		     //nuevo = nuevo.replaceAll(" \"", "&quot;");
		     //nuevo = nuevo.replaceAll("\" ", "&quot;");
		     //nuevo = nuevo.replaceAll("<", "&lt;");
		     //nuevo = nuevo.replaceAll(">", "&gt;");
		     return nuevo; 
	}
	
}
