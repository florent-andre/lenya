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
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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
 * @goal touch
 * @goal war-resources
 * 
 * @phase process-sources
 */
public class MyMojo extends AbstractMojo {
    /**
     * Location of the file.
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private File outputDirectory;

    /** @parameter default-value="${project}" */
    private org.apache.maven.project.MavenProject mavenProject;
    
    public void execute() throws MojoExecutionException {
    	
    	//TODO : make this parameters configurable
    	//folder that contains patches
    	String patchFolder = "META-INF/patches";
    	//TODO : create a map {patchRegex, fileToPatch }
    	String fileNameRegexOne = ".xweb";
    	String baseFile = mavenProject.getBuild().getOutputDirectory() + "/webresources/WEB-INF"; 
    	String fileToPatchOne = baseFile+"/web.xml";
    	
    	String fileNameRegexTwo = ".xconf";
    	String fileToPatchTwo = baseFile+"/cocoon.xconf";
    	//end make this paramters configurable
    	
    	@SuppressWarnings("unchecked")
    	Set<Artifact> dal = mavenProject.getDependencyArtifacts();
    	List<Dependency> dl = mavenProject.getDependencies();
    	
    	System.out.println("*************************************");
    	for(Artifact da : dal){
    		//TODO : a filter procedure for artifact
//    		System.out.println(da.getArtifactId());
//    		System.out.println(da.getFile().getAbsoluteFile());
    		File f = da.getFile();
            //then use jar file for exploring
            try{
            	//System.out.println(f.getCanonicalPath());
            	
//            	JarFile jf = new JarFile(f);
//            	Enumeration<JarEntry> entries = jf.entries();
            	final InputStream is = new FileInputStream(f);
				ArchiveInputStream in = new ArchiveStreamFactory().createArchiveInputStream("jar", is);
				JarArchiveEntry entry = (JarArchiveEntry)in.getNextEntry();
				
            	//get all files that are in the configured folder
            	//while(entries.hasMoreElements()){
				while(entry != null){
					System.out.println("***** new entry "+entry.getName());
            		//JarEntry ja = entries.nextElement();
            		//if(ja.getName().startsWith(patchFolder)){
					if(entry.getName().startsWith(patchFolder)){
            			//if(ja.getName().endsWith(fileNameRegexOne)){
						if(entry.getName().endsWith(fileNameRegexOne)){
            				//System.out.println(ja.getName());
							System.out.println(entry.getName());
							System.out.println(entry.toString());
            				System.out.println("This match PATCH WEB !!");
            				
            				String fName = new File(entry.getName()).getName();
            				System.out.println(fName);
            				
            				//extract file from jar
            				//File resultFile = new File(tempDir, fName);
            				File resultFile = File.createTempFile("jarpatch", fName);
            				//resultFile.createNewFile();
            				OutputStream out = new FileOutputStream(resultFile);
            				IOUtils.copy(in, out);
            				out.close();
            				in.close();
            				
            				//get dom representation of the document
            				File fileToPatch = new File(fileToPatchOne);
            				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            		    	DocumentBuilder db = dbf.newDocumentBuilder();
            		    	Document docToPatch = db.parse(fileToPatch);
            				
            				//TODO : put patch create on the top level
            				Xpatch xp = new Xpatch();
            				xp.patch(docToPatch, resultFile);
            				
            				//save the patched document
            				//TODO : extract this to do at the end
            				TransformerFactory transformerFactory = TransformerFactory.newInstance();
            			    Transformer transformer = transformerFactory.newTransformer();
            			    DOMSource source = new DOMSource(docToPatch);
            			    StreamResult streamResult =  new StreamResult(fileToPatch);
            			    transformer.transform(source, streamResult);
            			    
            				//System.out.println(ja.getClass().getResource(ja.getName()).toString());
            				
            			}
            			//if(ja.getName().endsWith(fileNameRegexTwo)){
						if(entry.getName().endsWith(fileNameRegexTwo)){
            				System.out.println("This match PATCH CONF!!");
            			}
            		}
            		
            		entry = (JarArchiveEntry)in.getNextEntry();
            	}
            	
            	/*System.out.println("test if the jar contain the needed folder : ");
            	System.out.println(jf.getEntry("/META-INF/patch/"));
            	JarEntry e = jf.getJarEntry(patchFolder);
            	e.isDirectory();
            	
            	System.out.println("Process the content of entry");
            	*/
//            	while(entries.hasMoreElements()){
//            		JarEntry ja = entries.nextElement();
//            		System.out.println(ja.getName());
//            		System.out.println(ja.isDirectory());
//            	}
            	
            	//TODO : write the result
            	
            	
            }catch ( IOException e ){
                throw new MojoExecutionException( "Error accessing file "+da.toString(), e );
            } catch (ParserConfigurationException e) {
            	throw new MojoExecutionException( "Error during parsing of the document to patch", e );
			} catch (DOMException e) {
				throw new MojoExecutionException( "Error during parsing of the document to patch", e );
			} catch (TransformerException e) {
				throw new MojoExecutionException( "Error during parsing of the document to patch", e );
			} catch (SAXException e) {
				throw new MojoExecutionException( "Error during parsing of the document to patch", e );
			} catch (ArchiveException e) {
				throw new MojoExecutionException( "Error during reading the jar archive", e );
			}
    	}
    	System.out.println("*************************************");
    	/*for(Dependency d : dl){
    		System.out.println(d.getArtifactId());
    	}*/
    	
        File f = outputDirectory;

        if ( !f.exists() ) {
            f.mkdirs();
        }

        File touch = new File( f, "touch.txt" );

        FileWriter w = null;
        try {
            w = new FileWriter( touch );

            w.write( "touch.txt" );
        }
        catch ( IOException e ) {
            throw new MojoExecutionException( "Error creating file " + touch, e );
        }
        finally {
            if ( w != null ) {
                try {
                    w.close();
                }
                catch ( IOException e ) {
                    // ignore
                }
            }
        }
    }
    
}
