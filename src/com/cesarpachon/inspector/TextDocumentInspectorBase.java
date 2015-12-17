/*
 * Created on 12/03/2011
 */
package com.cesarpachon.inspector;

import java.util.HashMap;
import java.util.HashSet;

import com.cesarpachon.openoffice.textdocument.ITextDocumentVisitor;

public abstract class TextDocumentInspectorBase implements ITextDocumentVisitor
{
	protected static HashMap<String, Integer> g_structureStyles = new HashMap<String, Integer>();
	static
	{
		g_structureStyles.put("Titulo1", new Integer(1));
		g_structureStyles.put("Titulo2", new Integer(2));
		g_structureStyles.put("Titulo3", new Integer(3));
		g_structureStyles.put("Titulo4", new Integer(4));
		g_structureStyles.put("Titulo5", new Integer(5));
	}
	
	/**
	 * solo estilos con nivel menor o igual a este generar�n una pagina nueva
	 */
	protected static final int MIN_STYLE_LEVEL_CREATE_PAGE = 2;
	
	protected static HashMap<String, String> g_specialStylesNames = new HashMap<String, String>();
	static
	{
		g_specialStylesNames.put("Ep�grafe", "epigrafe");
	}
	
	
	/**
	 * this styles are just ignored, will not be included nor marked as error
	 */
	protected static HashSet<String> g_skippableStylesNames = new HashSet<String>();
	static
		{
			//openoffice TOC is not needed for SCORM
		g_skippableStylesNames.add("Contents");
		g_skippableStylesNames.add("Contents ");
		g_skippableStylesNames.add("Table Contents");
		g_skippableStylesNames.add("Contents 1");
		g_skippableStylesNames.add("Contents 2");
		g_skippableStylesNames.add("Contents 3");
		g_skippableStylesNames.add("Contents 4");
		g_skippableStylesNames.add("Contents 5");
		g_skippableStylesNames.add("Contents Heading");
		}
	
	protected static HashMap<String, String> g_validStylesNames = new HashMap<String, String>();
	static
		{
			//titulo del documento
			g_validStylesNames.put("Title", "titulo");

			//subtitulo.. esto realmente se usa para referencias externas
			g_validStylesNames.put("Subtitle", "subtitulo");

			
			//cabeceras, titulos o headers
			g_validStylesNames.put("Heading 1", "Titulo1");
			g_validStylesNames.put("Heading 2", "Titulo2");
			g_validStylesNames.put("Heading 3", "Titulo3");
			g_validStylesNames.put("Heading 4", "Titulo4");
			g_validStylesNames.put("Titulo cuarto nivel", "Titulo4");
			g_validStylesNames.put("Heading 5", "Titulo5");
			
			//cuerpo de texto:
			g_validStylesNames.put("Standard", "cuerpo");
			g_validStylesNames.put("Text body", "cuerpo");
			g_validStylesNames.put("Normal (Web)", "cuerpo");
			g_validStylesNames.put("Text body 1st", "cuerpo");
			
			
			//especiales
			g_validStylesNames.put("balazo", "balazo");
			g_validStylesNames.put("comentario", "comentario");
			
			//listas de vi�etas
			g_validStylesNames.put("Lista de vi�etas", "lista_vinetas");
			g_validStylesNames.put("Bullet Symbols", "lista_vinetas");
			
			//lista de numeros
			g_validStylesNames.put("Lista de numeros", "lista_numeros");
			g_validStylesNames.put("Lista con n�meros", "lista_numeros");
			
			
			//listas de contenido
			g_validStylesNames.put("List Contents", "lista_contenidos");
			g_validStylesNames.put("P�rrafo de lista", "lista_contenidos");
			
			//Ep�grafe
			//asi es como word reconoce los titulos de imagenes y tablas
			//g_validStylesNames.put("Ep�grafe", "epigrafe");
			
			//
			//g_validStylesNames.put("�nfasis intenso", "bold");
						
			//video
			g_validStylesNames.put("enlace video", "video");

			//cita
			g_validStylesNames.put("citas", "citas");
			
			g_validStylesNames.put("italica", "italica");
			

			
		};
		
		
		protected static HashMap<String, String> g_validCharStylesNames = new HashMap<String, String>();
		
		static
			{
				//titulo del documento
			g_validCharStylesNames.put("Intense Emphasis", "enfasis");
			g_validCharStylesNames.put("Emphasis", "enfasis");
			g_validCharStylesNames.put("�nfasis intenso", "enfasis");
			g_validCharStylesNames.put("�nfasis sutil", "enfasis");
			g_validCharStylesNames.put("italica", "italica");
			
			//g_validCharStylesNames.put("Fuente de p�rrafo predeter.", "cuerpo");
			
			};
			
			protected HashMap<Integer, Table> m_tables = new HashMap<Integer, Table>();
			protected HashMap<Integer, Image> m_images = new HashMap<Integer, Image>();
			
			
			public abstract void beginInspection() throws Exception;

}
//------------------------------------------------
