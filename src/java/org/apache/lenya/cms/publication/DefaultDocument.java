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

package org.apache.lenya.cms.publication;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.lenya.cms.metadata.dublincore.DublinCore;
import org.apache.lenya.cms.metadata.dublincore.DublinCoreProxy;
import org.apache.lenya.cms.site.Label;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteManager;

/**
 * A typical CMS document.
 */
public class DefaultDocument implements Document {

    private String id;
    private DublinCore dublincore;
    private DocumentIdentityMap identityMap;

    /**
     * Creates a new instance of DefaultDocument.
     * @param map The identity map the document belongs to.
     * @param id The document ID (starting with a slash).
     * @deprecated Use {@link DefaultDocumentBuilder}instead.
     */
    public DefaultDocument(DocumentIdentityMap map, String id) {

        if (id == null) {
            throw new IllegalArgumentException("The document ID must not be null!");
        }
        if (!id.startsWith("/")) {
            throw new IllegalArgumentException("The document ID must start with a slash!");
        }
        this.id = id;

        this.identityMap = map;
        this.dublincore = new DublinCoreProxy(this);
    }

    /**
     * Creates a new instance of DefaultDocument. The language of the document is the default
     * language of the publication.
     * @param map The identity map the document belongs to.
     * @param id The document ID (starting with a slash).
     * @param area The area.
     */
    protected DefaultDocument(DocumentIdentityMap map, String id, String area) {
        if (id == null) {
            throw new IllegalArgumentException("The document ID must not be null!");
        }
        if (!id.startsWith("/")) {
            throw new IllegalArgumentException("The document ID must start with a slash!");
        }
        this.id = id;

        this.identityMap = map;

        setArea(area);
        setLanguage(identityMap.getPublication().getDefaultLanguage());

        this.dublincore = new DublinCoreProxy(this);

    }

    /**
     * Creates a new instance of DefaultDocument.
     * 
     * @param map The identity map the document belongs to.
     * @param id The document ID (starting with a slash).
     * @param area The area.
     * @param language the language
     */
    protected DefaultDocument(DocumentIdentityMap map, String id, String area, String language) {
        if (id == null) {
            throw new IllegalArgumentException("The document ID must not be null!");
        }
        if (!id.startsWith("/")) {
            throw new IllegalArgumentException("The document ID must start with a slash!");
        }
        this.id = id;

        this.identityMap = map;
        this.language = language;
        setArea(area);

        this.dublincore = new DublinCoreProxy(this);

    }

