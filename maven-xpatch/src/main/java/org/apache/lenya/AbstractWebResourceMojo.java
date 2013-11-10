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
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.w3c.dom.DOMException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Goal that export content of an in jar folder into another "to build" folder
 *
 */
public abstract class AbstractWebResourceMojo extends AbstractMojo {
	
    protected void runWebResource(File jarDependency,String originFolder, File target) throws MojoExecutionException {
    	
		//then use jar file for exploring
        try{
        	final InputStream is = new FileInputStream(jarDependency);
			ArchiveInputStream in = new ArchiveStreamFactory().createArchiveInputStream("jar", is);
			JarArchiveEntry entry = (JarArchiveEntry)in.getNextEntry();
			
			getLog().info("process file");
			
        	//get all files that are in the configured folder
			while(entry != null){
				if(entry.getName().startsWith(originFolder) && !excluded(entry.getName(),originFolder)){
					//TODO : how to deal with override ?
					// ==> make a test case with another dependency that have same folder and same file
					//String entryPart = entry.getName().replaceFirst(originFolder, "");
    				//File fName2 = new File(target,entryPart);
					File fName2 = new File(target,entry.getName());
    				//extract file from jar
    				//File resultFile = File.createTempFile("jarpatch", fName);
					//File resultFile = new File(fName2);
    				getLog().info("copy from : " + entry.getName() + " to : "+ fName2.getAbsolutePath());
					if(entry.isDirectory()) fName2.mkdirs();
					else{
						OutputStream out = new FileOutputStream(fName2);
        				IOUtils.copy(in, out);
        				out.close();
					}
        		}
        		entry = (JarArchiveEntry)in.getNextEntry();
        	}
        	
			//now close the archive input stream
			in.close();
        	
        	
        }catch ( IOException e ){
            throw new MojoExecutionException( "Error processing jar : "+jarDependency.toString(), e );
		} catch (DOMException e) {
			throw new MojoExecutionException( "Error during parsing of the document to patch", e );
		} catch (ArchiveException e) {
			throw new MojoExecutionException( "Error during reading the jar archive", e );
		}
    	
    }

	private boolean excluded(String name,String sourceFolder) {
		//the root folder is not included
		boolean result = name.equals(sourceFolder+"/");
		// TODO create a configurable regex exclude list
		/*String part = name.replaceFirst(resourcesFolder+"/", "");
		for(r in regexList){
			if(result) break;
			result = part.matches(r));
		}*/
		return result;
	}
    
    
}
