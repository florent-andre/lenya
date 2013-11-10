package org.apache.lenya.xpatch;

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

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.lang.NotImplementedException;
import org.apache.lenya.XpathModifier;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
//import org.apache.tools.ant.BuildException;
//import org.apache.tools.ant.DirectoryScanner;
//import org.apache.tools.ant.Project;
//import org.apache.tools.ant.taskdefs.MatchingTask;
//import org.apache.tools.ant.types.XMLCatalog;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Ant task to patch xmlfiles.
 *
 * 
 * replace-properties no|false,anything else
 * xpath: xpath expression for context node
 * unless-path: xpath expression that must return empty node set
 * unless: (deprecated) xpath expression that must return empty node set
 * if-prop: use path file only when project property is set
 * remove: xpath expression to remove before adding nodes
 * add-comments: if specified, overrides the ant task value
 * add-attribute: name of attribute to add to context node (requires value)
 * add-attribute-<i>name</i>: add attribute <i>name</i> with the specified value
 * value: value of attribute to add to context node (requires add-attribute)
 * insert-before: xpath expression, add new nodes before
 * insert-after: xpath expression, add new nodes after
 * 
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @author <a href="mailto:vgritsenko@apache.org">Vadim Gritsenko</a>
 * @author <a href="mailto:crafterm@fztig938.bank.dresdner.net">Marcus Crafter</a>
 * @author <a href="mailto:ovidiu@cup.hp.com">Ovidiu Predescu</a>
 * @author <a href="mailto:stephan@apache.org">Stephan Michels</a>
 * @version CVS $Id$
 */
public final class Xpatch {//extends MatchingTask {

    private static final String NL=System.getProperty("line.separator");
    private static final String FSEP=System.getProperty("file.separator");
    private Log log = new SystemStreamLog();
    private File file;
    private File srcdir;
    private boolean addComments;
    private XpathModifier xpathModifier = null;
    /** for resolving entities such as dtds */
//    private XMLCatalog xmlCatalog = new XMLCatalog();

    /**
     * Set file, which should be patched.
     *
     * @param file File, which should be patched.
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * Set base directory for the patch files.
     *
     * @param srcdir Base directory for the patch files.
     */
    public void setSrcdir(File srcdir) {
        this.srcdir = srcdir;
    }

    /**
     * Add the catalog to our internal catalog
     *
     * @param xmlCatalog the XMLCatalog instance to use to look up DTDs
     */
//    public void addConfiguredXMLCatalog(XMLCatalog newXMLCatalog) {
//        this.xmlCatalog.addConfiguredXMLCatalog(newXMLCatalog);
//    }

    /**
     * Whether to add a comment indicating where this block of code comes
     * from.
     */
    public void setAddComments(Boolean addComments) {
        this.addComments = addComments.booleanValue();
    }

    /**
     * Initialize internal instance of XMLCatalog
     */
//    public void init() throws BuildException {
//        super.init();
//        xmlCatalog.setProject(this.getProject());
//    }

    /**
     * Patch XML document with a given patch file.
     * 
     * @param configuration Orginal document
     * @param component Patch document
     * @param patchFile Patch file
     *
     * @return True, if the document was successfully patched
     * @throws ParserConfigurationException 
     */
    public boolean patch(final Document configuration, final File patchFile, Properties properties)
                           throws XpatchException {
    	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    	
    	Document patchDoc;
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			patchDoc = db.parse(patchFile);
		} catch (SAXException e) {
			throw new XpatchException(e);
		} catch (IOException e) {
			throw new XpatchException(e);
		} catch (ParserConfigurationException e) {
			throw new XpatchException(e);
		}
    	
		
        String filename = patchFile.toString();
                            
        // Check to see if Document is an xconf-tool document
        Element elem = patchDoc.getDocumentElement();

        String extension = filename.lastIndexOf(".")>0?filename.substring(filename.lastIndexOf(".")+1):"";
        String basename = basename(filename);

        if (!elem.getTagName().equals(extension)) {
        	throw new XpatchException("Not a valid xpatch file: "+filename);
        }

        //TODO : see what is this replace-properties attr
//        String replacePropertiesStr = elem.getAttribute("replace-properties");
//        boolean replaceProperties = !("no".equalsIgnoreCase(replacePropertiesStr) ||
//                                      "false".equalsIgnoreCase(replacePropertiesStr));
        
