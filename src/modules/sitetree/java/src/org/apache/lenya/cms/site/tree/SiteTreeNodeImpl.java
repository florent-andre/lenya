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

import java.util.ArrayList;
import java.util.List;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.site.Label;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Concrete implementation of the <code>SiteTreeNode</code> interface.
 * 
 * @see org.apache.lenya.cms.site.tree.SiteTreeNode
 * @version $Id: SiteTreeNodeImpl.java 155316 2005-02-25 10:53:29Z andreas $
 */
public class SiteTreeNodeImpl extends AbstractLogEnabled implements SiteTreeNode {

    /**
     * <code>ID_ATTRIBUTE_NAME</code> The id attribute
     */
    public static final String ID_ATTRIBUTE_NAME = "id";
    /**
     * <code>UUID_ATTRIBUTE_NAME</code> The uuid attribute
     */
    public static final String UUID_ATTRIBUTE_NAME = "uuid";
    /**
     * <code>ISIBLEINNAV_ATTRIBUTE_NAME</code>The visibleinnav attribute
     */
    public static final String VISIBLEINNAV_ATTRIBUTE_NAME="visibleinnav";
    /**
     * <code>HREF_ATTRIBUTE_NAME</code> The href attribute
     */
    public static final String HREF_ATTRIBUTE_NAME = "href";
    /**
     * <code>SUFFIX_ATTRIBUTE_NAME</code> The suffix attribute
     */
    public static final String SUFFIX_ATTRIBUTE_NAME = "suffix";
    /**
     * <code>LINK_ATTRIBUTE_NAME</code> The link attribute
     */
    public static final String LINK_ATTRIBUTE_NAME = "link";
    /**
     * <code>LANGUAGE_ATTRIBUTE_NAME</code> The language attribute
     */
    public static final String LANGUAGE_ATTRIBUTE_NAME = "xml:lang";
    /**
     * <code>NODE_NAME</code> The node name
     */
    public static final String NODE_NAME = "node";
    /**
     * <code>LABEL_NAME</code> The label name
     */
    public static final String LABEL_NAME = "label";

    private Node node = null;

