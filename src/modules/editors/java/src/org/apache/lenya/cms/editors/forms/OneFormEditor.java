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
package org.apache.lenya.cms.editors.forms;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.cocoon.components.ContextHelper;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.publication.ResourceType;
import org.apache.lenya.cms.usecase.DocumentUsecase;
import org.apache.lenya.cms.usecase.UsecaseException;
import org.apache.lenya.cms.usecase.xml.UsecaseErrorHandler;
import org.apache.lenya.cms.workflow.WorkflowUtil;
import org.apache.lenya.cms.workflow.usecases.UsecaseWorkflowHelper;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.Schema;
import org.apache.lenya.xml.ValidationUtil;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * One form editor.
 * 
 * @version $Id$
 */
public class OneFormEditor extends DocumentUsecase {

    private static final String REFORMAT_XSLT_URI = "fallback://lenya/modules/editors/usecases/forms/prettyprint.xsl";

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#getNodesToLock()
     */
    protected org.apache.lenya.cms.repository.Node[] getNodesToLock() throws UsecaseException {
        org.apache.lenya.cms.repository.Node[] objects = { getSourceDocument().getRepositoryNode() };
        return objects;
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckPreconditions()
     */
    protected void doCheckPreconditions() throws Exception {
        super.doCheckPreconditions();
        UsecaseWorkflowHelper.checkWorkflow(this.manager, this, getEvent(), getSourceDocument(),
                getLogger());
        setParameter("executable", Boolean.valueOf(!hasErrors()));
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();

        String encoding = getEncoding();
        String content = getXmlString(encoding);
        saveDocument(encoding, content);
    }

    protected String getEncoding() {
        Request request = ContextHelper.getRequest(this.context);
        String encoding = request.getCharacterEncoding();
        return encoding;
    }

    protected String getXmlString(String encoding) {
        // Get namespaces
        String namespaces = removeRedundantNamespaces(getParameterAsString("namespaces"));
        if (getLogger().isDebugEnabled()) {
            getLogger().debug(namespaces);
        }
        // Aggregate content
        String content = "<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>\n"
                + addNamespaces(namespaces, getParameterAsString("content"));
        return content;
    }

    public void advance() throws UsecaseException {
        
        clearErrorMessages();
        try {
            Document xml = getXml();
            if (xml != null) {
                validate(xml);
            }
            if (!hasErrors()) {
                SourceUtil.writeDOM(xml, getSourceDocument().getOutputStream());
                deleteParameter("content");
            }
        } catch (Exception e) {
            throw new UsecaseException(e);
        }

        /*
        if (getParameter("reformat") != null) {
            clearErrorMessages();
            try {
                Document xml = getXml();
                if (xml != null) {
                    validate(xml);
                }
                if (!hasErrors()) {
                    Document xslt = SourceUtil.readDOM(REFORMAT_XSLT_URI, this.manager);
                    DOMSource xsltSource = new DOMSource(xslt);

                    TransformerFactory factory = TransformerFactory.newInstance();
                    Transformer transformer = factory.newTransformer(xsltSource);
                    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                    transformer.setOutputProperty(OutputKeys.METHOD, "xml");

                    DOMSource source = new DOMSource(xml);
                    StringWriter writer = new StringWriter();
                    StreamResult result = new StreamResult(writer);
                    transformer.transform(source, result);

                    setParameter("content", writer.toString());
                }
            } catch (Exception e) {
                throw new UsecaseException(e);
            }
        }
        */
    }

    protected void doCheckExecutionConditions() throws Exception {
        super.doCheckExecutionConditions();
        if (hasErrors()) {
            return;
        }

        Document xml = getXml();
        if (xml != null) {
            validate(xml);
        }
    }

    protected void validate(Document xml) throws Exception {
        ResourceType resourceType = getSourceDocument().getResourceType();
        Schema schema = resourceType.getSchema();
        ValidationUtil.validate(this.manager, xml, schema, new UsecaseErrorHandler(this));
    }

    protected Document getXml() throws ParserConfigurationException, IOException {
        String encoding = getEncoding();
        String xmlString = getXmlString(encoding);

        Document xml = null;
        try {
            xml = DocumentHelper.readDocument(xmlString, encoding);
        } catch (SAXException e) {
            addErrorMessage("error-document-form", new String[] { e.getMessage() });
        }
        return xml;
    }

    /**
     * Save the content to the document source. After saving, the XML is
     * validated. If validation errors occur, the usecase transaction is rolled
     * back, so the changes are not persistent. If the validation succeeded, the
     * workflow event is invoked.
     * @param encoding The encoding to use.
     * @param content The content to save.
     * @throws Exception if an error occurs.
     */
    protected void saveDocument(String encoding, String content) throws Exception {
        saveXMLFile(encoding, content, getSourceDocument());

        WorkflowUtil.invoke(this.manager, getSession(), getLogger(), getSourceDocument(),
                getEvent());
    }

    /**
     * Save the XML file
     * @param encoding The encoding
     * @param content The content
     * @param document The source
     * @throws FileNotFoundException if the file was not found
     * @throws UnsupportedEncodingException if the encoding is not supported
     * @throws IOException if an IO error occurs
     */
    private void saveXMLFile(String encoding, String content,
            org.apache.lenya.cms.publication.Document document) throws FileNotFoundException,
            UnsupportedEncodingException, IOException {
        Writer writer = null;

        try {
            writer = new OutputStreamWriter(document.getOutputStream(), encoding);
            writer.write(content, 0, content.length());
        } catch (FileNotFoundException e) {
            getLogger().error("File not found " + e.toString());
        } catch (UnsupportedEncodingException e) {
            getLogger().error("Encoding not supported " + e.toString());
        } catch (IOException e) {
            getLogger().error("IO error " + e.toString());
        } finally {
            // close all streams
            if (writer != null)
                writer.close();
        }
    }

    /**
     * Remove redundant namespaces
     * @param namespaces The namespaces to remove
     * @return The namespace string without the removed namespaces
     */
    private String removeRedundantNamespaces(String namespaces) {
        String[] namespace = namespaces.split(" ");

        String ns = "";
        for (int i = 0; i < namespace.length; i++) {
            if (ns.indexOf(namespace[i]) < 0) {
                ns = ns + " " + namespace[i];
            } else {
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("Redundant namespace: " + namespace[i]);
                }
            }
        }
        return ns;
    }

    /**
     * Add namespaces
     * @param namespaces The namespaces to add
     * @param content The content to add them to
     * @return The content with the added namespaces
     */
    private String addNamespaces(String namespaces, String content) {
        int i = content.indexOf(">");
        return content.substring(0, i) + " " + namespaces + content.substring(i);
    }

    protected String getEvent() {
        return "edit";
    }

}