        boolean replaceProperties = false;
        
        // Get 'root' node were 'component' will be inserted into
        String xpath = getAttribute(elem, "xpath", replaceProperties);
        if ( xpath == null ) {
            throw new XpatchException("Attribute 'xpath' is required.");    
        }
        //apply xpath modifications on xpath if defined
        if(xpathModifier != null){
        	String origin = xpath;
        	xpath = xpathModifier.process(xpath);
        	log.debug("Xpath is modified from : " + origin + " to :" + xpath);
        }
        NodeList nodes;
		try {
			nodes = XPathAPI.selectNodeList(configuration, xpath);
		} catch (TransformerException e1) {
			throw new XpatchException(e1);
		}

        // Suspend, because the xpath returned no node
        if (nodes.getLength() !=1 ) {
        	log.warn("Xpath for path return no node");
            //log("Suspending: "+filename, Project.MSG_DEBUG);
            return false;
        }
        Node root = nodes.item(0);

        // Test that 'root' node satisfies 'component' insertion criteria
        String testPath = getAttribute(elem, "unless-path", replaceProperties);
        if (testPath == null || testPath.length() == 0) {
            // only look for old "unless" attr if unless-path is not present
            testPath = getAttribute(elem, "unless", replaceProperties);
        }
        
        //apply xpath modifications on testPath if defined
        if(xpathModifier != null && testPath != null){
        	String origin = testPath;
        	testPath = xpathModifier.process(testPath);
        	log.warn("Xpath for 'unless' is modified from : " + origin + " to :" + testPath);
        }
        
        // Is if-path needed?
        String ifProp = getAttribute(elem, "if-prop", replaceProperties);
        boolean ifValue = false;
        if (ifProp != null && !ifProp.equals("")) {
        	if(properties != null) ifValue = properties.containsKey(ifProp);
        	//throw new NotImplementedException("See to implement this case");
            //ifValue = Boolean.valueOf(this.getProject().getProperty(ifProp)).booleanValue();
        }