    /**
     * Creates a new SiteTreeNodeImpl object.
     * 
     * @param _node the node which is to be wrapped by this SiteTreeNode
     */
    protected SiteTreeNodeImpl(Node _node) {
        this.node = _node;
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTreeNode#getId()
     */
    public String getId() {
        if (this.node == this.node.getOwnerDocument().getDocumentElement()) {
            return "";
        }
        return this.node.getAttributes().getNamedItem(ID_ATTRIBUTE_NAME).getNodeValue();
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTreeNode#getUUID()
     */
    public String getUUID() {
        if (this.node == this.node.getOwnerDocument().getDocumentElement()) {
            getLogger().warn("Node equals OwnerDocument: " + this);
            return "";
        }
        return this.node.getAttributes().getNamedItem(UUID_ATTRIBUTE_NAME).getNodeValue();
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTreeNode#getAbsoluteId()
     */
    public String getAbsoluteId() {
        String absoluteId = "";
        Node currentNode = this.node;
        NamedNodeMap attributes = null;
        Node idAttribute = null;

        while (currentNode != null) {
            attributes = currentNode.getAttributes();

            if (attributes == null) {
                break;
            }

            idAttribute = attributes.getNamedItem(ID_ATTRIBUTE_NAME);

            if (idAttribute == null) {
                break;
            }

            absoluteId = "/" + idAttribute.getNodeValue() + absoluteId;
            currentNode = currentNode.getParentNode();
        }

        return absoluteId;
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTreeNode#getLabels()
     */
    public Label[] getLabels() {
        ArrayList labels = new ArrayList();

        NodeList children = this.node.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);

            if ((child.getNodeType() == Node.ELEMENT_NODE)
                    && child.getNodeName().equals(LABEL_NAME)) {
                String labelName = DocumentHelper.getSimpleElementText((Element) child);
                String labelLanguage = null;
                Node languageAttribute = child.getAttributes()
                        .getNamedItem(LANGUAGE_ATTRIBUTE_NAME);

                if (languageAttribute != null) {
                    labelLanguage = languageAttribute.getNodeValue();
                }

                labels.add(new Label(labelName, labelLanguage));
            }
        }

        return (Label[]) labels.toArray(new Label[labels.size()]);
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTreeNode#getLabel(java.lang.String)
     */
    public Label getLabel(String xmlLanguage) {
        Label label = null;
        Label[] labels = getLabels();
        String language = null;

        for (int i = 0; i < labels.length; i++) {
            language = labels[i].getLanguage();

            // FIXME: This expression is too complicated
            // considering there can no longer be any labels with
            // a null language, i.e. each label must have a language.
            if ((((xmlLanguage == null) || (xmlLanguage.equals(""))) && (language == null))
                    || ((language != null) && (language.equals(xmlLanguage)))) {
                label = labels[i];

                break;
            }
        }

        return label;
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTreeNode#addLabel(org.apache.lenya.cms.site.Label)
     */
    public void addLabel(Label label) {
        if (getLabel(label.getLanguage()) == null) {
            // only add the label if there is no label with the same language
            // yet.

            NamespaceHelper helper = getNamespaceHelper();
            Element labelElem = helper.createElement(SiteTreeNodeImpl.LABEL_NAME, label.getLabel());

            labelElem.setAttribute(SiteTreeNodeImpl.LANGUAGE_ATTRIBUTE_NAME, label.getLanguage());

            node.insertBefore(labelElem, node.getFirstChild());
        }
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTreeNode#removeLabel(org.apache.lenya.cms.site.Label)
     */
    public void removeLabel(Label label) {
        if (getLabel(label.getLanguage()) != null) {
            // this node doesn't contain this label

            NodeList children = this.node.getChildNodes();

            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);

                if ((child.getNodeType() == Node.ELEMENT_NODE)
                        && child.getNodeName().equals(LABEL_NAME)) {

                    Node languageAttribute = child.getAttributes()
                            .getNamedItem(LANGUAGE_ATTRIBUTE_NAME);

                    if (languageAttribute != null
                            && languageAttribute.getNodeValue().equals(label.getLanguage())) {
                        this.node.removeChild(child);
                        break;
                    }
                }
            }
        }
    }
    
    /**
     * @see org.apache.lenya.cms.site.tree.SiteTreeNode#visibleInNav()
     */
    public boolean visibleInNav() {
        Node attribute = this.node.getAttributes().getNamedItem(VISIBLEINNAV_ATTRIBUTE_NAME);
        
        if (attribute != null){
            return attribute.getNodeValue().equals("true");
        }
        return true;
    }
    
    /**
     * @see org.apache.lenya.cms.site.tree.SiteTreeNode#getHref()
     */
    public String getHref() {
        Node attribute = this.node.getAttributes().getNamedItem(HREF_ATTRIBUTE_NAME);

        if (attribute != null) {
            return attribute.getNodeValue();
        }
        return null;
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTreeNode#getSuffix()
     */
    public String getSuffix() {
        Node attribute = this.node.getAttributes().getNamedItem(SUFFIX_ATTRIBUTE_NAME);

        if (attribute != null) {
            return attribute.getNodeValue();
        }
        return null;
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTreeNode#hasLink()
     */
    public boolean hasLink() {
        Node attribute = this.node.getAttributes().getNamedItem(LINK_ATTRIBUTE_NAME);

        if (attribute != null) {
            return attribute.getNodeValue().equals("true");
        }
        return false;
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTreeNode#getChildren()
     */
    public SiteTreeNode[] getChildren() {
        List childElements = new ArrayList();

        NamespaceHelper helper = getNamespaceHelper();
        Element[] elements = helper.getChildren((Element) this.node, SiteTreeNodeImpl.NODE_NAME);

        for (int i = 0; i < elements.length; i++) {
            SiteTreeNode newNode = new SiteTreeNodeImpl(elements[i]);
            // FIXME: the if is a workaround
            if (getLogger() != null)
                ContainerUtil.enableLogging(newNode, getLogger());
            childElements.add(newNode);
        }

        return (SiteTreeNode[]) childElements.toArray(new SiteTreeNode[childElements.size()]);
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTreeNode#removeChildren()
     */
    public SiteTreeNode[] removeChildren() {
        List childElements = new ArrayList();
        NamespaceHelper helper = getNamespaceHelper();
        Element[] elements = helper.getChildren((Element) this.node, SiteTreeNodeImpl.NODE_NAME);
        for (int i = 0; i < elements.length; i++) {
            this.node.removeChild(elements[i]);
            SiteTreeNode newNode = new SiteTreeNodeImpl(elements[i]);
            ContainerUtil.enableLogging(newNode, getLogger());
            childElements.add(newNode);
        }
        return (SiteTreeNode[]) childElements.toArray(new SiteTreeNode[childElements.size()]);
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTreeNode#getNextSiblings()
     */
    public SiteTreeNode[] getNextSiblings() {
        List siblingElements = new ArrayList();

        NamespaceHelper helper = getNamespaceHelper();
        Element[] elements = helper
                .getNextSiblings((Element) this.node, SiteTreeNodeImpl.NODE_NAME);

        for (int i = 0; i < elements.length; i++) {
            SiteTreeNode newNode = new SiteTreeNodeImpl(elements[i]);
            ContainerUtil.enableLogging(newNode, getLogger());
            siblingElements.add(newNode);
        }

        return (SiteTreeNode[]) siblingElements.toArray(new SiteTreeNode[siblingElements.size()]);
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTreeNode#getPrecedingSiblings()
     */
    public SiteTreeNode[] getPrecedingSiblings() {
        List siblingElements = new ArrayList();

        NamespaceHelper helper = getNamespaceHelper();
        Element[] elements = helper.getPrecedingSiblings((Element) this.node,
                SiteTreeNodeImpl.NODE_NAME);

        for (int i = 0; i < elements.length; i++) {
            SiteTreeNode newNode = new SiteTreeNodeImpl(elements[i]);
            ContainerUtil.enableLogging(newNode, getLogger());
            siblingElements.add(newNode);
        }

        return (SiteTreeNode[]) siblingElements.toArray(new SiteTreeNode[siblingElements.size()]);
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTreeNode#getNextSiblingDocumentId()
     */
    public String getNextSiblingDocumentId() {
        SiteTreeNode[] siblings = getNextSiblings();
        if (siblings != null && siblings.length > 0) {
            return siblings[0].getAbsoluteId();
        }
        return null;
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTreeNode#accept(org.apache.lenya.cms.site.tree.SiteTreeNodeVisitor)
     */
    public void accept(SiteTreeNodeVisitor visitor) throws DocumentException {
        visitor.visitSiteTreeNode(this);
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTreeNode#acceptSubtree(org.apache.lenya.cms.site.tree.SiteTreeNodeVisitor)
     */
    public void acceptSubtree(SiteTreeNodeVisitor visitor) throws DocumentException {
        this.accept(visitor);
        SiteTreeNode[] children = this.getChildren();
        if (children == null) {
            getLogger().info("The node " + this.getId() + " has no children");
            return;
        }
        for (int i = 0; i < children.length; i++) {
            children[i].acceptSubtree(visitor);
        }
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTreeNode#acceptReverseSubtree(org.apache.lenya.cms.site.tree.SiteTreeNodeVisitor)
     */
    public void acceptReverseSubtree(SiteTreeNodeVisitor visitor) throws DocumentException {
        List orderedNodes = this.postOrder();
        for (int i = 0; i < orderedNodes.size(); i++) {
            SiteTreeNode _node = (SiteTreeNode) orderedNodes.get(i);
            _node.accept(visitor);
        }
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTreeNode#postOrder()
     */
    public List postOrder() {
        List list = new ArrayList();
        SiteTreeNode[] children = this.getChildren();
        for (int i = 0; i < children.length; i++) {
            List orderedChildren = children[i].postOrder();
            list.addAll(orderedChildren);
        }
        list.add(this);
        return list;
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTreeNode#setLabel(org.apache.lenya.cms.site.Label)
     */
    public void setLabel(Label label) {
        Label existingLabel = getLabel(label.getLanguage());
        if (existingLabel != null) {
            removeLabel(existingLabel);
        }
        addLabel(label);
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTreeNode#setNodeAttribute(String, String)
     */    
    public void setNodeAttribute (String attributeName, String attributeValue) {
        Element element = (Element) this.node;
        element.setAttribute(attributeName, attributeValue);
    }
    
    /**
     * @see org.apache.lenya.cms.site.tree.SiteTreeNode#getChildren(java.lang.String)
     */
    public SiteTreeNode[] getChildren(String language) {
        SiteTreeNode[] children = getChildren();
        List languageChildren = new ArrayList();

        for (int i = 0; i < children.length; i++) {
            if (children[i].getLabel(language) != null) {
                languageChildren.add(children[i]);
            }
        }

        return (SiteTreeNode[]) languageChildren.toArray(new SiteTreeNode[languageChildren.size()]);
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTreeNode#getParent()
     */
    public SiteTreeNode getParent() {
        SiteTreeNode parent = null;

        Node parentNode = this.node.getParentNode();
        if (parentNode.getNodeType() == Node.ELEMENT_NODE
                && parentNode.getLocalName().equals(NODE_NAME)) {
            parent = new SiteTreeNodeImpl(parentNode);
            ContainerUtil.enableLogging(parent, getLogger());
        }

        return parent;
    }

    /**
     * Returns the namespace helper of the sitetree XML document.
     * @return A namespace helper.
     */
    protected NamespaceHelper getNamespaceHelper() {
        NamespaceHelper helper = new NamespaceHelper(DefaultSiteTree.NAMESPACE_URI, "", this.node
                .getOwnerDocument());
        return helper;
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTreeNode#getParent(java.lang.String)
     */
    public SiteTreeNode getParent(String language) {
        SiteTreeNode parent = getParent();

        Label label = parent.getLabel(language);
        if (label == null) {
            parent = null;
        }

        return parent;
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTreeNode#preOrder()
     */
    public List preOrder() {
        List list = new ArrayList();
        list.add(this);
        SiteTreeNode[] children = this.getChildren();
        for (int i = 0; i < children.length; i++) {
            List orderedChildren = children[i].preOrder();
            list.addAll(orderedChildren);
        }
        return list;
    }

    public String getNodeAttribute(String attributeName) {
        Element element = (Element) this.node;
        return element.getAttribute(attributeName);
    }

}