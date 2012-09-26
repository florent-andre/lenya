package org.apache.lenya;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.io.FileUtils;
import org.apache.lenya.xpatch.Xpatch;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import junit.framework.TestCase;

public class XpatchTest extends TestCase {

	//utility class
	public Document getDomDocument(File f) throws ParserConfigurationException, SAXException, IOException{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    	DocumentBuilder db = dbf.newDocumentBuilder(); 
    	return db.parse(f);
	}
	
	//@Test
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
	
	//@Test
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
