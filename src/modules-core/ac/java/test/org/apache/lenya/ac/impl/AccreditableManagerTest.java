/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.apache.lenya.ac.impl;

import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.AccreditableManager;

/**
 * Test class for the Accreditable Manager
 */
public class AccreditableManagerTest extends AccessControllerTest {

    private AccreditableManager accreditableManager;
    
    protected static final String HINT = "file";

    /**
     * The JUnit setup method. Lookup the resolver role.
     *
     * @exception  Exception  Description of Exception
     * @since
     */
    public void setUp() throws Exception {
        super.setUp();

        this.accreditableManager = getAccessController().getAccreditableManager();
        
        assertNotNull("AccreditableManager is null", this.accreditableManager);
    }

    /**
     * The test.
     * @throws AccessControlException when something went wrong.
     */
    public void testAccreditableManager() throws AccessControlException {
        assertNotNull(this.accreditableManager.getUserManager());
        assertNotNull(this.accreditableManager.getGroupManager());
        assertNotNull(this.accreditableManager.getRoleManager());
    }

}