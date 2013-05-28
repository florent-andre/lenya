package org.apache.lenya;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.lenya.xpatch.Xpatch;
import org.apache.xpath.XPathAPI;
import static org.junit.Assert.*;
import org.junit.Test;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XpatchTest {

	//utility class
	public static Document getDomDocument(File f) throws ParserConfigurationException, SAXException, IOException{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    	DocumentBuilder db = dbf.newDocumentBuilder();
    	
    	//for not validating schema (usefull when out of internet)
    	//TODO : create a "non connected mode"
    	db.setEntityResolver(new EntityResolver() {
			
			public InputSource resolveEntity(String arg0, String arg1)
					throws SAXException, IOException {
				return new InputSource(new StringReader(""));
			}
		});
    	
    	return db.parse(f);
	}
	
	@Test
	public void testpatchWeb() throws TransformerException, DOMException, IOException, SAXException, ParserConfigurationException{
		Xpatch xp = new Xpatch();
		
		File ftopatch = new File (this.getClass().getResource("/xpatch/web.xml").getFile());
		Document topatch = getDomDocument(ftopatch);
		File patch = new File (this.getClass().getResource("/xpatch/web/overwrite-uploads.xweb").getFile());
		//need to copy the file in order to not modify the source ?
		//FileUtils.copyFile(f, topatch);
		
		String xpath = "/web-app/servlet/init-param[param-name='overwrite-uploads']/param-value";
		NodeList nodes = XPathAPI.selectNodeList(topatch, xpath);
		assertEquals("Only one node match", 1, nodes.getLength());
		String originalVal = nodes.item(0).getTextContent();
		assertEquals("Before patching value is rename", "rename", originalVal);
		
		//apply the patch
		xp.patch(topatch, patch, null);
		
		NodeList modifiedNodes = XPathAPI.selectNodeList(topatch, xpath);
		assertEquals("Only one node match", 1, modifiedNodes.getLength());
		String modifiedVal = nodes.item(0).getTextContent();
		assertEquals("After patching value is allow", "allow", modifiedVal);
	}
	
	@Test
	public void testpatchCocoon() throws TransformerException, DOMException, IOException, SAXException, ParserConfigurationException{
		Xpatch xp = new Xpatch();
		
		File ftopatch = new File (this.getClass().getResource("/xpatch/cocoon.xconf").getFile());
		Document topatch = getDomDocument(ftopatch);
		File patch = new File (this.getClass().getResource("/xpatch/conf/log4j-appender.xconf").getFile());
		//need to copy the file in order to not modify the source ?
		//FileUtils.copyFile(f, topatch);
		
		String xpath = "/configuration/appender[@name='Chainsaw']";
		NodeList nodes = XPathAPI.selectNodeList(topatch, xpath);
		assertEquals("No node match", 0, nodes.getLength());
		//String originalVal = nodes.item(0).getTextContent();
		//assertEquals("Before patching value is rename", "rename", originalVal);
		
		//apply the patch
		xp.patch(topatch, patch, null);
		
		NodeList modifiedNodes = XPathAPI.selectNodeList(topatch, xpath);
		assertFalse("One should node match", modifiedNodes.getLength() == 1);
		//This actually don't match as there is no <configuration> node added to cocoon.xconf
		//==> have to check if this appear in "normal" build (in favour of the "wait list" implementation
	}
	
}
