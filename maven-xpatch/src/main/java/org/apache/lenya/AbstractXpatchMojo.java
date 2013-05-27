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
import org.apache.lenya.filter.ResourcesFilter;
import org.apache.lenya.xpatch.Xpatch;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
	
	/**
     * @parameter default-value="META-INF/patches"
     * @required
     */
    public String patchFolder;
    
    /** @parameter default-value="${project}" */
    protected org.apache.maven.project.MavenProject mavenProject;
    
    protected ResourcesFilter resourcesFilter;
	
    public void execute() throws MojoExecutionException{
    	resourcesFilter = new ResourcesFilter(patchFolder);
	}
	
	protected void runXpatch(Artifact dependency,
    		String fileNameRegexOne, File fileToPatchOne,
    		String fileNameRegexTwo, File fileToPatchTwo,
    		Properties properties
    	) throws MojoExecutionException {
    	
		if(dependency.getFile() != null){
			
			List<File> patchFiles = new ArrayList<File>();	
			try{
	        	final InputStream is = new FileInputStream(dependency.getFile());
				ArchiveInputStream in = new ArchiveStreamFactory().createArchiveInputStream("jar", is);
				JarArchiveEntry entry = (JarArchiveEntry)in.getNextEntry();
				
	        	//get all files that are in the configured folder
				while(entry != null){
					if(resourcesFilter.isToKeep(entry.getName())){
						
							File fName = new File(entry.getName());
	        				//extract file from jar
	        				File resultFile = File.createTempFile(dependency.getArtifactId() + "-", "-"+fName.getName());
	        				//getLog().info("Apply patch file : " + fName.toString() + " **** " + resultFile.toString() );
	        				OutputStream out = new FileOutputStream(resultFile);
	        				IOUtils.copy(in, out);
	        				out.close();
	        				
	        				patchFiles.add(resultFile);
						}
					
		       		entry = (JarArchiveEntry)in.getNextEntry();
		       		
	        		}
				//now close the archive input stream
				in.close();
	        	
	            }catch ( IOException e ){
	                throw new MojoExecutionException( "Error accessing file "+dependency.toString(), e );
//	            } catch (ParserConfigurationException e) {
//	            	throw new MojoExecutionException( "Error during parsing of the document to patch", e );
				} catch (DOMException e) {
					throw new MojoExecutionException( "Error during parsing of the document to patch", e );
//				} catch (TransformerException e) {
//					throw new MojoExecutionException( "Error during parsing of the document to patch", e );
//				} catch (SAXException e) {
//					throw new MojoExecutionException( "Error during parsing of the document to patch", e );
				} catch (ArchiveException e) {
					throw new MojoExecutionException( "Error during reading the jar archive", e );
				}
			
			runXpatch(patchFiles, fileNameRegexOne, fileToPatchOne, fileNameRegexTwo, fileToPatchTwo, properties);
			
		}
    	

    }

	
protected void runXpatch(Collection<File> f,
		String fileNameRegexOne, File fileToPatchOne,
		String fileNameRegexTwo, File fileToPatchTwo,
		Properties properties
	) throws MojoExecutionException {
	
	
	try{
		
	
	for(File patch : f){
		File fileToPatch = null;
		if(patch.getName().endsWith(fileNameRegexOne)){
			fileToPatch = fileToPatchOne;
		}
		if(patch.getName().endsWith(fileNameRegexTwo)){
			fileToPatch = fileToPatchTwo;
		}
		
		
		if(fileToPatch != null){
			
			//get dom representation of the document
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    	DocumentBuilder db = dbf.newDocumentBuilder();
	    	Document docToPatch = db.parse(fileToPatch);
	    	
			//TODO : put patch create on the top level
			Xpatch xp = new Xpatch();
			boolean result = xp.patch(docToPatch, patch,properties);
			if(result){
				getLog().info("--> " +patch.getAbsolutePath()+ " : OK");
			}else{
				getLog().warn("--> " +patch.getAbsolutePath()+ " : not applied");
			}
			
			
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
//		} catch (ArchiveException e) {
//			throw new MojoExecutionException( "Error during reading the jar archive", e );
		}

}



//    protected void runXpatch(File f, String patchFolder,
//    		String fileNameRegexOne, File fileToPatchOne,
//    		String fileNameRegexTwo, File fileToPatchTwo,
//    		Properties properties
//    	) throws MojoExecutionException {
//    	
//    	    	
//		try{
//        	final InputStream is = new FileInputStream(f);
//			ArchiveInputStream in = new ArchiveStreamFactory().createArchiveInputStream("jar", is);
//			JarArchiveEntry entry = (JarArchiveEntry)in.getNextEntry();
//			
//        	//get all files that are in the configured folder
//			while(entry != null){
//				if(entry.getName().startsWith(patchFolder)){
//        			
//					File fileToPatch = null;
//					if(entry.getName().endsWith(fileNameRegexOne)){
//						fileToPatch = fileToPatchOne;
//        			}
//					if(entry.getName().endsWith(fileNameRegexTwo)){
//						fileToPatch = fileToPatchTwo;
//        			}
//					
//					
//					if(fileToPatch != null){
//						File fName = new File(entry.getName());
//        				getLog().info("Apply patch file : " + fName.toString() + " **");
//        				//extract file from jar
//        				File resultFile = File.createTempFile("jarpatch", fName.getName());
//        				OutputStream out = new FileOutputStream(resultFile);
//        				IOUtils.copy(in, out);
//        				out.close();
//        				
//        				//get dom representation of the document
//        				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//        		    	DocumentBuilder db = dbf.newDocumentBuilder();
//        		    	Document docToPatch = db.parse(fileToPatch);
//        		    	
//        				//TODO : put patch create on the top level
//        				Xpatch xp = new Xpatch();
//        				boolean result = xp.patch(docToPatch, resultFile,properties);
//        				if(result){
//        					getLog().info("--> Patching is okay");
//        				}else{
//        					getLog().warn("--> Patch don't occur");
//        				}
//        				
//        				
//        				//save the patched document
//        				//TODO : extract this to do at the end
//        				TransformerFactory transformerFactory = TransformerFactory.newInstance();
//        			    Transformer transformer = transformerFactory.newTransformer();
//        			    DOMSource source = new DOMSource(docToPatch);
//        			    StreamResult streamResult =  new StreamResult(fileToPatch);
//        			    transformer.transform(source, streamResult);
//        			    getLog().info("Patched document is now saved");
//					}
//					
//        		}
//        		
//        		entry = (JarArchiveEntry)in.getNextEntry();
//        	}
//        	
//			//now close the archive input stream
//			in.close();
//        	
//        	
//            }catch ( IOException e ){
//                throw new MojoExecutionException( "Error accessing file "+f.toString(), e );
//            } catch (ParserConfigurationException e) {
//            	throw new MojoExecutionException( "Error during parsing of the document to patch", e );
//			} catch (DOMException e) {
//				throw new MojoExecutionException( "Error during parsing of the document to patch", e );
//			} catch (TransformerException e) {
//				throw new MojoExecutionException( "Error during parsing of the document to patch", e );
//			} catch (SAXException e) {
//				throw new MojoExecutionException( "Error during parsing of the document to patch", e );
//			} catch (ArchiveException e) {
//				throw new MojoExecutionException( "Error during reading the jar archive", e );
//			}
//
//    }
    
    
}
