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
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public abstract class AbstractXpatchMojo extends AbstractMojo {
	
    protected void runXpatch(File f, String patchFolder,
    		String fileNameRegexOne, File fileToPatchOne,
    		String fileNameRegexTwo, File fileToPatchTwo,
    		Properties properties
    	) throws MojoExecutionException {
    	
    	    	
		try{
        	final InputStream is = new FileInputStream(f);
			ArchiveInputStream in = new ArchiveStreamFactory().createArchiveInputStream("jar", is);
			JarArchiveEntry entry = (JarArchiveEntry)in.getNextEntry();
			
        	//get all files that are in the configured folder
			while(entry != null){
				if(entry.getName().startsWith(patchFolder)){
        			
					File fileToPatch = null;
					if(entry.getName().endsWith(fileNameRegexOne)){
						fileToPatch = fileToPatchOne;
        			}
					if(entry.getName().endsWith(fileNameRegexTwo)){
						fileToPatch = fileToPatchTwo;
        			}
					
					
					if(fileToPatch != null){
						File fName = new File(entry.getName());
        				getLog().info("** apply patch file : " + fName.toString() + " **");
        				//extract file from jar
        				File resultFile = File.createTempFile("jarpatch", fName.getName());
        				OutputStream out = new FileOutputStream(resultFile);
        				IOUtils.copy(in, out);
        				out.close();
        				getLog().info("Temp file created");
        				
        				//get dom representation of the document
        				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        		    	DocumentBuilder db = dbf.newDocumentBuilder();
        		    	Document docToPatch = db.parse(fileToPatch);
        		    	getLog().info("parsing the file to patch is okay");
        		    	
        				//TODO : put patch create on the top level
        				Xpatch xp = new Xpatch();
        				xp.patch(docToPatch, resultFile,properties);
        				getLog().info("Patching the document is okay");
        				
        				//save the patched document
        				//TODO : extract this to do at the end
        				TransformerFactory transformerFactory = TransformerFactory.newInstance();
        			    Transformer transformer = transformerFactory.newTransformer();
        			    DOMSource source = new DOMSource(docToPatch);
        			    StreamResult streamResult =  new StreamResult(fileToPatch);
        			    transformer.transform(source, streamResult);
        			    getLog().info("Patched document is now saved");
					}
					
        		}
        		
        		entry = (JarArchiveEntry)in.getNextEntry();
        	}
        	
			//now close the archive input stream
			in.close();
        	
        	
            }catch ( IOException e ){
                throw new MojoExecutionException( "Error accessing file "+f.toString(), e );
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
    
    
}
