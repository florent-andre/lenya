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

package org.apache.lenya.cms.cocoon.components.modules.input;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.cocoon.components.modules.input.AbstractInputModule;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.spring.configurator.WebAppContextUtils;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.excalibur.store.impl.MRUMemoryStore;
import org.apache.lenya.cms.cocoon.source.FallbackSourceFactory;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentFactoryBuilder;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.cms.repository.RepositoryManager;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.util.ServletHelper;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;

/**
 * <p>
 * This module returns the actual source URI of a fallback:// source. The protocol (fallback,
 * template-fallback, ...) is configurable via the <em>protocol</em> parameter.
 * </p>
 */
public class FallbackModule extends AbstractInputModule {

    private String protocol;
    protected MRUMemoryStore store;
    private RepositoryManager repositoryManager;
    private DocumentFactoryBuilder documentFactoryBuilder;
    private SourceResolver resolver;
    private static Boolean useCache = null;

    public void setRepositoryManager(RepositoryManager manager) {
        this.repositoryManager = manager;
    }

    public void setDocumentFactoryBuilder(DocumentFactoryBuilder builder) {
        this.documentFactoryBuilder = builder;
    }

    protected boolean useCache() {
        return this.store != null;
    }

    public void setStore(MRUMemoryStore store) {
        Assert.notNull(store, "store");
        this.store = store;
    }

    protected MRUMemoryStore getStore() {
        return this.store;
    }

    protected String getPublicationId(Map objectModel) {
        Request request = ObjectModelHelper.getRequest(objectModel);
        String webappUri = ServletHelper.getWebappURI(request);
        URLInformation info = new URLInformation(webappUri);
        String pubId = null;
        try {
            Session session = RepositoryUtil.getSession(this.repositoryManager, request);
            DocumentFactory factory = this.documentFactoryBuilder.createDocumentFactory(session);
            String pubIdCandidate = info.getPublicationId();
            if (pubIdCandidate != null && factory.existsPublication(pubIdCandidate)) {
                pubId = pubIdCandidate;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return pubId;
    }

    public Object getAttribute(String name, Configuration modeConf, Map objectModel)
            throws ConfigurationException {
        String uri;
        String fallbackUri = getFallbackUri(name);
        if (useCache()) {
            final String pubId = getPublicationId(objectModel);
            String cacheKey = FallbackSourceFactory.getCacheKey(pubId, fallbackUri);
            MRUMemoryStore store = getStore();
            if (store.containsKey(cacheKey)) {
                uri = (String) store.get(cacheKey);
            } else {
                uri = resolveSourceUri(name);
            }
        } else {
            uri = resolveSourceUri(name);
        }
        return uri;
    }

    protected String resolveSourceUri(String name) throws ConfigurationException {
        Source source = null;
        try {
            source = this.resolver.resolveURI(getFallbackUri(name));
            return source.getURI();
        } catch (Exception e) {
            throw new ConfigurationException("Resolving fallback source [" + name + "] failed: ", e);
        } finally {
            if (source != null) {
                this.resolver.release(source);
            }
        }
    }

    protected String getFallbackUri(String name) {
        return this.protocol + "://" + name;
    }

    public Iterator getAttributeNames(Configuration modeConf, Map objectModel)
            throws ConfigurationException {
        return Collections.EMPTY_SET.iterator();
    }

    public Object[] getAttributeValues(String name, Configuration modeConf, Map objectModel)
            throws ConfigurationException {
        Object[] objects = { getAttribute(name, modeConf, objectModel) };
        return objects;
    }

    public void setProtocol(String protocol) {
        Assert.notNull(protocol, "protocol");
        this.protocol = protocol;
    }
    
    public void setSourceResolver(SourceResolver resolver) {
        this.resolver = resolver;
    }

}