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

import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Goal that export content of the webresources folder into webapp
 *
 * @goal testwebresources
 * 
 * @phase process-test-resources
 */
public class WebResourceTestMojo extends AbstractWebResourceMojo {
    /**
     * Location of the file.
     * @parameter property="project.build.testOutputDirectory"
     * @required
     */
    private File outputDirectory;
    
    /**
     * Test resource folder
     * @parameter property="project.build.testResources"
     * @required
     */
    private List<Resource> testRessources;
    
    private String resourcesFolder;
    
    /** @parameter default-value="${project}" */
    private org.apache.maven.project.MavenProject mavenProject;
    
    public void execute() throws MojoExecutionException {
    	
    	//TODO : make this parameters configurable
    	resourcesFolder = "webresources";
    	File baseFile = new File(outputDirectory, ""); 
    	
    	//end make this parameters configurable
    	
    	Set<Artifact> dal = mavenProject.getDependencyArtifacts();
    	
    	for(Artifact da : dal){
    		//TODO : a filter procedure for artifact
    		File f = da.getFile();
    		
    		//sometimes file from artifact is null... see why 
    		if(f != null){
    			runWebResource(f, resourcesFolder, baseFile);		
    		}
    	}
    	
    	//and then also copy the test resources of the current project
    	
    	System.out.println("test copy des ressources");
    	if(testRessources != null){
    		System.out.println("c'est pas nullllllllllllllllllllllll");
    		for(Resource r : testRessources){
    			System.out.println(r.getDirectory());
    			System.out.println(r.getTargetPath());
    			
    			//TODO : improve this part to make it more generic and configurable
    			File baseResource = new File(r.getDirectory());
    			File directoryToCopy = new File(baseResource,resourcesFolder);
    			//File lenyaDirectory = new File(directoryToCopy,"lenya");
    			
    			File targetFolder = new File(baseFile,"lenya/webapp");
    			
    			if(directoryToCopy.exists()){
    				//copy the content of webresource folder into the lenya/webapp folder
        			try {
    					FileUtils.copyDirectory(directoryToCopy, targetFolder);
    				} catch (IOException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
    			}
    			
    			
    			
    		}
    	}
    	
    }
    
}
