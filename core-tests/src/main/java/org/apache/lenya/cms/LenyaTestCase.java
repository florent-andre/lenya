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
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.cocoon.Constants;
import org.apache.cocoon.components.ContextHelper;
import org.apache.cocoon.core.container.ContainerTestCase;
import org.apache.cocoon.environment.Context;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.commandline.CommandLineContext;
import org.apache.cocoon.environment.commandline.CommandLineRequest;
import org.apache.cocoon.environment.mock.MockEnvironment;
import org.apache.cocoon.util.IOUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.excalibur.source.SourceResolver;
import org.apache.excalibur.xml.EntityResolver;
import org.apache.lenya.xml.DocumentHelper;

/**
 * Base class for Lenya tests which need the context information.
 */
public class LenyaTestCase extends ContainerTestCase {

    protected DefaultContext context;

    protected void addContext(DefaultContext context) {
        super.addContext(context);
        
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

        Context envContext = new CommandLineContext(contextRoot);
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
        Context envContext = (Context) context.get(Constants.CONTEXT_ENVIRONMENT_CONTEXT);
        objectModel.put(ObjectModelHelper.CONTEXT_OBJECT, envContext);
        context.put(ContextHelper.CONTEXT_OBJECT_MODEL, objectModel);
        
        EntityResolver entityResolver = (EntityResolver) getManager().lookup(EntityResolver.ROLE);
        DocumentHelper.setEntityResolver(entityResolver);
        
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