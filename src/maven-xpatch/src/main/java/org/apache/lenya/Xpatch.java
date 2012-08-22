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
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactCollector;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.resolver.filter.ScopeArtifactFilter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
//import org.apache.maven.plugin.dependency.utils.DependencyStatusSets;
//import org.apache.maven.plugin.dependency.utils.DependencyUtil;
import org.apache.maven.plugin.dependency.AbstractFromDependenciesMojo;
import org.apache.maven.plugin.dependency.utils.DependencyStatusSets;
import org.apache.maven.plugin.dependency.utils.filters.MarkerFileFilter;
import org.apache.maven.plugin.dependency.utils.markers.DefaultFileMarkerHandler;
//import org.apache.maven.plugin.dependency.utils.markers.DefaultFileMarkerHandler;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.artifact.filter.collection.ArtifactsFilter;
import org.apache.maven.shared.dependency.graph.traversal.CollectingDependencyNodeVisitor;
import org.apache.maven.shared.dependency.tree.DependencyNode;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilder;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilderException;
import org.apache.maven.shared.dependency.tree.traversal.DependencyNodeVisitor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * Goal which touches a timestamp file.
 *
 * **goal touch
 * 
 * **phase process-sources
 */
@Mojo( name = "unpack-dependencies", requiresDependencyResolution = ResolutionScope.TEST,
defaultPhase = LifecyclePhase.PROCESS_SOURCES )
public class Xpatch extends AbstractFromDependenciesMojo
{
    /**
     * Location of the file.
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private File outputDirectory;
    
    
    public void execute() throws MojoExecutionException {
    	
        //This code and related classe's dependencies are copied from maven-dependency-plugin as this classes are not published as jar (as of today 2012 aug 21)
    	DependencyStatusSets dss = getDependencySets( this.failOnMissingClassifierArtifact );

        for ( Artifact artifact : dss.getResolvedDependencies() )
        {
            File destDir;
            
            File f = artifact.getFile();
            //then use jar file for exploring
            try{
            	System.out.println(f.getCanonicalPath());
            	
            	JarFile jf = new JarFile(f);
            	Enumeration<JarEntry> entries = jf.entries();
            	
            	System.out.println("test if the jar contain the needed folder : ");
            	System.out.println(jf.getEntry("/META-INF/patch/"));
            	
            	System.out.println("Process the content of entry");
            	while(entries.hasMoreElements()){
            		JarEntry ja = entries.nextElement();
            		System.out.println(ja.getName());
            		System.out.println(ja.isDirectory());
            	}
            	
            }catch ( IOException e ){
                throw new MojoExecutionException( "Error accessing file "+artifact.toString(), e );
            }
            
            
            
            
        }

        
        
        
    	/*File f = outputDirectory;

        if ( !f.exists() )
        {
            f.mkdirs();
        }

        File touch = new File( f, "touch.txt" );

        FileWriter w = null;
        try
        {
            w = new FileWriter( touch );

            w.write( "touch.txt" );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Error creating file " + touch, e );
        }
        finally
        {
            if ( w != null )
            {
                try
                {
                    w.close();
                }
                catch ( IOException e )
                {
                    // ignore
                }
            }
        }*/
    }
    
    protected ArtifactsFilter getMarkedArtifactFilter()
    {
        return new MarkerFileFilter( this.overWriteReleases, this.overWriteSnapshots, this.overWriteIfNewer,
                                     new DefaultFileMarkerHandler( this.markersDirectory ) );
    }
    
}
