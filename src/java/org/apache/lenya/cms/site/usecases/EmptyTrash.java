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
package org.apache.lenya.cms.site.usecases;

import java.util.Arrays;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentIdentityMap;
import org.apache.lenya.cms.publication.DocumentManager;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.PublicationFactory;
import org.apache.lenya.cms.publication.util.DocumentSet;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteManager;
import org.apache.lenya.cms.usecase.AbstractUsecase;

/**
 * Empty the trash.
 *
 * @version $Id:$
 */
public class EmptyTrash extends AbstractUsecase {
    
    protected static final String DOCUMENTS = "documents";

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();
        try {
            Document[] documents = getTrashDocuments();
            setParameter(DOCUMENTS, Arrays.asList(documents));
        } catch (SiteException e) {
            throw new RuntimeException(e);
        } catch (PublicationException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();
        
        DocumentManager documentManager = null;
        try {
            documentManager = (DocumentManager) this.manager.lookup(DocumentManager.ROLE);
            Document[] documents = getTrashDocuments();
            DocumentSet set = new DocumentSet(documents);
            documentManager.delete(set);
        }
        finally {
            if (documentManager != null) {
                this.manager.release(documentManager);
            }
        }
    }
    
    /**
     * @return The documents in the trash area.
     * @throws PublicationException if an error occurs.
     * @throws SiteException if an error occurs.
     */
    protected Document[] getTrashDocuments() throws PublicationException, SiteException {
        PublicationFactory factory = PublicationFactory.getInstance(getLogger());
        Publication publication = factory.getPublication(this.manager, getSourceURL());
        DocumentIdentityMap identityMap = new DocumentIdentityMap(this.manager);
        Document[] documents;
        
        ServiceSelector selector = null;
        SiteManager siteManager = null;
        try {
            selector = (ServiceSelector) this.manager.lookup(SiteManager.ROLE + "Selector");
            siteManager = (SiteManager) selector.select(publication
                    .getSiteManagerHint());
            documents = siteManager.getDocuments(identityMap, publication, Publication.TRASH_AREA);
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        } finally {
            if (selector != null) {
                if (siteManager != null) {
                    selector.release(siteManager);
                }
                this.manager.release(selector);
            }
        }
        
        return documents;
    }
}
