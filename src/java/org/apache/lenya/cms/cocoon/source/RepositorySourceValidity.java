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
package org.apache.lenya.cms.cocoon.source;

import org.apache.excalibur.source.SourceValidity;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.repository.RepositoryException;

/**
 * Validity for repository sources.
 */
public class RepositorySourceValidity implements SourceValidity {

    private static final long serialVersionUID = 1L;
    
    private Node node;
    
    private long lastModified = -1;
    
    /**
     * @param source The source this validity is for.
     */
    public RepositorySourceValidity(RepositorySource source) {
        this.node = source.getNode();
        try {
            this.lastModified = this.node.getLastModified();
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    public int isValid() {
        try {
            if (getNode().getLastModified() == this.lastModified) {
                return SourceValidity.VALID;
            }
            else {
                return SourceValidity.INVALID;
            }
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }
    
    protected Node getNode() {
        return this.node;
    }

    public int isValid(SourceValidity validity) {
        if (validity instanceof RepositorySourceValidity) {
            RepositorySourceValidity repoValidity = (RepositorySourceValidity) validity;
            
            if (!repoValidity.getNode().getSourceURI().equals(getNode().getSourceURI())) {
                throw new RuntimeException("Wrong source URI!");
            }
            
            try {
                if (getNode().getLastModified() >= repoValidity.getNode().getLastModified()) {
                    return SourceValidity.VALID;
                }
                else {
                    return SourceValidity.INVALID;
                }
            } catch (RepositoryException e) {
                throw new RuntimeException(e);
            }
        } else {
            return SourceValidity.INVALID;
        }
    }

}
