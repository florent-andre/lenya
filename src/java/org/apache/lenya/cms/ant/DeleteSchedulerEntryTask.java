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

/* $Id$  */

package org.apache.lenya.cms.ant;

import java.io.File;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.scheduler.LoadQuartzServlet;
import org.apache.lenya.cms.site.Label;
import org.apache.lenya.cms.site.tree.SiteTree;
import org.apache.lenya.cms.site.tree.SiteTreeNode;
import org.apache.lenya.cms.site.tree.SiteTreeNodeVisitor;
import org.apache.tools.ant.BuildException;

/**
 * Moves the scheduler entry for a document.
 */
public class DeleteSchedulerEntryTask extends PublicationTask implements SiteTreeNodeVisitor {

    /**
     * @see org.apache.tools.ant.Task#execute()
     */
    public void execute() throws BuildException {
        try {
            log("Document ID: [" + documentId + "]");
            log("Area:        [" + area + "]");

            SiteTree tree = getSiteTree(area);
            SiteTreeNode node = tree.getNode(documentId);

            node.acceptSubtree(this);
        } catch (Exception e) {
            throw new BuildException(e);
        }
    }

    private String area, documentId, servletContextPath;

    /**
     * @param string The area.
     */
    public void setArea(String string) {
        area = string;
    }

    /**
     * @param string The document-id.
     */
    public void setDocumentId(String string) {
        documentId = string;
    }

    /**
     * Sets the servlet context path.
     * @param servletContextPath A string.
     */
    public void setServletContextPath(String servletContextPath) {
        this.servletContextPath = servletContextPath;
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTreeNodeVisitor#visitSiteTreeNode(org.apache.lenya.cms.site.tree.SiteTreeNode)
     */
    public void visitSiteTreeNode(SiteTreeNode node) throws DocumentException {

        Label[] labels = node.getLabels();
        for (int i = 0; i < labels.length; i++) {

            String language = labels[i].getLanguage();

            try {
                Document document = getIdentityMap().getFactory().get(area, documentId, language);

                String servletContext = new File(servletContextPath).getCanonicalPath();
                log("Deleting scheduler entry for document [" + document + "]");
                log("Resolving servlet [" + servletContext + "]");

                LoadQuartzServlet servlet = LoadQuartzServlet.getServlet(servletContext);
                servlet.deleteDocumentJobs(document);

            } catch (Exception e) {
                throw new DocumentException(e);
            }

        }
    }

}
