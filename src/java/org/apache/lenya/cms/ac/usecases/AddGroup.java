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
package org.apache.lenya.cms.ac.usecases;

import java.io.File;

import org.apache.lenya.ac.Group;
import org.apache.lenya.ac.file.FileGroup;
import org.apache.lenya.ac.file.FileGroupManager;
import org.apache.lenya.ac.impl.AbstractItem;
import org.apache.lenya.cms.usecase.UsecaseException;

/**
 * Usecase to add a group.
 *
 * @version $Id:$ 
 */
public class AddGroup extends AccessControlUsecase {

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckExecutionConditions()
     */
    protected void doCheckExecutionConditions() throws Exception {
        validate();
    }

    /**
     * Validates the request parameters.
     * @throws UsecaseException if an error occurs.
     */
    void validate() throws UsecaseException {

        String groupId = getParameterAsString(GroupProfile.ID);

        Group existingGroup = getGroupManager().getGroup(groupId);

        if (existingGroup != null) {
            addErrorMessage("This group already exists.");
        }

        if (!AbstractItem.isValidId(groupId)) {
            addErrorMessage("This is not a valid group ID.");
        }

    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();

        String id = getParameterAsString(GroupProfile.ID);
        String name = getParameterAsString(GroupProfile.NAME);
        String description = getParameterAsString(GroupProfile.DESCRIPTION);

        File configDir = ((FileGroupManager) getGroupManager()).getConfigurationDirectory();
        Group group = new FileGroup(configDir, id);
        group.setName(name);
        group.setDescription(description);
        
        group.save();
        getGroupManager().add(group);
        
        setExitParameter(GroupProfile.ID, id);
    }

}