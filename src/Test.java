import com.cesarpachon.openoffice.textdocument.*; 
import com.cesarpachon.inspector.*;

public class Test{

  public static void main(String[] args){
    System.out.println("testing..");   
    String url = "file:///home/cesar/Projects/odt2html/resources/unidad01.odt"; 
    try{
      TextDocument doc = new TextDocument(url); 
      TextDocumentInspectorToConsole inspector = 
        new TextDocumentInspectorToConsole(doc);
      inspector.beginInspection();
      doc.close();
    }catch(Exception e){
     e.printStackTrace(); 
    }
  }
}
