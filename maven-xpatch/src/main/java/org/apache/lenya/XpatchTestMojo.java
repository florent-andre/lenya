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
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import java.io.File;
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
 * @requiresDependencyResolution test
 */
public class XpatchTestMojo extends AbstractXpatchMojo {
	/**
	 * Location of the file.
	 * 
	 * @parameter property="project.build.testOutputDirectory"
	 * @required
	 */
	private File outputDirectory;

	// private ProjectDependenciesResolver depResolver;

	public void execute() throws MojoExecutionException {
		super.execute();

		File baseFile = new File(outputDirectory, "");
		File xtestFile = new File(baseFile,
				"org/apache/lenya/cms/LenyaTestCase.xtest");

		// TODO : create a map {patchRegex, fileToPatch } for configuring who
		// patch what
		String fileNameRegexOne = ".xweb";
		File fileToPatchOne = xtestFile;

		String fileNameRegexTwo = ".xconf";
		File fileToPatchTwo = xtestFile;
		
		
		XpathModifier xpathModifier = new XpathModifier();
		xpathModifier.addModifier("/cocoon", "/cocoon/components");
		//TODO : add the same for xroles : from role-list to cocoon/roles
		// end make this parameters configurable

		// TODO : make it configurable
		boolean getTransitives = true;

		Set<Artifact> dal;
		if (getTransitives) {
			dal = mavenProject.getArtifacts();
		} else {
			dal = mavenProject.getDependencyArtifacts();
		}

		// TODO :
		// permettre l'apply des patchs "locaux"
		// continuer le changement de l'abstract pour la prise en compte des
		// lest de file
		//
		getLog().debug("====== Start dep Artifacts =====");
		// TODO : a filter procedure for artifact
		for (Artifact da : dal) {
			getLog().info("Xpatch : processing Artifact : " + da.toString());
			runXpatch(da, fileNameRegexOne, fileToPatchOne, fileNameRegexTwo,
					fileToPatchTwo, mavenProject.getProperties(),xpathModifier);
		}

		getLog().info("Xpatch : processing local files");

		// TODO : make it configurable : only the test resources or the normal
		// resources, or both

		Collection<File> resourceFilesList = new ArrayList<File>();
		List<Resource> resources = mavenProject.getBuild().getResources();
		for (Resource resource : resources) {

			if (resource != null) {
				File resourceFolder = new File(resource.getDirectory());
				Collection<File> resourcesFiles = FileUtils.listFiles(
						resourceFolder, TrueFileFilter.INSTANCE,
						TrueFileFilter.INSTANCE);
				for (File f : resourcesFiles) {
					// TODO : use the Apache lib for managing file don't care
					// where they come...
					String relativUri = f.getAbsolutePath().replace(
							resourceFolder.getPath() + "/", "");
					if (resourcesFilter.isToKeep(relativUri)) {
						resourceFilesList.add(f);
					}
				}

			}
		}
		
		///now run the patches on the project
		runXpatch(resourceFilesList, fileNameRegexOne, fileToPatchOne, fileNameRegexTwo, fileToPatchTwo, mavenProject.getProperties(),xpathModifier);


	}

}
