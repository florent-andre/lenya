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
import org.apache.maven.plugin.MojoExecutionException;
import java.io.File;
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
    	
    }
    
}
