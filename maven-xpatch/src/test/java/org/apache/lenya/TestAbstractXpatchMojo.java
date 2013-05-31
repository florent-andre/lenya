package org.apache.lenya;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.io.FileUtils;
import org.apache.lenya.filter.ResourcesFilter;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.xpath.XPathAPI;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class TestAbstractXpatchMojo extends AbstractXpatchMojo {

	public void execute() throws MojoExecutionException{
		super.execute();
	}
	
	public void testNodes(Document patched, String xpath, int number) throws TransformerException{
		NodeList nodes = XPathAPI.selectNodeList(patched, xpath);
		assertEquals(number + " " + xpath, number, nodes.getLength());
	}
	
	@Test
	public void testMultiplePatches() throws MojoExecutionException, ParserConfigurationException, SAXException, IOException, TransformerException{
		String resourcesFolder = "testAbstractXpatch";
		String basePath = "/"+resourcesFolder+"/";
		String archiveName = "core-impl-2.6-SNAPSHOT.jar";
		String cocoonFileName = "cocoon.xconf";
		
		Artifact archive = new DefaultArtifact("test", "test", "0.0", "compile", "jar", "", null);
		archive.setFile(new File(this.getClass().getResource(basePath+archiveName).getFile()));
		
		//List<File> archive = new ArrayList<File>();
		//archive.add());
		
		String patchFolder = "META-INF/patches";
		resourcesFilter = new ResourcesFilter(patchFolder);
		
		File cocoonXconf = new File(this.getClass().getResource(basePath + cocoonFileName).getFile());
		
		String fileNameRegexOne = ".xconf";
    	File fileToPatchOne = cocoonXconf;
    	
    	//This is not effective
    	String fileNameRegexTwo = ".DISABLED";
    	File fileToPatchTwo = new File(".");
    	
		runXpatch(archive, fileNameRegexOne, fileToPatchOne, fileNameRegexTwo, fileToPatchTwo, null,null);
		
		Document patched = XpatchTest.getDomDocument(cocoonXconf);
		
		getLog().info("================ Result File content");
		getLog().info(FileUtils.readFileToString(cocoonXconf));
		
		testNodes(patched, "/cocoon/component", 6); //8
		
		testNodes(patched, "/cocoon/input-modules/component-instance", 12);
		
		testNodes(patched, "/cocoon/document-builders", 1);
		
		//testNodes(patched, "/cocoon/input-modules", 12);
		
		testNodes(patched, "/cocoon/@user-roles", 1);
		
		testNodes(patched, "/cocoon/site-managers", 1);
		
		testNodes(patched, "/cocoon/store-janitor", 1);
		
		testNodes(patched, "/cocoon/meta-data", 1);
		
		testNodes(patched, "/cocoon/resource-types", 1);
		
		testNodes(patched, "/cocoon/template-instantiators", 1);
		
		//==> TO see depends on the patch.webapp property (see definition file : /core-impl/src/main/resources/META-INF/patches/conf/scheduler/add_datasources_element.xconf)
		//testNodes(patched, "/cocoon/datasources", 1);
		
		//same as before
		//testNodes(patched, "/cocoon/datasources/jdbc", 1);
		
		
		testNodes(patched, "/cocoon/source-factories/component-instance", 6);
		
	}

}
