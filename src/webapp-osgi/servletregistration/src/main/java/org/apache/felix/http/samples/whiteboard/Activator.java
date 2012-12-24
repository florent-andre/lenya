/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.felix.http.samples.whiteboard;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.http.HttpService;

import javax.servlet.Servlet;
import javax.servlet.Filter;
import javax.servlet.http.HttpServlet;

import org.apache.cocoon.servlet.CocoonServlet;
import java.util.Hashtable;

public final class Activator implements BundleActivator {
    private ServiceRegistration reg1;
    private ServiceRegistration reg2;
    private ServiceRegistration reg3;
    private ServiceRegistration reg4;
    
    public void start(BundleContext context) throws Exception {
    	
//        Hashtable<String, String> props = new Hashtable<String, String>();
//        props.put("alias", "/");
//        this.reg1 = context.registerService(Servlet.class.getName(), new TestServlet("servlet1"), props);
        
        //the cocoon servlet
        CocoonServlet cocoonServlet = new CocoonServlet();
    	CocoonServletWrapper csw = new CocoonServletWrapper("cocoonWrapper");
        Hashtable<String, String> props = new Hashtable<String, String>();
        props.put("alias", "/");
        props.put("init.init-classloader", "false");
        props.put("init.configurations", "/WEB-INF/cocoon.xconf");
        props.put("init.logkit-config", "/WEB-INF/logkit.xconf");
        props.put("init.servlet-logger", "access");
        props.put("init.cocoon-logger", "core");
        props.put("init.log-level", "WARN");
        props.put("init.forbidden-deprecation-level", "ERROR");
        props.put("init.allow-reload", "no");
        props.put("init.load-class", "org.hsqldb.jdbcDriver");
        props.put("init.enable-uploads", "true");
        props.put("init.autosave-uploads", "true");
        props.put("init.overwrite-uploads", "allow");
        props.put("init.upload-max-size", "10000000");
        props.put("init.show-cocoon-version", "true");
        props.put("init.manage-exceptions", "true");
        props.put("init.enable-instrumentation", "false");
        props.put("init.instrumentation-config", "/WEB-INF/instrumentation.xconf");
        props.put("init.container-encoding", "ISO-8859-1");
        props.put("init.form-encoding", "UTF-8");
        props.put("init.logger-class", "org.apache.avalon.excalibur.logger.Log4JLoggerManager");
        props.put("init.log4j-config", "/WEB-INF/log4j.xconf");
        
//        ServiceReference sRef = context.getServiceReference(HttpService.class.getName());
//        if (sRef != null)
//        {
//        	System.out.println("START REGISTER");
//        	HttpService service = (HttpService) context.getService(sRef);
//        	System.out.println("ADD COCOON SERVLET");
//        	//service.registerServlet("/hello", new HelloWorld(), null, null);
//        	service.registerServlet("/hello", cocoonServlet, null, null);
//        	System.out.println("REGISTER OKAY");
//        }
        //this.reg1 = context.registerService(Servlet.class.getName(), cocoonServlet, props);
        //this.reg1 = context.registerService(HttpServlet.class.getName(), cocoonServlet, props);
        //this.reg1 = context.registerService(HttpServlet.class.getName(), csw, props);
        
        this.reg1 = context.registerService(Servlet.class.getName(), new CocoonServlet(), props);
        //version that //works//
        //this.reg1 = context.registerService(Servlet.class.getName(), csw, props);
        
        //end cocoon servlet
        
        props = new Hashtable<String, String>();
        props.put("alias", "/other");
        this.reg2 = context.registerService(Servlet.class.getName(), new TestServlet("servlet2"), props);

        props = new Hashtable<String, String>();
        props.put("pattern", ".*");
        this.reg3 = context.registerService(Filter.class.getName(), new TestFilter("filter1"), props);

        props = new Hashtable<String, String>();
        props.put("pattern", "/other/.*");
        props.put("service.ranking", "100");
        this.reg4 = context.registerService(Filter.class.getName(), new TestFilter("filter2"), props);
    }

    public void stop(BundleContext context)
        throws Exception
    {
        this.reg1.unregister();
        this.reg2.unregister();
        this.reg3.unregister();
        this.reg4.unregister();
    }
}
