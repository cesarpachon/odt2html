/*
 * Created on 11/03/2011
 */
package com.cesarpachon.services;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import com.cesarpachon.util.FileUtils;
import com.cesarpachon.util.LogConsole;

public class ScormManifestGenerator 
{

	private ArrayList<String> listado; 
	private String m_rutaAbsoluta;
	
	public ScormManifestGenerator()
	{
		listado = new ArrayList<String>();
	}
	
	/**
	 * contentdir: parent folder (outputdir is a subfolder but in absolute path, like "scorm".)
	 * outputdir: path to scorm output, where the manifest will be created
	 * manifesttemplatefile: full path to manifest used as template
	 * @param outputdir
	 * @param manifesttemplatefile
	 * @throws Exception 
	 */
	public void execute(String outputdir, String manifesttemplatefile) throws Exception
	{
		
		LogConsole.getInstance().log("ScormManifestGenerator: iniciando ejecución. paso 1: generar manifiesto.");
		
	    m_rutaAbsoluta = outputdir;
		 listar("");
		String template_content = FileUtils.getFileContent(manifesttemplatefile);

	    StringBuffer salida = new StringBuffer( );
	    Iterator<String> iter = listado.iterator( );
	    while(iter.hasNext())
	    {
	    	salida.append(iter.next( ));
	    }
	    
	    template_content = template_content.replaceAll("_CONTENIDO_", salida.toString());
	    
	    String archivoxmlout = outputdir + System.getProperty("file.separator") + "imsmanifest.xml";
	    
		FileUtils.saveToFile(template_content, archivoxmlout);
		
		LogConsole.getInstance().log("ScormManifestGenerator: paso 2: copiar archivos XSD");
		
		FileUtils.copyFile("C:\\repositorios\\virtualhumboldt_sistemas\\empaquetador_scorm\\imsmd_rootv1p2p1.xsd",m_rutaAbsoluta + "/imsmd_rootv1p2p1.xsd"); 
		FileUtils.copyFile("C:\\repositorios\\virtualhumboldt_sistemas\\empaquetador_scorm\\imscp_rootv1p1p2.xsd",m_rutaAbsoluta + "/imscp_rootv1p1p2.xsd");
		FileUtils.copyFile("C:\\repositorios\\virtualhumboldt_sistemas\\empaquetador_scorm\\ims_xml.xsd",m_rutaAbsoluta + "/ims_xml.xsd");
		FileUtils.copyFile("C:\\repositorios\\virtualhumboldt_sistemas\\empaquetador_scorm\\adlcp_rootv1p2.xsd",m_rutaAbsoluta + "/adlcp_rootv1p2.xsd");

		//LogConsole.getInstance().log("ScormManifestGenerator: paso 3: generar archivo ZIP");
		
		//LogConsole.getInstance().log("ScormManifestGenerator: paso 4: proceso finalizado!");
	}
	//---------------------------------------------
	

	  private void listar(String ruta) throws Exception
	  {
		  
		  String[] archivos = obtenerListaArchivos(new File(m_rutaAbsoluta + "/"+ruta));
		  
		  //System.out.println("LISTANDO "+ ruta + " abs: "+ rutaAbsoluta);
		  
		  if(archivos == null)
		  {
			  System.out.println("ERROR lista de archivos nula: "+ ruta);
			  return;
		  }
		  
		   //System.out.println("encontrados "+archivos.length + " archivos en "+ ruta);
		    
		  for(int i=0; i<archivos.length; ++i)
		    {
		    	String nombreArchivoActual = archivos[i];
		    	//System.out.println("  procesando archivo: "+ nombreArchivoActual);
		    	if(".svn".equals(nombreArchivoActual))
		    			continue;
		    	
		    	File f = new File(m_rutaAbsoluta + "/"+ruta +"/"+ nombreArchivoActual);
		    	if(f.isDirectory() )
		    	{
		    		//System.out.println("es directorio!");
		    		listar(ruta +"/"+ nombreArchivoActual);
		    	}
		    	else
		    	{
		    		//System.out.println("NO es directorio");
		    		String rutaRelativa = ruta + "/"+ nombreArchivoActual;
		    		if(rutaRelativa.startsWith("\\"))
		    		{
		    			rutaRelativa = rutaRelativa.substring(1);
		    		}
		    		//System.out.println("<file href=\""+ rutaRelativa + "\"/>");
		    		listado.add("\t<file href=\""+ rutaRelativa + "\"/>\n");
		    	}
		    }  
		  
	  }
	  //---------------------------------
	  

	  /**
	  * lista de nombres de archivos que tienen la extensión dada
	  */  
	  private String[] obtenerListaArchivos(File dir) throws Exception
	  {
		FilenameFilter fnf = new FilenameFilter(){
	          public boolean accept(File dir, String name)
	            {
	                //return name.endsWith("_"+lenguajeLocal+"."+extension);
					//return !name.endsWith(".svn");
	        	    if("Thumbs.db".equals(name)) return false;
	        	  	return true;
	            }
	        };
	        return dir.list(fnf);  
	   }  
	
}
