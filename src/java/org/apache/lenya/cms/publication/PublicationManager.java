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

/**
 * Publication manager.
 */
public interface PublicationManager {

    /**
     * The service's role.
     */
    String ROLE = PublicationManager.class.getName();
    
    /**
     * @param factory The factory.
     * @return All publications.
     * @throws PublicationException if an error occurrs.
     */
    Publication[] getPublications(DocumentFactory factory) throws PublicationException;
    
    /**
     * @param factory The factory.
     * @param id The publication ID.
     * @return A publication.
     * @throws PublicationException if an error occurs.
     */
    Publication getPublication(DocumentFactory factory, String id) throws PublicationException;
    
}