    /**
     * @see org.apache.lenya.cms.publication.Document#getId()
     */
    public String getId() {
        return id;
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#getName()
     */
    public String getName() {
        String[] ids = id.split("/");
        String nodeId = ids[ids.length - 1];

        return nodeId;
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#getNodeId()
     * @deprecated replaced by getName()
     */
    public String getNodeId() {
        return getName();
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#getPublication()
     */
    public Publication getPublication() {
        return getIdentityMap().getPublication();
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#getLastModified()
     */
    public Date getLastModified() {
        return new Date(getFile().lastModified());
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#getDublinCore()
     */
    public DublinCore getDublinCore() {
        return dublincore;
    }

    /**
     * Returns the file for this document.
     * @return A file object.
     */
    public File getFile() {
        return getPublication().getPathMapper().getFile(getPublication(), getArea(), getId(),
                getLanguage());
    }

    private String language = "";

    /**
     * @see org.apache.lenya.cms.publication.Document#getLanguage()
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#getLanguages()
     */
    public String[] getLanguages() throws DocumentException {

        List documentLanguages = new ArrayList();
        String[] allLanguages = getPublication().getLanguages();

        for (int i = 0; i < allLanguages.length; i++) {
            Document version;
            try {
                version = getIdentityMap().getFactory().getLanguageVersion(this, allLanguages[i]);
            } catch (DocumentBuildException e) {
                throw new DocumentException(e);
            }
            if (version.exists()) {
                documentLanguages.add(allLanguages[i]);
            }
        }

        return (String[]) documentLanguages.toArray(new String[documentLanguages.size()]);
    }

    /**
     * Sets the language of this document.
     * @param language The language.
     */
    public void setLanguage(String language) {
        assert language != null;
        this.language = language;
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#getLabel()
     */
    public String getLabel() throws DocumentException {
        String labelString = "";
        try {
            SiteManager siteManager = getPublication().getSiteManager(getIdentityMap());
            if (siteManager != null) {
                Label label = siteManager.getLabel(this);
                labelString = label.getLabel();
            }
        } catch (SiteException e) {
            throw new DocumentException(e);
        }
        return labelString;
    }

    private String area;

    /**
     * @see org.apache.lenya.cms.publication.Document#getArea()
     */
    public String getArea() {
        return area;
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#getCompleteURL()
     */
    public String getCompleteURL() {
        return "/" + getPublication().getId() + "/" + getArea() + getDocumentURL();
    }

    /**
     * @see Document#getCompleteInfoURL()
     */
    public String getCompleteInfoURL() {
        return "/" + getPublication().getId() + "/" + Publication.INFO_AREA_PREFIX + getArea()
                + getDocumentURL();
    }

    /**
     * @see Document#getCompleteURLWithoutLanguage()
     */
    public String getCompleteURLWithoutLanguage() {
        String extensionSuffix = "".equals(getExtension()) ? "" : ("." + getExtension());

        return "/" + getPublication().getId() + "/" + getArea() + getId() + extensionSuffix;
    }

    /**
     * Sets the area.
     * @param area A string.
     */
    protected void setArea(String area) {
        if (!AbstractPublication.isValidArea(area)) {
            throw new IllegalArgumentException("The area [" + area + "] is not valid!");
        }
        this.area = area;
    }

    private String extension = "html";

    /**
     * @see org.apache.lenya.cms.publication.Document#getExtension()
     */
    public String getExtension() {
        return extension;
    }

    /**
     * Sets the extension of the file in the URL.
     * @param extension A string.
     */
    protected void setExtension(String extension) {
        assert extension != null;
        this.extension = extension;
    }

    private String documentURL;

    /**
     * Sets the document URL.
     * @param url The document URL (without publication ID and area).
     */
    public void setDocumentURL(String url) {
        assert url != null;
        this.documentURL = url;
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#getDocumentURL()
     */
    public String getDocumentURL() {
        return documentURL;
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#exists()
     */
    public boolean exists() throws DocumentException {
        boolean exists;
        try {
            SiteManager manager = getPublication().getSiteManager(getIdentityMap());
            if (manager != null) {
                exists = manager.contains(this);
            } else {
                exists = getFile().exists();
            }
        } catch (SiteException e) {
            throw new DocumentException(e);
        }
        return exists;
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#existsInAnyLanguage()
     */
    public boolean existsInAnyLanguage() throws DocumentException {
        boolean exists;
        try {
            SiteManager manager = getPublication().getSiteManager(getIdentityMap());
            if (manager != null) {
                exists = manager.containsInAnyLanguage(this);
            } else {
                exists = getFile().exists();
            }
        } catch (SiteException e) {
            throw new DocumentException(e);
        }
        return exists;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object object) {
        boolean equals = false;
        if (getClass().isInstance(object)) {
            Document document = (Document) object;
            equals = getPublication().equals(document.getPublication())
                    && getId().equals(document.getId()) && getArea().equals(document.getArea())
                    && getLanguage().equals(document.getLanguage());
        }
        return equals;

    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {

        String key = getPublication().getId() + ":" + getPublication().getServletContext() + ":"
                + getArea() + ":" + getId() + ":" + getLanguage();

        return key.hashCode();
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getPublication().getId() + ":" + getArea() + ":" + getId() + ":" + getLanguage();
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#getIdentityMap()
     */
    public DocumentIdentityMap getIdentityMap() {
        return this.identityMap;
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#getCanonicalWebappURL()
     */
    public String getCanonicalWebappURL() {
        return getCompleteURL();
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#getCanonicalDocumentURL()
     */
    public String getCanonicalDocumentURL() {
        return getDocumentURL();
    }

}