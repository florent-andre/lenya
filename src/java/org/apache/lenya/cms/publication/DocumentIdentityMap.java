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
package org.apache.lenya.cms.publication;

import java.io.File;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.excalibur.source.SourceUtil;
import org.apache.lenya.transaction.Identifiable;
import org.apache.lenya.transaction.IdentifiableFactory;
import org.apache.lenya.transaction.IdentityMap;
import org.apache.lenya.transaction.IdentityMapImpl;

/**
 * A DocumentIdentityMap avoids the multiple instanciation of a document object.
 * 
 * @version $Id$
 */
public class DocumentIdentityMap extends AbstractLogEnabled implements IdentifiableFactory {

    private IdentityMap map;
    protected ServiceManager manager;
    
    /**
     * @return The identity map.
     */
    public IdentityMap getIdentityMap() {
        return this.map;
    }
    
    /**
     * Ctor.
     * @param map The identity map to use.
     * @param manager The service manager.
     * @param logger The logger to use.
     */
    public DocumentIdentityMap(IdentityMap map, ServiceManager manager, Logger logger) {
        this.map = map;
        this.manager = manager;
        ContainerUtil.enableLogging(this, logger);
    }

    /**
     * Ctor.
     * @param serviceManager The service manager.
     * @param logger The logger.
     */
    public DocumentIdentityMap(ServiceManager serviceManager, Logger logger) {
        this(new IdentityMapImpl(logger), serviceManager, logger);
    }

    /**
     * Returns a document.
     * @param publication The publication.
     * @param area The area.
     * @param documentId The document ID.
     * @param language The language.
     * @return A document.
     * @throws DocumentBuildException if an error occurs.
     */
    public Document get(Publication publication, String area, String documentId, String language)
            throws DocumentBuildException {

        if (getLogger().isDebugEnabled())
            getLogger().debug("DocumentIdentityMap::get() called on publication [" + publication.getId() + "], area [" + area + "], documentId [" + documentId + "], language [" + language + "]");

        String key = getKey(publication, area, documentId, language);

        if (getLogger().isDebugEnabled())
            getLogger().debug("DocumentIdentityMap::get() got key [" + key + "] from DocumentFactory");

        return (Document) getIdentityMap().get(this, key);
    }

    /**
     * Returns the document identified by a certain web application URL.
     * @param webappUrl The web application URL.
     * @return A document.
     * @throws DocumentBuildException if an error occurs.
     */
    public Document getFromURL(String webappUrl) throws DocumentBuildException {
        String key = getKey(webappUrl);
        return (Document) getIdentityMap().get(this, key);
    }

    /**
     * Builds a clone of a document for another language.
     * @param document The document to clone.
     * @param language The language of the target document.
     * @return A document.
     * @throws DocumentBuildException if an error occurs.
     */
    public Document getLanguageVersion(Document document, String language)
            throws DocumentBuildException {
        return get(document.getPublication(), document.getArea(), document.getId(), language);
    }

    /**
     * Builds a clone of a document for another area.
     * @param document The document to clone.
     * @param area The area of the target document.
     * @return A document.
     * @throws DocumentBuildException if an error occurs.
     */
    public Document getAreaVersion(Document document, String area) throws DocumentBuildException {
        return get(document.getPublication(), area, document.getId(), document.getLanguage());
    }

    /**
     * Returns the parent of a document.
     * @param document A document.
     * @return A document or <code>null</code> if the document has no parent.
     * @throws DocumentBuildException if an error occurs.
     */
    public Document getParent(Document document) throws DocumentBuildException {
        Document parent = null;
        int lastSlashIndex = document.getId().lastIndexOf("/");
        if (lastSlashIndex > 0) {
            String parentId = document.getId().substring(0, lastSlashIndex);
            parent = get(document.getPublication(), document.getArea(), parentId, document
                    .getLanguage());
        }
        return parent;
    }

    /**
     * Returns the parent of a document.
     * @param document A document.
     * @param defaultDocumentId The document ID to use if the document has no parent.
     * @return A document.
     * @throws DocumentBuildException if an error occurs.
     */
    public Document getParent(Document document, String defaultDocumentId)
            throws DocumentBuildException {
        Document parent = getParent(document);
        if (parent == null) {
            parent = get(document.getPublication(), document.getArea(), defaultDocumentId, document
                    .getLanguage());
        }
        return parent;
    }

    /**
     * Builds a document for the default language.
     * @param publication The publication.
     * @param area The area.
     * @param documentId The document ID.
     * @return A document.
     * @throws DocumentBuildException if an error occurs.
     */
    public Document get(Publication publication, String area, String documentId)
            throws DocumentBuildException {
        return get(publication, area, documentId, publication.getDefaultLanguage());
    }

