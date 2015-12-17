import com.cesarpachon.openoffice.textdocument.*; 
import com.cesarpachon.inspector.*;
import com.cesarpachon.services.*;
import java.util.Properties;
import java.io.*; 

public class Test{

  public static void main(String[] args){
    System.out.println("testing..");   
    try{
	   	Properties properties = new Properties();
	    properties.load(new FileInputStream(new File("/home/cesar/Projects/odt2html/resources/repositorio.properties")));
      RepositoryTO repo = new RepositoryTO(); 
      repo.setCurrentUnit("01"); 
      repo.loadFromProperties(properties);
      String url = repo.getUrlUnidad(); 
      TextDocument doc = new TextDocument(url); 
      TextDocumentInspectorToSCORM inspector = 
        new TextDocumentInspectorToSCORM(doc, repo);
    
      inspector.beginInspection();
      inspector.beginContentGeneration();
      inspector.end(); 
    }catch(Exception e){
     e.printStackTrace(); 
    }
  }
}
