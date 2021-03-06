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
package org.apache.lenya.cms.site.tree;

import org.apache.lenya.cms.site.AbstractLink;
import org.apache.lenya.cms.site.SiteNode;
import org.apache.lenya.xml.DocumentHelper;
import org.w3c.dom.Element;

/**
 * SiteTree link.
 */
public class SiteTreeLink extends AbstractLink {

    protected SiteTreeLink(SiteNode node, String _language, Element element) {
        super(node, "", _language);
        this.element = element;
    }

    public void delete() {
        SiteTreeNodeImpl node = (SiteTreeNodeImpl) getNode();
        node.removeLabel(getLanguage());
    }

    private Element element;

    /**
     * Set the actual label of the label object.
     * 
     * @param label The label.
     */
    public void setLabel(String label) {
        DocumentHelper.setSimpleElementText(this.element, label);
        ((SiteTreeNodeImpl) getNode()).save();
    }

    public String getLabel() {
        return DocumentHelper.getSimpleElementText(this.element);
    }

}
