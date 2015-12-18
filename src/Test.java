import com.cesarpachon.openoffice.textdocument.*; 
import com.cesarpachon.inspector.*;
import com.cesarpachon.services.*;
import java.io.*; 

public class Test{

  public static void main(String[] args){
    System.out.println("testing..");   
    try{
      String url = "file:///home/cesar/Projects/odt2html/input/taller_matematicas01.odt"; 
      TextDocument doc = new TextDocument(url); 
      TextDocumentInspectorToSCORM inspector = 
        new TextDocumentInspectorToSCORM(doc);
    
      inspector.beginInspection();
      inspector.beginContentGeneration();
      inspector.end(); 
    }catch(Exception e){
     e.printStackTrace(); 
    }
  }
}
