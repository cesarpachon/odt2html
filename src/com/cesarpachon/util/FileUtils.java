/*
 * Created on 5/03/2011
 */
package com.cesarpachon.util;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.cesarpachon.exception.IncompleteInformationException;


public class FileUtils 
{
	
	/**
	 * tries to return the content of the filepath as string. if the file does not 
	 * exist in the given path, tries to return the content of the secondary path.
	 * if none exist, throws IOException. 
	 * @throws IOException 
	 * @throws IncompleteInformationException 
	 */
 public static String getFileContent(String filepath, String secondarypath)
 			throws IOException, IncompleteInformationException
 {
	 String content = "";
	 try 
	 {
		content = getFileContent(filepath);
	} catch (FileNotFoundException e) 
	{
		//file not found! try secondary path! 
		content = getFileContent(secondarypath);
	}
	return content;
 }
 //-----------------------------
	
	
	
 /**
  * return the content of the given fullpath as a string	
 * @throws IncompleteInformationException 
  */
 public static String getFileContent(String filepath) throws FileNotFoundException, IOException, IncompleteInformationException
 {
	 if(filepath == null)
	 {
		 String msg = "FileUtils:getFileContent: null parameter provided.";
		 LogConsole.getInstance().log(msg);
		 throw new IncompleteInformationException(msg);
	 }
	 BufferedReader reader = null;
		File fullpath =  new File(filepath);
		if(fullpath.exists())
		{
			reader = new BufferedReader(new FileReader(fullpath));
		}
		else
		{
				throw new FileNotFoundException("NOT FOUND file: "+ fullpath);
		}
		
		//read the content of the file and return it as string
		StringBuffer buff = new StringBuffer();
		String line;
		do
		{
			line = reader.readLine();
			if(line == null) 
				break;
			buff.append(line);
			buff.append("\n");
		}while(true);
		filepath = buff.toString();
		reader.close();
		return filepath;
 }//--------------------

public static void saveToFile(String content, String filepath) throws IOException 
{
	//old way FileWriter writer = new FileWriter(new File(filepath));	
	
	Writer writer = new BufferedWriter(new OutputStreamWriter(
		    new FileOutputStream(filepath), "UTF-8"));

	
	writer.write(content);
	writer.close();	
}
//--------------------------

public static File[] getSubdirectories(String dirName) 
{
	System.out.println("getting subdirectories: "+ dirName);
	File dir = new File(dirName);

	//File[] files = (new File(dirName)).listFiles();
	

    // This filter only returns directories
    FileFilter dirFilter = new FileFilter() {
        public boolean accept(File dir) {
        	//System.out.println("evaluating "+ dir);
            return dir.isDirectory();
        }
    };

    return dir.listFiles(dirFilter);
}
//------------------------------
 
public static String[] getRepositories(File[] subdirs)
{
	ArrayList<String> repositories = new ArrayList<String>();
	for(File subdir:subdirs)
	{
		//test if the subdir has a file named "repositorio.properties"
		String path = subdir.getAbsolutePath();
		path += "/repositorio.properties";
		File f = new File(path);
		//System.out.println("evaluating "+ f);
        if(f.exists()) repositories.add(subdir.getAbsolutePath());
	}
	return repositories.toArray(new String[repositories.size()]);
}
//-------------------------------

public static void copyFile(String orifullpath, String desfullpath) throws IOException 
{
        FileInputStream fis = new FileInputStream(orifullpath);
        FileOutputStream fos = new FileOutputStream(desfullpath);
        copyFile(fis, fos);
        fis.close();
        fos.close();
}
//-----------------------

private static void copyFile(InputStream is, OutputStream os) throws IOException
{
    byte[] buf = new byte[1024];
    int i = 0;
    while((i=is.read(buf))!=-1) 
    {
        os.write(buf, 0, i);
    }
}
//-----------------------


public static void ZipFolder(String zipOutputFullPath,  String folderToZip) throws IOException
{
	LogConsole.getInstance().log("FileUtils:ZipFolder: "+ folderToZip+ " -> "+ zipOutputFullPath );
			//name of zip file to create
			String outFilename = zipOutputFullPath;
			
			//create ZipOutputStream object
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outFilename));
			
			//path to the folder to be zipped
			File zipFolder = new File(folderToZip);
			
			//get path prefix so that the zip file does not contain the whole path
			// eg. if folder to be zipped is /home/lalit/test
			// the zip file when opened will have test folder and not home/lalit/test folder
			
			//int len = zipFolder.getAbsolutePath().lastIndexOf(File.separator);
			//String baseName = zipFolder.getAbsolutePath().substring(0,len+1);
			String baseName = zipFolder.getAbsolutePath();
			
			addFolderToZip(zipFolder, out, baseName);
		
			out.close();		
			LogConsole.getInstance().log("FileUtils:ZipFolder: Carpeta ha sido comprimida." );
			
}
//-----------------------------------------------
 
private static void addFolderToZip(File folder, ZipOutputStream zip, String baseName) throws IOException 
{
	File[] files = folder.listFiles();
	for (File file : files) {
		if (file.isDirectory()) 
		{
			if(!file.getName().equals(".svn"))
				addFolderToZip(file, zip, baseName);
		}
		else 
		{
			String name = file.getAbsolutePath().substring(baseName.length());
			if(name.startsWith("\\"))
			{
				name = name.substring(1);
			}
			ZipEntry zipEntry = new ZipEntry(name);
			zip.putNextEntry(zipEntry);
			copyFile(new FileInputStream(file), zip);
			zip.closeEntry();
		}
	}
}
//-------------------------



}
