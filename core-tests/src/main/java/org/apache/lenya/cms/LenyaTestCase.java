/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.lenya.cms;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.avalon.excalibur.component.DefaultRoleManager;
import org.apache.avalon.excalibur.component.ExcaliburComponentManager;
import org.apache.avalon.excalibur.logger.LoggerManager;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.WrapperServiceManager;
import org.apache.avalon.framework.context.Context;
import org.apache.cocoon.Constants;
import org.apache.cocoon.components.ContextHelper;
import org.apache.cocoon.core.container.ContainerTestCase;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.commandline.CommandLineContext;
import org.apache.cocoon.environment.commandline.CommandLineRequest;
import org.apache.cocoon.environment.mock.MockEnvironment;
import org.apache.cocoon.util.Deprecation;
import org.apache.cocoon.util.IOUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.excalibur.source.SourceResolver;
import org.apache.excalibur.xml.EntityResolver;
import org.apache.lenya.xml.DocumentHelper;




/**
 * Base class for Lenya tests which need the context information.
 */
public class LenyaTestCase extends TestCase {

	/** The default logger */
    private Logger logger;

    /** The service manager to use */
    private ServiceManager manager;

    /** Return the logger */
    protected Logger getLogger() {
        return logger;
    }

    /** Return the service manager */
    protected ServiceManager getManager() {
        return this.manager;
    }
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        String level = System.getProperty("junit.test.loglevel", "" + ConsoleLogger.LEVEL_WARN);
        this.logger = new ConsoleLogger(Integer.parseInt(level));
        Deprecation.setLogger(this.logger);
        prepare();
    }
    
    protected DefaultContext context;

    protected void addContext(DefaultContext context) {
        
        String currentDir = this.getClass().getClassLoader().getResource("").getPath();
        
        this.context = context;

        String tempPath = System.getProperty("tempDir", currentDir + "lenya/temp");
        String contextRoot = System.getProperty("contextRoot", currentDir + "lenya/webapp");

        getLogger().info("Adding context root entry [" + contextRoot + "]");

        File contextRootDir = new File(contextRoot);
        context.put("context-root", contextRootDir);

        String testPath = System.getProperty("testPath", currentDir + "test");
        File testRootDir = new File(testPath);
        
        System.out.println("*************************** context root dir = "+ contextRoot +" *************************");
        System.out.println("*************************** root dir = "+ testRootDir.getAbsolutePath() +" *************************");
        
        context.put("test-path", testRootDir);

        org.apache.cocoon.environment.Context envContext = new CommandLineContext(contextRoot);
        ContainerUtil.enableLogging(envContext, getLogger());
        context.put(Constants.CONTEXT_ENVIRONMENT_CONTEXT, envContext);

        File tempDir = new File(tempPath);

        File workDir = new File(tempDir, "work");
        context.put("work-directory", workDir);

        File cacheDir = new File(tempDir, "cache");
        context.put(Constants.CONTEXT_CACHE_DIR, cacheDir);

        File uploadDir = new File(tempDir, "upload");
        context.put(Constants.CONTEXT_UPLOAD_DIR, uploadDir);

        context.put(Constants.CONTEXT_CLASS_LOADER, LenyaTestCase.class.getClassLoader());
        context.put(Constants.CONTEXT_CLASSPATH, getClassPath(contextRoot));
        context.put(Constants.CONTEXT_DEFAULT_ENCODING, "ISO-8859-1");
    }

    private Request request = null;

    protected Request getRequest() {
        return this.request;
    }

    protected void prepare() throws Exception {
    	
    	System.out.println("*****************************************************************");
    	System.out.println("*****************************************************************");
    	System.out.println("*****************************************************************");
    	
        final String resourceName = LenyaTestCase.class.getName().replace('.', '/') + ".xtest";
        System.out.println("******************* xTEST FILE :"+ resourceName +"**********************************");
        
        URL resourceUrl = ClassLoader.getSystemResource(resourceName);
        System.out.println("*********************** resource url xtest "+ resourceUrl.toString() +"***************************");
        System.out.println("*****************************************************************");
        prepare(resourceUrl.openStream());
        
        SourceResolver resolver = (SourceResolver) getManager().lookup(SourceResolver.ROLE);
        MockEnvironment env = new MockEnvironment(resolver);

        String pathInfo = getWebappUrl();

        this.request = new CommandLineRequest(env,
                "",
                "",
                pathInfo,
                new HashMap(),
                getRequestParameters());
        context.put("object-model.request", this.request);

        Map objectModel = new HashMap();
        objectModel.put(ObjectModelHelper.REQUEST_OBJECT, request);
        org.apache.cocoon.environment.Context envContext = (org.apache.cocoon.environment.Context) context.get(Constants.CONTEXT_ENVIRONMENT_CONTEXT);
        objectModel.put(ObjectModelHelper.CONTEXT_OBJECT, envContext);
        context.put(ContextHelper.CONTEXT_OBJECT_MODEL, objectModel);
        
        EntityResolver entityResolver = (EntityResolver) getManager().lookup(EntityResolver.ROLE);
        DocumentHelper.setEntityResolver(entityResolver);
        
    }
    
    /**
     * Initializes the ComponentLocator
     *
     * @param testconf The configuration file is passed as a <code>InputStream</code>
     *
     * A common way to supply a InputStream is to overwrite the initialize() method
     * in the sub class, do there whatever is needed to get the right InputStream object
     * supplying a conformant xtest configuartion and pass it to this initialize method.
     * the mentioned initialize method is also the place to set a different logging priority
     * to the member variable m_logPriority.
     */
    protected final void prepare(final InputStream testconf) throws Exception {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("ContainerTestCase.initialize");
        }

        System.out.println("$$$$$$$$$$$$$$$$$$$ Dans le prépare");
        final DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
        final Configuration conf = builder.build(testconf);

        Context context = this.setupContext(conf.getChild("context"));
        Configuration componentsConf = conf.getChild("components");
        
        //construction de la configuration des roles
        String userRoles = conf.getAttribute("user-roles", "");
        System.out.println("$$$$$$$$$$$$$$$$$$$ valeur du userRoles == " + userRoles);
        if (!"".equals(userRoles)) {
        	//we need to add parents folder in test environment.
        	userRoles = "lenya/webapp" + userRoles; 
        	URL resourceUrl = ClassLoader.getSystemResource(userRoles);
            System.out.println("*********************** resource ROLES "+ resourceUrl.toString() +"***************************");
            System.out.println("*****************************************************************");
            InputStream roleis = resourceUrl.openStream(); 
            
            
            final DefaultConfigurationBuilder roleBuilder = new DefaultConfigurationBuilder();
            final Configuration roleConf = builder.build(roleis);
//            try {
//            	
//                p = (SAXParser)startupManager.lookup(SAXParser.ROLE);
//                SAXConfigurationHandler b = new PropertyAwareSAXConfigurationHandler(settings, this.getLogger());
//                org.apache.cocoon.environment.Context context =
//                    (org.apache.cocoon.environment.Context) this.context.get(Constants.CONTEXT_ENVIRONMENT_CONTEXT);
//                URL url = context.getResource(userRoles);
//                if (url == null) {
//                    throw new ConfigurationException("User-roles configuration '"+userRoles+"' cannot be found.");
//                }
//                InputSource is = new InputSource(new BufferedInputStream(url.openStream()));
//                is.setSystemId(url.toString());
//                p.parse(is, b);
//                roles = b.getConfiguration();
//            } catch (Exception e) {
//                throw new ConfigurationException("Error trying to load user-roles configuration", e);
//            } finally {
//                startupManager.release((Component)p);
//            }

//            DefaultRoleManager urm = new DefaultRoleManager(drm);
        
            //TODO : faire une fonction qui renvoi le role conf et une exception dans le else
            setupManagers(componentsConf,
                    roleConf,
                    context);
        }else{
        	System.out.println("!!!!!!!!!!!!!!!!!!!!!! La configuration n'est pas récupérée !!!!!!!!!!!!!!!!!!!!");
        }
        //fin construction de la configuration des roles
        
    }

    
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        done();
        super.tearDown();
    }

    /**
     * Disposes the <code>ComponentLocator</code>
     */
    final private void done() {
        if (manager != null) {
            ContainerUtil.dispose(manager);
            manager = null;
        }
    }

    /**
     * set up a context according to the xtest configuration specifications context
     * element.
     *
     * A method addContext(DefaultContext context) is called here to enable subclasses
     * to put additional objects into the context programmatically.
     */
    final private Context setupContext( final Configuration conf )
    throws Exception {
        final DefaultContext context = new DefaultContext();
        final Configuration[] confs = conf.getChildren( "entry" );
        for (int i = 0; i < confs.length; i++) {
            final String key = confs[i].getAttribute("name");
            final String value = confs[i].getAttribute("value", null);
            if (value == null) {
                String clazz = confs[i].getAttribute("class");
                Object obj = getClass().getClassLoader().loadClass(clazz).newInstance();
                context.put(key, obj);
                if (getLogger().isInfoEnabled()) {
                    getLogger().info("ContainerTestCase: added an instance of class " + clazz + " to context entry " + key);
                }
            } else {
                context.put(key, value);
                if (getLogger().isInfoEnabled()) {
                    getLogger().info("ContainerTestCase: added value \"" + value + "\" to context entry " + key);
                }
            }
        }
        addContext(context);
        return context ;
    }

    

    final private void setupManagers(final Configuration confCM,
                                     final Configuration confRM,
                                     final Context context)
    throws Exception {
        // Setup the RoleManager
        DefaultRoleManager roleManager = new DefaultRoleManager();
        roleManager.enableLogging(getLogger());
        roleManager.configure(confRM);

        // Set up the ComponentLocator
        ExcaliburComponentManager ecManager = new ExcaliburComponentManager();
        ecManager.enableLogging(getLogger());
        ecManager.contextualize(context);
        ecManager.setRoleManager(roleManager);
        ecManager.setLoggerManager(new DefaultLoggerManager(getLogger()));
        ecManager.configure(confCM);
        ecManager.initialize();
        this.manager = new WrapperServiceManager(ecManager);
    }

    protected final Object lookup(final String key) throws ServiceException {
        return manager.lookup(key);
    }

    protected final void release(final Object object) {
        manager.release(object);
    }

    protected static class DefaultLoggerManager implements LoggerManager {
        private Logger logger;

        public DefaultLoggerManager(Logger logger) {
            this.logger = logger;
        }
        /* (non-Javadoc)
         * @see org.apache.avalon.excalibur.logger.LoggerManager#getDefaultLogger()
         */
        public Logger getDefaultLogger() {
            return this.logger;
        }
        /* (non-Javadoc)
         * @see org.apache.avalon.excalibur.logger.LoggerManager#getLoggerForCategory(java.lang.String)
         */
        public Logger getLoggerForCategory(String arg0) {
            return this.logger;
        }
    }
    
    protected String getWebappUrl() {
        return "/test/authoring/index.html";
    }

    protected Map getRequestParameters() {
        return new HashMap();
    }
    
    
    

    /**
     * This builds the important ClassPath used by this class. It does so in a neutral way. It
     * iterates in alphabetical order through every file in the lib directory and adds it to the
     * classpath.
     * 
     * Also, we add the files to the ClassLoader for the Cocoon system. In order to protect
     * ourselves from skitzofrantic classloaders, we need to work with a known one.
     * 
     * TODO : remove this function as not still usefull
     * 
     * @param context The context path
     * @return a <code>String</code> value
     */
    protected String getClassPath(final String context) {
        StringBuffer buildClassPath = new StringBuffer();
        
        buildClassPath.append(SystemUtils.JAVA_CLASS_PATH);
        
        //System.out.println("Context classpath: " + buildClassPath);
        getLogger().info("Context classpath: " + buildClassPath);
        return buildClassPath.toString();
    }
    
    //test only to not get the "junit No tests found in ..."
    public void testSilly(){
    	assertEquals(true, true);
    }
    
}



