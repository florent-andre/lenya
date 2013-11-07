package org.apache.lenya;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.jar.JarArchiveEntry;
import org.apache.commons.io.IOUtils;
import org.apache.lenya.xpatch.Xpatch;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Goal which touches a timestamp file.
 *
 * @goal xpatch
 * 
 * @phase process-sources
 * 
 * @requiresDependencyResolution compile
 */
public class XpatchMojo extends AbstractXpatchMojo {
	
	//TODO : see to use java5 annotations : 
    //http://maven.apache.org/plugin-tools/maven-plugin-plugin/examples/using-annotations.html
	
    /**
     * Location of the file.
     * @parameter property="project.build.outputDirectory"
     * @required
     */
    private File outputDirectory;

    /** @parameter default-value="${project}" */
    private org.apache.maven.project.MavenProject mavenProject;
    
    public void execute() throws MojoExecutionException {
    	super.execute();
    	
    	//TODO : make this parameters configurable
    	//folder that contains patches
    	//String patchFolder = "META-INF/patches";
    	//TODO : create a map {patchRegex, fileToPatch }
    	String fileNameRegexOne = ".xweb";
    	//String baseFile = mavenProject.getBuild().getOutputDirectory() + "/webresources/WEB-INF"; 
    	File baseFile = new File(outputDirectory, "/webresources/WEB-INF");
    	File fileToPatchOne = new File(baseFile,"/web.xml");
    	
    	String fileNameRegexTwo = ".xconf";
    	File fileToPatchTwo = new File(baseFile,"/cocoon.xconf");
    	//end make this parameters configurable
    	
//    	@SuppressWarnings("unchecked")
//    	Set<Artifact> dal = mavenProject.getDependencyArtifacts();
    	
    	// TODO : make it configurable
		boolean getTransitives = true;

		Set<Artifact> dal;
		if (getTransitives) {
			dal = mavenProject.getArtifacts();
		} else {
			dal = mavenProject.getDependencyArtifacts();
		}
		
		getLog().debug("====== Start dep Artifacts =====");
		// TODO : a filter procedure for artifact
		for (Artifact da : dal) {
			getLog().info("Xpatch : processing Artifact : " + da.toString());
			runXpatch(da, fileNameRegexOne, fileToPatchOne, fileNameRegexTwo,
					fileToPatchTwo, mavenProject.getProperties(),null);
		}
		
		getLog().warn("No 'in project' file processing, see XpatchTestMojo for implementing it if requiered");
		
    	
    }
    
    
}
