
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;


class SAXValidator {
	   public static void main(String[] args) {
	      try {
	         File dtd = new File("./adressen.xml");
	         SAXParserFactory f = SAXParserFactory.newInstance();
	         f.setValidating(true); // Default is false         
	         SAXParser p = f.newSAXParser();
	         DefaultHandler h = new MyErrorHandler();
	         p.parse(dtd,h);
	      } catch (ParserConfigurationException e) {
	         System.out.println(e.toString()); 
	      } catch (SAXException e) {
	         System.out.println(e.toString()); 
	      } catch (IOException e) {
	         System.out.println(e.toString()); 
	      }
	   }
	   private static class MyErrorHandler extends DefaultHandler {
	      public void warning(SAXParseException e) throws SAXException {
	         System.out.println("Warning: "); 
	         printInfo(e);
	      }
	      public void error(SAXParseException e) throws SAXException {
	         System.out.println("Error: "); 
	         printInfo(e);
	      }
	      public void fatalError(SAXParseException e) throws SAXException {
	         System.out.println("Fattal error: "); 
	         printInfo(e);
	      }
	      private void printInfo(SAXParseException e) {
	         System.out.println("   Public ID: "+e.getPublicId());
	         System.out.println("   System ID: "+e.getSystemId());
	         System.out.println("   Line number: "+e.getLineNumber());
	         System.out.println("   Column number: "+e.getColumnNumber());
	         System.out.println("   Message: "+e.getMessage());
	      }
	   }
}
	