/// Sauvegarde de la version originale
// La nouvelle version copie le code du containerTestCase, mais ceci afin d'avoir la cohérence sur
// l'utilisation des xroles

///**
// * Base class for Lenya tests which need the context information.
// */
//public class LenyaTestCase extends ContainerTestCase {
//
//    protected DefaultContext context;
//
//    protected void addContext(DefaultContext context) {
//        super.addContext(context);
//        
//        String currentDir = this.getClass().getClassLoader().getResource("").getPath();
//        
//        this.context = context;
//
//        String tempPath = System.getProperty("tempDir", currentDir + "lenya/temp");
//        String contextRoot = System.getProperty("contextRoot", currentDir + "lenya/webapp");
//
//        getLogger().info("Adding context root entry [" + contextRoot + "]");
//
//        File contextRootDir = new File(contextRoot);
//        context.put("context-root", contextRootDir);
//
//        String testPath = System.getProperty("testPath", currentDir + "test");
//        File testRootDir = new File(testPath);
//        
//        System.out.println("*************************** context root dir = "+ contextRoot +" *************************");
//        System.out.println("*************************** root dir = "+ testRootDir.getAbsolutePath() +" *************************");
//        
//        context.put("test-path", testRootDir);
//
//        Context envContext = new CommandLineContext(contextRoot);
//        ContainerUtil.enableLogging(envContext, getLogger());
//        context.put(Constants.CONTEXT_ENVIRONMENT_CONTEXT, envContext);
//
//        File tempDir = new File(tempPath);
//
//        File workDir = new File(tempDir, "work");
//        context.put("work-directory", workDir);
//
//        File cacheDir = new File(tempDir, "cache");
//        context.put(Constants.CONTEXT_CACHE_DIR, cacheDir);
//
//        File uploadDir = new File(tempDir, "upload");
//        context.put(Constants.CONTEXT_UPLOAD_DIR, uploadDir);
//
//        context.put(Constants.CONTEXT_CLASS_LOADER, LenyaTestCase.class.getClassLoader());
//        context.put(Constants.CONTEXT_CLASSPATH, getClassPath(contextRoot));
//        context.put(Constants.CONTEXT_DEFAULT_ENCODING, "ISO-8859-1");
//    }
//
//    private Request request = null;
//
//    protected Request getRequest() {
//        return this.request;
//    }
//
//    protected void prepare() throws Exception {
//    	
//    	System.out.println("*****************************************************************");
//    	System.out.println("*****************************************************************");
//    	System.out.println("*****************************************************************");
//    	
//        final String resourceName = LenyaTestCase.class.getName().replace('.', '/') + ".xtest";
//        System.out.println("******************* xTEST FILE :"+ resourceName +"**********************************");
//        
//        URL resourceUrl = ClassLoader.getSystemResource(resourceName);
//        System.out.println("*********************** resource url xtest "+ resourceUrl.toString() +"***************************");
//        System.out.println("*****************************************************************");
//        prepare(resourceUrl.openStream());
//        
//        SourceResolver resolver = (SourceResolver) getManager().lookup(SourceResolver.ROLE);
//        MockEnvironment env = new MockEnvironment(resolver);
//
//        String pathInfo = getWebappUrl();
//
//        this.request = new CommandLineRequest(env,
//                "",
//                "",
//                pathInfo,
//                new HashMap(),
//                getRequestParameters());
//        context.put("object-model.request", this.request);
//
//        Map objectModel = new HashMap();
//        objectModel.put(ObjectModelHelper.REQUEST_OBJECT, request);
//        Context envContext = (Context) context.get(Constants.CONTEXT_ENVIRONMENT_CONTEXT);
//        objectModel.put(ObjectModelHelper.CONTEXT_OBJECT, envContext);
//        context.put(ContextHelper.CONTEXT_OBJECT_MODEL, objectModel);
//        
//        EntityResolver entityResolver = (EntityResolver) getManager().lookup(EntityResolver.ROLE);
//        DocumentHelper.setEntityResolver(entityResolver);
//        
//    }
//    
//    
//
//    protected String getWebappUrl() {
//        return "/test/authoring/index.html";
//    }
//
//    protected Map getRequestParameters() {
//        return new HashMap();
//    }
//    
//    
//    
//
//    /**
//     * This builds the important ClassPath used by this class. It does so in a neutral way. It
//     * iterates in alphabetical order through every file in the lib directory and adds it to the
//     * classpath.
//     * 
//     * Also, we add the files to the ClassLoader for the Cocoon system. In order to protect
//     * ourselves from skitzofrantic classloaders, we need to work with a known one.
//     * 
//     * TODO : remove this function as not still usefull
//     * 
//     * @param context The context path
//     * @return a <code>String</code> value
//     */
//    protected String getClassPath(final String context) {
//        StringBuffer buildClassPath = new StringBuffer();
//        
//        buildClassPath.append(SystemUtils.JAVA_CLASS_PATH);
//        
//        //System.out.println("Context classpath: " + buildClassPath);
//        getLogger().info("Context classpath: " + buildClassPath);
//        return buildClassPath.toString();
//    }
//    
//    //test only to not get the "junit No tests found in ..."
//    public void testSilly(){
//    	assertEquals(true, true);
//    }
//    
//}