    /**
     * Checks if a string represents a valid document ID.
     * @param id The string.
     * @return A boolean value.
     */
    public boolean isValidDocumentId(String id) {

        if (!id.startsWith("/")) {
            return false;
        }

        String[] snippets = id.split("/");

        if (snippets.length < 2) {
            return false;
        }

        for (int i = 1; i < snippets.length; i++) {
            if (!snippets[i].matches("[a-zA-Z0-9\\-]+")) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if a webapp URL represents a document.
     * @param webappUrl A web application URL.
     * @return A boolean value.
     * @throws DocumentBuildException if an error occurs.
     */
    public boolean isDocument(String webappUrl) throws DocumentBuildException {

        try {
            PublicationFactory factory = PublicationFactory.getInstance(getLogger());
            Publication publication = factory.getPublication(this.manager, webappUrl);
            if (publication.exists()) {

                ServiceSelector selector = null;
                DocumentBuilder builder = null;
                try {
                    selector = (ServiceSelector) this.manager.lookup(DocumentBuilder.ROLE
                            + "Selector");
                    builder = (DocumentBuilder) selector.select(publication
                            .getDocumentBuilderHint());
                    return builder.isDocument(publication, webappUrl);
                } finally {
                    if (selector != null) {
                        if (builder != null) {
                            selector.release(builder);
                        }
                        this.manager.release(selector);
                    }
                }
            } else {
                return false;
            }
        } catch (ServiceException e) {
            throw new DocumentBuildException(e);
        } catch (PublicationException e) {
            throw new DocumentBuildException(e);
        }
    }

    /**
     * Builds a document key.
     * @param publication The publication.
     * @param area The area.
     * @param documentId The document ID.
     * @param language The language.
     * @return A key.
     */
    public String getKey(Publication publication, String area, String documentId, String language) {
        return publication.getId() + ":" + area + ":" + documentId + ":" + language;
    }

    /**
     * Builds a document key.
     * @param webappUrl The web application URL.
     * @return A key.
     */
    public String getKey(String webappUrl) {
        ServiceSelector selector = null;
        DocumentBuilder builder = null;
        SourceResolver resolver = null;
        Source source = null;
        Document document;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            source = resolver.resolveURI("context://");
            File servletContext = SourceUtil.getFile(source);

            PublicationFactory factory = PublicationFactory.getInstance(getLogger());
            URLInformation info = new URLInformation(webappUrl);
            String publicationId = info.getPublicationId();
            Publication publication = factory.getPublication(publicationId, servletContext
                    .getAbsolutePath());

            selector = (ServiceSelector) this.manager.lookup(DocumentBuilder.ROLE + "Selector");
            builder = (DocumentBuilder) selector.select(publication.getDocumentBuilderHint());
            document = builder.buildDocument(this, publication, webappUrl);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (selector != null) {
                if (builder != null) {
                    selector.release(builder);
                }
                this.manager.release(selector);
            }
            if (resolver != null) {
                if (source != null) {
                    resolver.release(source);
                }
                this.manager.release(resolver);
            }
        }
        return getKey(document.getPublication(), document.getArea(), document.getId(), document
                .getLanguage());
    }

    /**
     * @see org.apache.lenya.transaction.IdentifiableFactory#build(org.apache.lenya.transaction.IdentityMap,
     *      java.lang.String)
     */
    public Identifiable build(IdentityMap map, String key) throws Exception {

        if (getLogger().isDebugEnabled())
            getLogger().debug("DocumentFactory::build() called with key [" + key + "]");

        String[] snippets = key.split(":");
        String publicationId = snippets[0];
        String area = snippets[1];
        String documentId = snippets[2];
        String language = snippets[3];

        ServiceSelector selector = null;
        DocumentBuilder builder = null;
        SourceResolver resolver = null;
        Source source = null;
        Document document;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            source = resolver.resolveURI("context://");
            File servletContext = SourceUtil.getFile(source);

            PublicationFactory factory = PublicationFactory.getInstance(getLogger());
            Publication publication = factory.getPublication(publicationId, servletContext
                    .getAbsolutePath());

            selector = (ServiceSelector) this.manager.lookup(DocumentBuilder.ROLE + "Selector");
            builder = (DocumentBuilder) selector.select(publication.getDocumentBuilderHint());
            String webappUrl = builder.buildCanonicalUrl(publication, area, documentId, language);
            document = builder.buildDocument(this, publication, webappUrl);
        } finally {
            if (selector != null) {
                if (builder != null) {
                    selector.release(builder);
                }
                this.manager.release(selector);
            }
            if (resolver != null) {
                if (source != null) {
                    resolver.release(source);
                }
                this.manager.release(resolver);
            }
        }
        if (getLogger().isDebugEnabled())
            getLogger().debug("DocumentFactory::build() done.");

        return document;
    }

    /**    * @see org.apache.lenya.transaction.IdentifiableFactory#getType()
     */
    public String getType() {
        return Document.TRANSACTIONABLE_TYPE;
    }

}
