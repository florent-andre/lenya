/*
 * $Id: ProxyGenerator.java,v 1.8 2003/02/07 12:14:09 ah Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 wyona. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * 3. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgment: "This product includes software developed
 *    by wyona (http://www.wyona.org)"
 *
 * 4. The name "wyona" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@wyona.org
 *
 * 5. Products derived from this software may not be called "wyona" nor may "wyona"
 *    appear in their names without prior written permission of wyona.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by wyona (http://www.wyona.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY wyona "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. wyona WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL wyona BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF wyona HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. wyona WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Wyona includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.wyona.cms.cocoon.generation;

import org.apache.avalon.excalibur.xml.Parser;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;

import org.apache.cocoon.Constants;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.Cookie;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.environment.http.HttpRequest;
import org.apache.cocoon.environment.http.HttpSession;
import org.apache.cocoon.environment.wrapper.*;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.methods.PostMethod;

import org.apache.log4j.Category;

import org.xml.sax.InputSource;

//import org.apache.log4j.Category;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;


/**
 * DOCUMENT ME!
 *
 * @author Michael Wechner
 * @version 2002.8.27
 */
public class ProxyGenerator extends org.apache.cocoon.generation.ServletGenerator
    implements Parameterizable {
    static Category log = Category.getInstance(ProxyGenerator.class);
    protected String src;

    // The URI of the namespace of this generator
    private String URI = "http://www.wyona.org/wyona-cms/proxygenerator/1.0";

    /**
     * DOCUMENT ME!
     *
     * @param parameters DOCUMENT ME!
     *
     * @throws ParameterException DOCUMENT ME!
     */
    public void parameterize(Parameters parameters) throws ParameterException {
        //super.parameterize(parameters);
    }

    /**
     * DOCUMENT ME!
     *
     * @param resolver DOCUMENT ME!
     * @param objectModel DOCUMENT ME!
     * @param src DOCUMENT ME!
     * @param par DOCUMENT ME!
     *
     * @throws ProcessingException DOCUMENT ME!
     * @throws SAXException DOCUMENT ME!
     * @throws IOException DOCUMENT ME!
     */
    public void setup(SourceResolver resolver, Map objectModel, String src, Parameters par)
        throws ProcessingException, SAXException, IOException {
        super.setup(resolver, objectModel, src, par);

        try {
            //log.debug(".setup(): "+src);
            this.src = src;
        } catch (Exception e) {
            //log.error(e);
        }
    }

    /**
     * Generate XML data.
     *
     * @throws SAXException DOCUMENT ME!
     */
    public void generate() throws SAXException {
        /*  NOTE: httpRequest not used anymore!
            HttpRequest httpRequest
                = (HttpRequest) objectModel.get(ObjectModelHelper.REQUEST_OBJECT);
            log.debug(".generate(): server-port: "+httpRequest.getServerPort());
        */
        Request request = (Request) objectModel.get(ObjectModelHelper.REQUEST_OBJECT);

        log.debug("\n----------------------------------------------------------------" +
            "\n- Request: (" + request.getClass().getName() + ") at port " +
            request.getServerPort() +
            "\n----------------------------------------------------------------");

        String submitMethod = request.getMethod();

        Parser parser = null;

        try {
            // DEBUG
            if (submitMethod.equals("POST")) {
                // FIXME: Andreas
                if (request instanceof HttpRequest) {
                    java.io.InputStream is = intercept(((HttpRequest) request).getInputStream());
                }

                //log.debug("HTTP method: "+submitMethod);
            } else if (submitMethod.equals("GET")) {
                //log.debug("HTTP method: "+submitMethod);
            } else {
                //log.debug("HTTP method: "+submitMethod);
            }

            URL url = createURL(request);

            // Forward "InputStream", Parameters, QueryString to Servlet
            //log.debug(".generate(): Remote URL: "+url);
            org.apache.commons.httpclient.HttpMethod httpMethod = null;

            if (submitMethod.equals("POST")) {
                httpMethod = new PostMethod();

                Enumeration params = request.getParameterNames();

                while (params.hasMoreElements()) {
                    String paramName = (String) params.nextElement();
                    String[] paramValues = request.getParameterValues(paramName);

                    for (int i = 0; i < paramValues.length; i++) {
                        ((PostMethod) httpMethod).setParameter(paramName, paramValues[i]);
                    }
                }
            } else if (submitMethod.equals("GET")) {
                httpMethod = new org.apache.commons.httpclient.methods.GetMethod();
                httpMethod.setQueryString(request.getQueryString());
            }

            // Copy/clone Cookies
            Cookie[] cookies = request.getCookies();
            org.apache.commons.httpclient.Cookie[] transferedCookies = null;

            if (cookies != null) {
                transferedCookies = new org.apache.commons.httpclient.Cookie[cookies.length];

                for (int i = 0; i < cookies.length; i++) {
                    //log.warn(".generate(): Original COOKIE: "+cookies[i].getPath()+" "+cookies[i].getName()+" "+cookies[i].getDomain()+" "+cookies[i].getValue());
                    boolean secure = false; // http: false, https: true
                    transferedCookies[i] = new org.apache.commons.httpclient.Cookie(url.getHost(),
                            cookies[i].getName(), cookies[i].getValue(), url.getFile(), null, secure);

                    //log.warn(".generate(): Copied COOKIE: "+transferedCookies[i]);
                }
            }

            // Initialize HttpClient
            HttpClient httpClient = new HttpClient();

            // Set cookies
            if ((transferedCookies != null) && (transferedCookies.length > 0)) {
                HttpState httpState = new HttpState();
                httpState.addCookies(transferedCookies);
                httpClient.setState(httpState);
            }

            // DEBUG cookies
            /*
                      org.apache.commons.httpclient.Cookie[] tcookies=httpClient.getState().getCookies();
                      for(int i=0;i<tcookies.length;i++){
                        log.warn(".generate(): Transfered COOKIE: "+tcookies[i]);
                        }
            */
            // Send request to servlet
            httpMethod.setRequestHeader("Content-type", "text/plain");
            httpMethod.setPath(url.getPath());

            // FIXME
            for (Enumeration e = request.getHeaderNames(); e.hasMoreElements();) {
                String name = (String) e.nextElement();
                httpMethod.addRequestHeader(name, request.getHeader(name));
            }

            httpClient.startSession(url.getHost(), url.getPort());

            // /FIXME
            log.debug("\n----------------------------------------------------------------" +
                "\n- Starting session at URI: " + url + "\n- Host:                    " +
                url.getHost() + "\n- Port:                    " + url.getPort() +
                "\n----------------------------------------------------------------");

            int result = httpClient.executeMethod(httpMethod);

            log.debug("\n----------------------------------------------------------------" +
                "\n- Result:                    " + result +
                "\n----------------------------------------------------------------");

            byte[] sresponse = httpMethod.getResponseBody();

            //log.warn(".generate(): Response from remote server: "+new String(sresponse));
            httpClient.endSession();

            // Return XML
            InputSource input = new InputSource(new ByteArrayInputStream(sresponse));
            parser = (Parser) this.manager.lookup(Parser.ROLE);
            parser.parse(input, this.xmlConsumer);
        } catch (Exception e) {
            this.contentHandler.startDocument();

            AttributesImpl attr = new AttributesImpl();
            this.start("servletproxygenerator", attr);
            this.data(".generate(): " + e);
            this.end("servletproxygenerator");
            this.contentHandler.endDocument();

            //log.error(e);
            e.printStackTrace();
        } finally {
            this.manager.release(parser);
        }
    }

    /**
     * Log input stream for debugging
     *
     * @param in an <code>InputStream</code> value
     *
     * @return an <code>InputStream</code> value
     *
     * @exception Exception if an error occurs
     */
    private InputStream intercept(InputStream in) throws Exception {
        byte[] buffer = new byte[1024];
        int bytes_read;
        ByteArrayOutputStream bufferOut = new ByteArrayOutputStream();

        while ((bytes_read = in.read(buffer)) != -1) {
            bufferOut.write(buffer, 0, bytes_read);
        }

        //log.warn("Intercepted Input Stream:\n\n"+bufferOut.toString());
        return new ByteArrayInputStream(bufferOut.toByteArray());
    }

    //  private URL createURL(HttpRequest request) throws MalformedURLException{
    private URL createURL(Request request) throws MalformedURLException {
        URL url = null;

        try {
            url = new URL(this.src);
            log.debug(".createURL(): " + url);
        } catch (MalformedURLException e) {
            //log.warn(".createURL(): "+e);
            url = new URL("http://127.0.0.1:" + request.getServerPort() + this.src);

            //FIXME
            //url=new URL("http://192.168.0.219:"+request.getServerPort()+this.src);
            log.debug(".createURL(): Add localhost and port: " + url);
        }

        return url;
    }

    private void attribute(AttributesImpl attr, String name, String value) {
        attr.addAttribute("", name, name, "CDATA", value);
    }

    private void start(String name, AttributesImpl attr)
        throws SAXException {
        super.contentHandler.startElement(URI, name, name, attr);
        attr.clear();
    }

    private void end(String name) throws SAXException {
        super.contentHandler.endElement(URI, name, name);
    }

    private void data(String data) throws SAXException {
        super.contentHandler.characters(data.toCharArray(), 0, data.length());
    }
}
