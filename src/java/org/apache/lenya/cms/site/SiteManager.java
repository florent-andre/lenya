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

package org.apache.lenya.cms.site;

import org.apache.lenya.cms.publication.Document;

/**
 * <p>A site structure management component.</p>
 * 
 * <p>Dependence on a set of resources must be a strict partial order <strong>&lt;</strong>:</p>
 * <ul>
 * <li><em>irreflexive:</em> d<strong>&lt;</strong>d does not hold for any resource d</li>
 * <li><em>antisymmetric:</em> d<strong>&lt;</strong>e and e<strong>&lt;</strong>d implies d=e</li>
 * <li><em>transitive:</em> d<strong>&lt;</strong>e and e<strong>&lt;</strong>f implies d<strong>&lt;</strong>f</li>
 * </ul>

 *  * @author <a href="andreas@apache.org">Andreas Hartmann</a>
 * @version $Id: SiteManager.java,v 1.1 2004/02/18 18:47:07 andreas Exp $
 */
public interface SiteManager {

    /**
     * Checks if a resource requires another one.
     * @param dependingResource The depending resource.
     * @param requiredResource The required resource.
     * @return A boolean value.
     * @throws SiteException if an error occurs.
     */
    boolean requires(Document dependingResource, Document requiredResource) throws SiteException;

    /**
     * Returns the resources which are required by a certain resource.
     * @param resource The depending resource.
     * @return An array of resources.
     * @throws SiteException if an error occurs.
     */
    Document[] getRequiredResources(Document resource) throws SiteException;
    
    /**
     * Returns the resources which require a certain resource.
     * @param resource The required resource.
     * @return An array of resources.
     * @throws SiteException if an error occurs.
     */
    Document[] getRequiringResources(Document resource) throws SiteException;
    
    /**
     * Checks if the site structure contains a certain resource in a certain area.
     * @param resource The resource.
     * @return A boolean value.
     * @throws SiteException if an error occurs.
     */
    boolean contains(Document resource) throws SiteException;

}
