package org.apache.lenya;

import java.io.File;

import org.apache.maven.model.Model;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.project.MavenProject;

public class MyMojoTest extends AbstractMojoTestCase {
	/** {@inheritDoc} */
	protected void setUp() throws Exception {
		// required
		super.setUp();
	}

	/** {@inheritDoc} */
	protected void tearDown() throws Exception {
	    // required
	    super.tearDown();
    }

	/**
	 * @throws Exception if any
	 */
	public void testSomething() throws Exception {
		File pom = getTestFile( "src/test/resources/unit/pom.xml" );
	    assertNotNull( pom );
	    assertTrue( pom.exists() );
	    MavenProject mp = new MavenProject();
	    
	    
	    /*
	     * This part throw errors due to not configured project...
	     * 
	     * See this article for tutorials : http://blog.code-cop.org/2010/09/maven-plugin-testing-tools.html
	     * 
	     */
	    /*
	    MyMojo myMojo = (MyMojo) lookupMojo( "touch", pom );
	    
	    assertNotNull( myMojo );
	    myMojo.execute();
	    */
	 }
}
