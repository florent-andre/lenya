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
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang.StringUtils;
import org.apache.lenya.filter.ResourcesFilter;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.ProjectDependenciesResolver;
import org.codehaus.plexus.component.annotations.Requirement;
import org.sonatype.aether.collection.DependencyManager;
import org.sonatype.aether.util.graph.TreeDependencyVisitor;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Goal which patch the file for the tests
 *
 * @goal testxpatch
 * 
 * @phase process-test-resources
 * 
 * @requiresDependencyResolution compile
 */
public class XpatchTestMojo extends AbstractXpatchMojo {
    /**
     * Location of the file.
     * @parameter property="project.build.testOutputDirectory"
     * @required
     */
    private File outputDirectory;

    
    
    //private ProjectDependenciesResolver depResolver;
    
    public void execute() throws MojoExecutionException {
    	super.execute();
    	//TODO : make this parameters configurable
    	//folder that contains patches
    	//String patchFolder = "META-INF/patches";
    	
    	
    	
    	File baseFile = new File(outputDirectory, "");
    	File xtestFile = new File(baseFile,"org/apache/lenya/cms/LenyaTestCase.xtest");
    	
    	//TODO : create a map {patchRegex, fileToPatch } for configuring who patch what
    	String fileNameRegexOne = ".xweb";
    	File fileToPatchOne = xtestFile;
    	
    	String fileNameRegexTwo = ".xconf";
    	File fileToPatchTwo = xtestFile;
    	//end make this parameters configurable
    	
    	//TODO : make it configurable
    	boolean getTransitives = true;
    	
    	Set<Artifact> dal;
    	if(getTransitives){
    		dal = mavenProject.getArtifacts();
    	}else{
    		dal = mavenProject.getDependencyArtifacts();
    	}
    	
//    	TODO : 
//    		permettre l'apply des patchs "locaux"
//    		continuer le changement de l'abstract pour la prise en compte des lest de file
//    	
    	getLog().debug("====== Start dep Artifacts =====");
    	//TODO : a filter procedure for artifact
    	for(Artifact da : dal){
    		getLog().info("Xpatch : processing Artifact : " + da.toString());
			runXpatch(da, fileNameRegexOne, fileToPatchOne, fileNameRegexTwo, fileToPatchTwo, mavenProject.getProperties());
			
    		
//    		File f = da.getFile();
//    		//sometimes file is null
//    		if(f != null){
//    			    			
//    		}else{
//    			getLog().warn("Xpatch : jar from Artifact : " + da.toString() + " is empty.");
//    		}
            //then use jar file for exploring
            
    	}
    	
//    	getLog().debug("====== Start All deplist =====");
//   	 List<Dependency> depsss = dependencies;
//   	for(Dependency dep : depsss){
//   		
//   		getLog().debug("---> " + dep.toString());
//   		//getLog().debug("---> " + dep.getFile().getName());
//   	}
    	//depResolver.resolve(arg0);
    	
//    	getLog().debug("====== Start All artifacts =====");
//    	 
//    	for(Artifact dep : deps){
//    		
//    		getLog().debug("---> " + dep.toString());
//    		getLog().debug("---> " + dep.getFile().getName());
//    	}
    	
//    	getLog().debug("====== Start list Attached =====");
//    	List<Artifact> attached = mavenProject.getAttachedArtifacts();
//    	for(Artifact da : attached){
//    		getLog().debug(da.getFile().toString());
//    	}
//    	
//    	getLog().debug("====== Start list Extension =====");
//    	Set<Artifact> extentions = mavenProject.getExtensionArtifacts();
//    	for(Artifact da : extentions){
//    		getLog().debug(da.getFile().toString());
//    	}
    	
    	
//    	getLog().debug("====== Start resources =====");
//    	List<Resource> resources = mavenProject.getResources();
//    	for(Resource resource : resources){
//    		getLog().debug("+++++++++++" + resource.getDirectory());
//    		getLog().debug("+++++++++++" + resource.getTargetPath());
//    		
//    	}
    	
    	getLog().debug("====== Start output resources =====");
    	
    	//TODO : make it configurable : only the test resources or the normal resources, or both
    	
    	Collection<File> resourceFilesList = new ArrayList<File>();
    	List<Resource> resources = mavenProject.getBuild().getResources();
    	for(Resource resource : resources){
    		
    		if(resource != null){
    			File resourceFolder = new File(resource.getDirectory());
    			Collection<File> resourcesFiles = FileUtils.listFiles(resourceFolder, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
    			
    			for(File f : resourcesFiles){
    				
    				//TODO : use the Apache lib for managing file don't care where they come...
    				String relativUri = f.getAbsolutePath().replace(resourceFolder.getPath() +"/", "");
    				getLog().info("*** relativ url == "+relativUri);
    				if(resourcesFilter.isToKeep(relativUri)){
    					resourceFilesList.add(f);
    				}
    			}
    			
    			
    		}
	}
    	
    	
    	///now run the patches on the project
    	runXpatch(resourceFilesList, fileNameRegexOne, fileToPatchOne, fileNameRegexTwo, fileToPatchTwo, mavenProject.getProperties());
    	
    	//Collection<File> files = FileUtils.listFiles(outputDirectory, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
    	
    	getLog().info("FILE TO PROCESSSSSSSZSSSSSSSSSSSSSSSSSss");
    	for(File f : resourceFilesList){
    		getLog().info("+++++++++++" + f.toString());
    		
    		//getLog().debug("dqfmlkjqdf :: " + f.toURI().relativize(mavenProject.get))
    	}
    	
//    	getLog().debug("====== Start build resources =====");
//    	List<Resource> buildr = mavenProject.getBuild().getResources();
//    	for(Resource resource : buildr){
//    		getLog().debug("+++++++++++" + resource.getDirectory());
//    		getLog().debug("+++++++++++" + resource.getTargetPath());
//    		
//    	}
    	
    	
    	
    }
    
    
}
