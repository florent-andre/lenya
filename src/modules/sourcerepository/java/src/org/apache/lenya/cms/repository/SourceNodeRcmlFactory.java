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
package org.apache.lenya.cms.repository;

import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.lenya.cms.rc.RCML;

/**
 * Source node RCML factory interface.
 * Implementations must be thread safe.
 */
public interface SourceNodeRcmlFactory extends ThreadSafe {

    /**
     * Role org.apache.lenya.cms.repository.SourceNodeRcmlFactory
     */
    String ROLE = SourceNodeRcmlFactory.class.getName();

    /**
     * Get RCML object.
     * @param node Source node.
     * @return An RCML object.
     */
    RCML getRcml(SourceNode node);
}
