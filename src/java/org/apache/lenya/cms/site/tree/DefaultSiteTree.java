/*
 * Copyright  1999-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
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

package org.apache.lenya.cms.site.tree;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.site.Label;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Default sitetree implementation.
 * 
 * @version $Id$
 */
public class DefaultSiteTree extends AbstractLogEnabled implements SiteTree {

    /**
     * The sitetree namespace.
     */
    public static final String NAMESPACE_URI = "http://apache.org/cocoon/lenya/sitetree/1.0";

    /**
     * The name of the sitetree file.
     */
    public static final String SITE_TREE_FILENAME = "sitetree.xml";

    private Document document = null;
    private File treefile = null;
    // the area is only retained to provide some more info when raising an
    // exception.
    private String area = "";

    /**
     * Create a DefaultSiteTree
     * @param pubDir the publication directory
     * @param _area the area
     * @throws SiteException if an error occurs
     */
    protected DefaultSiteTree(File pubDir, String _area) throws SiteException {
        this(new File(pubDir, Publication.CONTENT_PATH + File.separator + _area + File.separator
                + SITE_TREE_FILENAME));
        this.area = _area;
    }

    /**
     * Create a DefaultSiteTree from a filename.
     * @param treefilename file name of the tree
     * @throws SiteException if an error occurs
     * @deprecated use the DefaultSiteTree(File pubDir, String area) constructor
     *             instead.
     */
    protected DefaultSiteTree(String treefilename) throws SiteException {
        this(new File(treefilename));
    }

    /**
     * Create a DefaultSiteTree from a file.
     * @param _treefile the file containing the tree
     * @throws SiteException if an error occurs
     * @deprecated this constructor will be private in the future
     */
    protected DefaultSiteTree(File _treefile) throws SiteException {
        this.treefile = _treefile;

        try {
            if (!_treefile.isFile()) {
                //the treefile doesn't exist, so create it

                this.document = createDocument();
            } else {
                // Read tree
                this.document = DocumentHelper.readDocument(_treefile);
            }
        } catch (ParserConfigurationException e) {
            throw new SiteException(e);
        } catch (SAXException e) {
            throw new SiteException(e);
        } catch (IOException e) {
            throw new SiteException(e);
        }

    }

    private long lastModified = 0;

