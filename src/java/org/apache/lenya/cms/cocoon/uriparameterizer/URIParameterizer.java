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

/* $Id: URIParameterizer.java 473841 2006-11-12 00:46:38Z gregor $  */

package org.apache.lenya.cms.cocoon.uriparameterizer;

import java.util.Map;

import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.parameters.Parameters;

public interface URIParameterizer extends Component {
    
    String ROLE = URIParameterizer.class.getName();
    
    /**
     * Receives the URI parameters for a source.
     * @param uri The URI.
     * @param src The source.
     * @param parameters The parameters.
     * @return The URI parameters.
     * @throws URIParameterizerException when something went wrong.
     */
    Map parameterize(String uri, String src, Parameters parameters)
        throws URIParameterizerException;
}