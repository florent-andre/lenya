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

package org.apache.lenya.cms.ac.usecases;

import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.lenya.ac.IPRange;
import org.apache.lenya.ac.file.FileIPRange;
import org.apache.lenya.ac.file.FileIPRangeManager;
import org.apache.lenya.ac.impl.AbstractItem;
import org.apache.lenya.cms.ac.usecases.IPRangeProfile.Part;

/**
 * Usecase to add an IP range.
 */
public class AddIPRange extends AccessControlUsecase {

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckExecutionConditions()
     */
    protected void doCheckExecutionConditions() throws Exception {
        String id = getParameterAsString(IPRangeProfile.ID);

        IPRange existingIPRange = getIpRangeManager().getIPRange(id);

        if (existingIPRange != null) {
            addErrorMessage("This IP range already exists.");
        }

        if (!AbstractItem.isValidId(id)) {
            addErrorMessage("This is not a valid IP range ID.");
        }

        IPRangeProfile.validateAddresses(this);
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        File configDir = ((FileIPRangeManager) getIpRangeManager()).getConfigurationDirectory();

        String id = getParameterAsString(IPRangeProfile.ID);
        String name = getParameterAsString(IPRangeProfile.NAME);
        String description = getParameterAsString(IPRangeProfile.DESCRIPTION);

        IPRange ipRange = new FileIPRange(configDir, id);
        ContainerUtil.enableLogging(ipRange, getLogger());

        ipRange.setName(name);
        ipRange.setDescription(description);

        String networkString = "";
        String subnetString = "";

        for (int i = 0; i < 4; i++) {
            if (i > 0) {
                networkString += ".";
                subnetString += ".";
            }
            Part netPart = (Part) getParameter(IPRangeProfile.NETWORK_ADDRESS + "-" + i);
            networkString += netPart.getValue();
            Part subPart = (Part) getParameter(IPRangeProfile.SUBNET_MASK + "-" + i);
            subnetString += subPart.getValue();
        }

        InetAddress networkAddress = InetAddress.getByName(networkString);
        ipRange.setNetworkAddress(networkAddress.getAddress());

        InetAddress subnetMask = InetAddress.getByName(subnetString);
        ipRange.setSubnetMask(subnetMask.getAddress());

        ipRange.save();
        getIpRangeManager().add(ipRange);
        
        Map parameters = new HashMap();
        parameters.put(IPRangeProfile.ID, id);
        setExitUsecase("admin.ipRange", parameters);
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();
        List partNumbers = new ArrayList();
        for (byte i = 0; i < 4; i++) {
            setParameter(IPRangeProfile.NETWORK_ADDRESS + "-" + i, new Part(i));
            setParameter(IPRangeProfile.SUBNET_MASK + "-" + i, new Part(i));
            partNumbers.add(new Integer(i));
        }
        setParameter(IPRangeProfile.PART_NUMBERS, partNumbers);
    }
}