    /**
     * Checks if the tree file has been modified externally and reloads the site
     * tree.
     */
    protected void checkModified() {
        if (this.area.equals(Publication.LIVE_AREA) && this.treefile.isFile()
                && this.treefile.lastModified() > this.lastModified) {

            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Sitetree [" + this.treefile + "] has changed: reloading.");
            }

            try {
                this.document = DocumentHelper.readDocument(this.treefile);
            } catch (Exception e) {
                throw new IllegalStateException(e.getMessage());
            }
            this.lastModified = this.treefile.lastModified();
        }
    }

    /**
     * Create a new DefaultSiteTree xml document.
     * @return the new site document
     * @throws ParserConfigurationException if an error occurs
     */
    public Document createDocument() throws ParserConfigurationException {
        this.document = DocumentHelper.createDocument(NAMESPACE_URI, "site", null);

        Element root = this.document.getDocumentElement();
        root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        root
                .setAttribute("xsi:schemaLocation",
                        "http://apache.org/cocoon/lenya/sitetree/1.0  ../../../../resources/entities/sitetree.xsd");

        return this.document;
    }

    /**
     * Find a node in a subtree. The search is started at the given node. The
     * list of ids contains the document-id split by "/".
     * @param node where to start the search
     * @param ids list of node ids
     * @return the node that matches the path given in the list of ids
     */
    protected Node findNode(Node node, List ids) {

        checkModified();

        if (ids.size() < 1) {
            return node;
        }
        NodeList nodes = node.getChildNodes();

        for (int i = 0; i < nodes.getLength(); i++) {
            NamedNodeMap attributes = nodes.item(i).getAttributes();

            if (attributes != null) {
                Node idAttribute = attributes.getNamedItem("id");

                if (idAttribute != null && !"".equals(idAttribute.getNodeValue())
                        && idAttribute.getNodeValue().equals(ids.get(0))) {
                    return findNode(nodes.item(i), ids.subList(1, ids.size()));
                }
            }
        }

        // node wasn't found
        return null;
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTree#addNode(org.apache.lenya.cms.site.tree.SiteTreeNode,
     *      java.lang.String)
     */
    public void addNode(SiteTreeNode node, String refDocumentId) throws SiteException {
        this.addNode(node.getParent().getAbsoluteId(), node.getId(), node.getLabels(), node
                .getHref(), node.getSuffix(), node.hasLink(), refDocumentId);
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTree#addNode(java.lang.String,
     *      java.lang.String, org.apache.lenya.cms.site.Label[])
     */
    public void addNode(String parentid, String id, Label[] labels) throws SiteException {
        addNode(parentid, id, labels, null, null, false);
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTree#addNode(org.apache.lenya.cms.site.tree.SiteTreeNode)
     */
    public void addNode(SiteTreeNode node) throws SiteException {
        this.addNode(node, null);
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTree#addNode(java.lang.String,
     *      org.apache.lenya.cms.site.Label[], java.lang.String,
     *      java.lang.String, boolean, java.lang.String)
     */
    public void addNode(String documentid, Label[] labels, String href, String suffix,
            boolean link, String refDocumentId) throws SiteException {
		StringBuffer buf = new StringBuffer();
        StringTokenizer st = new StringTokenizer(documentid, "/");
        int length = st.countTokens();

        for (int i = 0; i < (length - 1); i++) {
            buf.append("/" + st.nextToken());
        }
        String parentid = buf.toString();
        String id = st.nextToken();
        this.addNode(parentid, id, labels, href, suffix, link, refDocumentId);
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTree#addNode(java.lang.String,
     *      org.apache.lenya.cms.site.Label[], java.lang.String,
     *      java.lang.String, boolean)
     */
    public void addNode(String documentid, Label[] labels, String href, String suffix, boolean link)
            throws SiteException {
        this.addNode(documentid, labels, href, suffix, link, null);
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTree#addNode(java.lang.String,
     *      java.lang.String, org.apache.lenya.cms.site.Label[],
     *      java.lang.String, java.lang.String, boolean)
     */
    public void addNode(String parentid, String id, Label[] labels, String href, String suffix,
            boolean link) throws SiteException {
        this.addNode(parentid, id, labels, href, suffix, link, null);
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTree#addNode(java.lang.String,
     *      java.lang.String, org.apache.lenya.cms.site.Label[],
     *      java.lang.String, java.lang.String, boolean, java.lang.String)
     */
    public void addNode(String parentid, String id, Label[] labels, String href, String suffix,
            boolean link, String refDocumentId) throws SiteException {

        Node parentNode = getNodeInternal(parentid);

        if (parentNode == null) {
            throw new SiteException("Parentid: " + parentid + " in " + this.area + " tree not found");
        }

        getLogger().debug("PARENT ELEMENT: " + parentNode);

        // Check if child already exists
        Node childNode = getNodeInternal(parentid + "/" + id);

        if (childNode != null) {
            getLogger().info("This node: " + parentid + "/" + id + " has already been inserted");

            return;
        }

        // Create node
        NamespaceHelper helper = new NamespaceHelper(NAMESPACE_URI, "", this.document);
        Element child = helper.createElement(SiteTreeNodeImpl.NODE_NAME);
        child.setAttribute(SiteTreeNodeImpl.ID_ATTRIBUTE_NAME, id);

        if ((href != null) && (href.length() > 0)) {
            child.setAttribute(SiteTreeNodeImpl.HREF_ATTRIBUTE_NAME, href);
        }

        if ((suffix != null) && (suffix.length() > 0)) {
            child.setAttribute(SiteTreeNodeImpl.SUFFIX_ATTRIBUTE_NAME, suffix);
        }

        if (link) {
            child.setAttribute(SiteTreeNodeImpl.LINK_ATTRIBUTE_NAME, "true");
        }

        for (int i = 0; i < labels.length; i++) {
            String labelName = labels[i].getLabel();
            Element label = helper.createElement(SiteTreeNodeImpl.LABEL_NAME, labelName);
            String labelLanguage = labels[i].getLanguage();

            if ((labelLanguage != null) && (labelLanguage.length() > 0)) {
                label.setAttribute(SiteTreeNodeImpl.LANGUAGE_ATTRIBUTE_NAME, labelLanguage);
            }

            child.appendChild(label);
        }

        // Add Node
        if (refDocumentId != null && !refDocumentId.equals("")) {
            Node nextSibling = getNodeInternal(refDocumentId);
            if (nextSibling != null) {
                parentNode.insertBefore(child, nextSibling);
            } else {
                parentNode.appendChild(child);
            }
        } else {
            parentNode.appendChild(child);
        }
        getLogger().debug("Tree has been modified: " + this.document.getDocumentElement());

    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTree#addLabel(java.lang.String,
     *      org.apache.lenya.cms.site.Label)
     */
    public void addLabel(String documentId, Label label) {
        SiteTreeNode node = getNode(documentId);
        if (node != null) {
            node.addLabel(label);
        }
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTree#removeLabel(java.lang.String,
     *      org.apache.lenya.cms.site.Label)
     */
    public void removeLabel(String documentId, Label label) {
        SiteTreeNode node = getNode(documentId);
        if (node != null) {
            node.removeLabel(label);
        }
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTree#removeNode(java.lang.String)
     */
    public SiteTreeNode removeNode(String documentId) {
        assert documentId != null;

        Node node = removeNodeInternal(documentId);

        if (node == null) {
            return null;
        }

        SiteTreeNode newNode = new SiteTreeNodeImpl(node);
        ContainerUtil.enableLogging(newNode, getLogger());
        return newNode;
    }

    /**
     * removes the node corresponding to the given document-id and returns it
     * @param documentId the document-id of the Node to be removed
     * @return the <code>Node</code> that was removed
     */
    private Node removeNodeInternal(String documentId) {
        Node node = this.getNodeInternal(documentId);
        Node parentNode = node.getParentNode();
        Node newNode = parentNode.removeChild(node);

        return newNode;
    }

    /**
     * Find a node for a given document-id
     * 
     * @param documentId the document-id of the Node that we're trying to get
     * 
     * @return the Node if there is a Node for the given document-id, null
     *         otherwise
     */
    private Node getNodeInternal(String documentId) {
        StringTokenizer st = new StringTokenizer(documentId, "/");
        ArrayList ids = new ArrayList();

        while (st.hasMoreTokens()) {
            ids.add(st.nextToken());
        }

        Node node = findNode(this.document.getDocumentElement(), ids);
        return node;
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTree#getNode(java.lang.String)
     */
    public SiteTreeNode getNode(String documentId) {
        assert documentId != null;

        SiteTreeNode treeNode = null;

        Node node = getNodeInternal(documentId);
        if (node != null) {
            treeNode = new SiteTreeNodeImpl(node);
            ContainerUtil.enableLogging(treeNode, getLogger());
        }

        return treeNode;
    }

    /**
     * @see org.apache.lenya.cms.publication.SiteTree#getTopNodes()
     */
    public SiteTreeNode[] getTopNodes() {
        List childElements = new ArrayList();

        NamespaceHelper helper = new NamespaceHelper(NAMESPACE_URI, "", document);
 
        Element[] elements = helper.getChildren((Element) document.getDocumentElement(), SiteTreeNodeImpl.NODE_NAME);

        for (int i = 0; i < elements.length; i++) {
            SiteTreeNode newNode = new SiteTreeNodeImpl(elements[i]);
            childElements.add(newNode);
        }

        return (SiteTreeNode[]) childElements.toArray(new SiteTreeNode[childElements.size()]);
    }
    
    /**
     * Move up the node amongst its siblings.
     * 
     * @param documentid The document id for the node.
     * @throws SiteException if the moving failed.
     */
    public void moveUp(String documentid) throws SiteException {
        Node node = this.getNodeInternal(documentid);
        if (node == null) {
            throw new SiteException("Node to move: " + documentid + " not found");
        }
        Node parentNode = node.getParentNode();
        if (parentNode == null) {
            throw new SiteException("Parentid of node with documentid: " + documentid
                    + " not found");
        }

        Node previousNode;
        try {
            previousNode = XPathAPI.selectSingleNode(node,
                    "(preceding-sibling::*[local-name() = 'node'])[last()]");
        } catch (TransformerException e) {
            throw new SiteException(e);
        }

        if (previousNode == null) {
            getLogger().warn("Couldn't found a preceding sibling");
            return;
        }
        Node insertNode = parentNode.removeChild(node);
        parentNode.insertBefore(insertNode, previousNode);
    }

    /**
     * Move down the node amongst its siblings.
     * 
     * @param documentid The document id for the node.
     * @throws SiteException if the moving failed.
     */
    public void moveDown(String documentid) throws SiteException {
        Node node = this.getNodeInternal(documentid);
        if (node == null) {
            throw new SiteException("Node to move: " + documentid + " not found");
        }
        Node parentNode = node.getParentNode();
        if (parentNode == null) {
            throw new SiteException("Parentid of node with documentid: " + documentid
                    + " not found");
        }
        Node nextNode;
        try {
            nextNode = XPathAPI.selectSingleNode(node,
                    "following-sibling::*[local-name() = 'node'][position()=2]");
        } catch (TransformerException e) {
            throw new SiteException(e);
        }

        Node insertNode = parentNode.removeChild(node);

        if (nextNode == null) {
            getLogger().warn("Couldn't found the second following sibling");
            parentNode.appendChild(insertNode);
        } else {
            parentNode.insertBefore(insertNode, nextNode);
        }
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTree#importSubtree(org.apache.lenya.cms.site.tree.SiteTreeNode,
     *      org.apache.lenya.cms.site.tree.SiteTreeNode, java.lang.String,
     *      java.lang.String)
     */
    public void importSubtree(SiteTreeNode newParent, SiteTreeNode subtreeRoot, String newid,
            String refDocumentId) throws SiteException {
        assert subtreeRoot != null;
        assert newParent != null;
        String parentId = newParent.getAbsoluteId();
        String id = newid;

        this.addNode(parentId, id, subtreeRoot.getLabels(), subtreeRoot.getHref(), subtreeRoot
                .getSuffix(), subtreeRoot.hasLink(), refDocumentId);
        newParent = this.getNode(parentId + "/" + id);
        if (newParent == null) {
            throw new SiteException("The added node was not found.");
        }
        SiteTreeNode[] children = subtreeRoot.getChildren();
        if (children == null) {
            getLogger().info("The node " + subtreeRoot.toString() + " has no children");
            return;
        }
        for (int i = 0; i < children.length; i++) {
            importSubtree(newParent, children[i], children[i].getId(), null);
        }
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTree#save()
     */
    public void save() throws SiteException {
        try {
            DocumentHelper.writeDocument(this.document, this.treefile);
        } catch (TransformerException e) {
            throw new SiteException("The document [" + this.document.getLocalName()
                    + "] could not be transformed");
        } catch (IOException e) {
            throw new SiteException("The saving of document [" + this.document.getLocalName()
                    + "] failed");
        }
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTree#setLabel(java.lang.String,
     *      org.apache.lenya.cms.site.Label)
     */
    public void setLabel(String documentId, Label label) {
        SiteTreeNode node = getNode(documentId);
        if (node != null) {
            node.setLabel(label);
        }
    }

}