        if (ifProp != null && ifProp.length() > 0 && !ifValue ) {
            //log("Skipping: " + filename, Project.MSG_DEBUG);
        	log.debug("---> Path not applied, see case 1");
            return false;
        } else
			try {
				if (testPath != null && testPath.length() > 0 &&
				    XPathAPI.eval(root, testPath).bool()) {
				    //log("Skipping: " + filename, Project.MSG_DEBUG);
					log.debug("---> Path not applied, see case 2");
				    return false;
				} else {
					//log.info("Start applying the patch");
				    // Test if component wants us to remove a list of nodes first
				    xpath = getAttribute(elem, "remove", replaceProperties);

				    if (xpath != null && xpath.length() > 0) {
				        try {
							nodes = XPathAPI.selectNodeList(configuration, xpath);
						} catch (TransformerException e) {
							throw new XpatchException(e);
						}

				        for (int i = 0, length = nodes.getLength(); i<length; i++) {
				            Node node = nodes.item(i);
				            Node parent = node.getParentNode();

				            parent.removeChild(node);
				        }
				    }

				    // Test for an attribute that needs to be added to an element
				    String name = getAttribute(elem, "add-attribute", replaceProperties);
				    String value = getAttribute(elem, "value", replaceProperties);

				    if (name != null && name.length() > 0) {
				        if (value == null) {
				            throw new XpatchException("No attribute value specified for 'add-attribute' "+
				                                  xpath);
				        }
				        if (root instanceof Element) {
				            ((Element) root).setAttribute(name, value);
				        }
				    }
 
				    // Override addComments from ant task if specified as an attribute
				    String addCommentsAttr = getAttribute(elem, "add-comments", replaceProperties);
				    if ((addCommentsAttr!=null) && (addCommentsAttr.length()>0)) {
				        setAddComments(new Boolean(addCommentsAttr));
				    }

				    // Allow multiple attributes to be added or modified
				    if (root instanceof Element) {
				        NamedNodeMap attrMap = elem.getAttributes();
				        for (int i=0; i<attrMap.getLength(); ++i){
				            Attr attr = (Attr)attrMap.item(i);
				            final String addAttr = "add-attribute-";
				            if (attr.getName().startsWith(addAttr)) {
				                String key = attr.getName().substring(addAttr.length());
				                ((Element) root).setAttribute(key, attr.getValue());
				            }
				        }
				    }

				    // Test if 'component' provides desired insertion point
				    xpath = getAttribute(elem, "insert-before", replaceProperties);
				    Node before = null;

				    if (xpath != null && xpath.length() > 0) {
				        try {
							nodes = XPathAPI.selectNodeList(root, xpath);
						} catch (TransformerException e) {
							throw new XpatchException(e);
						}
				        if (nodes.getLength() == 0) {
				            throw new XpatchException("XPath ("+xpath+") returned zero nodes");
				        }
				        before = nodes.item(0);
				    } else {
				        xpath = getAttribute(elem, "insert-after", replaceProperties);
				        if (xpath != null && xpath.length() > 0) {
				            nodes = XPathAPI.selectNodeList(root, xpath);
				            if (nodes.getLength() == 0) {
				                throw new XpatchException("XPath ("+xpath+") zero nodes.");
				            }
				            before = nodes.item(nodes.getLength()-1).getNextSibling();
				        }
				    }

				    // Add 'component' data into 'root' node
				    //log("Processing: "+filename);
				    //NodeList componentNodes = component.getDocumentElement().getChildNodes();
				    NodeList componentNodes = patchDoc.getDocumentElement().getChildNodes();
				    
				    if (this.addComments) {
				        root.appendChild(configuration.createComment("..... Start configuration from '"+basename+"' "));
				        root.appendChild(configuration.createTextNode(NL));
				    }
				    for (int i = 0; i<componentNodes.getLength(); i++) {
				        Node node = configuration.importNode(componentNodes.item(i),
				                                             true);

				        if (replaceProperties) {
				            replaceProperties(node);
				        }
				        if (before==null) {
				            root.appendChild(node);
				        } else {
				            root.insertBefore(node, before);
				        }
				    }
				    if (this.addComments) {
				        root.appendChild(configuration.createComment("..... End configuration from '"+basename+"' "));
				        root.appendChild(configuration.createTextNode(NL));
				    }
				    //log.debug("Patch applied !!!");
				    return true;
				}
			} catch (DOMException e) {
				throw new XpatchException(e);
			} catch (TransformerException e) {
				throw new XpatchException(e);
			}
    }

    private String getAttribute(Element elem, String attrName, boolean replaceProperties) {
        String attr = elem.getAttribute(attrName);
        if (attr == null) {
            return null;
        } else if (replaceProperties) {
        	throw new NotImplementedException("Implement this case");
            //return getProject().replaceProperties(attr);
        } else {
            return attr;
        }
    }

    private void replaceProperties(Node n) throws DOMException {
        NamedNodeMap attrs = n.getAttributes();
        if (attrs != null) {
            for (int i = 0; i < attrs.getLength(); i++) {
                Node attr = attrs.item(i);
                throw new NotImplementedException("implement this case");
                //attr.setNodeValue(getProject().replaceProperties(attr.getNodeValue()));     
            } 
        }
        switch (n.getNodeType()) {
            case Node.ATTRIBUTE_NODE:
            case Node.CDATA_SECTION_NODE:
            case Node.TEXT_NODE: {
            	throw new NotImplementedException("implement this case");
                //n.setNodeValue(getProject().replaceProperties(n.getNodeValue()));
                //break;
            }
            case Node.DOCUMENT_NODE:
            case Node.DOCUMENT_FRAGMENT_NODE:
            case Node.ELEMENT_NODE: {
                Node child = n.getFirstChild();
                while (child != null) {
                    replaceProperties(child);
                    child = child.getNextSibling();
                }
                break;
            }
            default: {
                // ignore all other node types
            }
        }
    }

    /** Returns the file name (excluding directories and extension). */
    private String basename(String fileName) {
        int start = fileName.lastIndexOf(FSEP)+1; // last '/'
        int end = fileName.lastIndexOf(".");  // last '.'

        if (end == 0) {
            end = fileName.length();
        }
        return fileName.substring(start, end);
    }

	public XpathModifier getXpathModifier() {
		return xpathModifier;
	}

	public void setXpathModifier(XpathModifier xpathModifier) {
		this.xpathModifier = xpathModifier;
	}
}
