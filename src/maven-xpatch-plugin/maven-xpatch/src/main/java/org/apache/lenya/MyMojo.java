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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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
    	System.out.println("*************************************");
    	//TODO : make this parameters configurable
    	//folder that contains patches
    	String patchFolder = "META-INF/patches";
    	//TODO : create a map {patchRegex, fileToPatch }
    	String fileNameRegexOne = "*.xweb";
    	String fileToPatchOne = "webapp/WEB-INF/web.xml";
    	String fileNameRegexTwo = "*.xconf";
    	String fileToPatchTwo = "webapp/WEB-INF/cocoon.xconf";
    	//end make this paramters configurable
    	
    	@SuppressWarnings("unchecked")
		Set<Artifact> al = mavenProject.getArtifacts();
    	Set<Artifact> dal = mavenProject.getDependencyArtifacts();
    	List<Dependency> dl = mavenProject.getDependencies();
    	System.out.println("*************************************");
    	for(Artifact a : al){
    		System.out.println(a.getArtifactId());
    	}
    	System.out.println("*************************************");
    	for(Artifact da : dal){
    		//TODO : a filter procedure for artifact
    		System.out.println(da.getArtifactId());
    		System.out.println(da.getFile().getAbsoluteFile());
    		File f = da.getFile();
            //then use jar file for exploring
            try{
            	System.out.println(f.getCanonicalPath());
            	
            	JarFile jf = new JarFile(f);
            	Enumeration<JarEntry> entries = jf.entries();
            	
            	//get all files that are in the configured folder
            	while(entries.hasMoreElements()){
            		JarEntry ja = entries.nextElement();
            		System.out.println(ja.getName());
            		if(ja.getName().matches(patchFolder)){
            			System.out.println("This match !!");
            		}
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
