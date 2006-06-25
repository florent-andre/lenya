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
package org.apache.lenya.cms.usecase.scheduling;

import java.util.Arrays;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.cocoon.components.cron.JobSchedulerEntry;
import org.apache.lenya.cms.usecase.DocumentUsecase;

/**
 * Usecase to manage scheduled jobs.
 * 
 * @version $Id:$
 */
public class ManageJobs extends DocumentUsecase {

    protected static final String JOBS = "jobs";

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();

        UsecaseScheduler scheduler = null;
        try {
            scheduler = (UsecaseScheduler) this.manager.lookup(UsecaseScheduler.ROLE);
            JobSchedulerEntry[] jobs = scheduler.getJobs();
            setParameter(JOBS, Arrays.asList(jobs));
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        } finally {
            if (scheduler != null) {
                this.manager.release(scheduler);
            }
        }
    }
}