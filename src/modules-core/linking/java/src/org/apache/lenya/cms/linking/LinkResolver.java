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
package org.apache.lenya.cms.linking;

import java.net.MalformedURLException;

import org.apache.lenya.cms.publication.Document;

/**
 * <p>
 * Resolve a link from a document to another document using it's
 * </p>
 * <ul>
 * <li>publication ID</li>
 * <li>area</li>
 * <li>UUID</li>
 * <li>language</li>
 * <li>revision number</li>
 * </ul>
 * <p>
 * All of these parameters are optional and default to the attributes of the
 * document which contains the link.
 * </p>
 * <p>
 * Syntax (square brackets denote optional parts):
 * </p>
 * <code>lenya-document:&lt;uuid&gt;[,lang=...][,area=...][,rev=...][,pub=...]</code>
 * <p>
 * The fallback mode determines the behaviour if the target language is omitted
 * and the target document doesn't exist in the language of the source document.
 * The default fallback mode is {@link #MODE_DEFAULT_LANGUAGE}.
 * <p>
 */
public interface LinkResolver {

    String ROLE = LinkResolver.class.getName();
    String SCHEME = "lenya-document";
    
    /**
     * Fail if the target document doesn't exist in the source language.
     */
    int MODE_FAIL = 0;
    
    /**
     * Try to fall back to the default language.
     */
    int MODE_DEFAULT_LANGUAGE = 1;

    void setFallbackMode(int mode);

    int getFallbackMode();

    /**
     * Resolve a link.
     * 
     * @param currentDocument The document which contains the link.
     * @param linkUri The link URI.
     * @return A document or <code>null</code> if the target document does not
     *         exist.
     * @throws MalformedURLException if the URI is invalid.
     */
    Document resolve(Document currentDocument, String linkUri) throws MalformedURLException;

}