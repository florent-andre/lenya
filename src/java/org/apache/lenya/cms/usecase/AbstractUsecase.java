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
package org.apache.lenya.cms.usecase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.components.cron.CronJob;
import org.apache.cocoon.components.cron.JobScheduler;
import org.apache.cocoon.servlet.multipart.Part;
import org.apache.lenya.cms.publication.DocumentManager;

/**
 * Abstract usecase implementation.
 * 
 * @version $Id$
 */
public class AbstractUsecase extends AbstractOperation implements Usecase, Contextualizable {

    /**
     * Ctor.
     */
    public AbstractUsecase() {
        // do nothing
    }

    /**
     * Override to initialize parameters.
     */
    protected void initParameters() {
        // do nothing
    }

    private String sourceUrl = null;

    /**
     * Returns the source URL.
     * @return A string.
     */
    protected String getSourceURL() {
        return this.sourceUrl;
    }

    /**
     * Returns the context.
     * @return A context.
     */
    protected Context getContext() {
        return this.context;
    }

    /**
     * Checks if the operation can be executed and returns the error messages.
     * Error messages prevent the operation from being executed.
     * @return A boolean value.
     */
    public List getErrorMessages() {
        return Collections.unmodifiableList(this.errorMessages);
    }

    /**
     * Returns the information messages to show on the confirmation screen.
     * @return An array of strings. Info messages do not prevent the operation
     *         from being executed.
     */
    public List getInfoMessages() {
        return Collections.unmodifiableList(this.infoMessages);
    }

    private List errorMessages = new ArrayList();
    private List infoMessages = new ArrayList();

    /**
     * Adds an error message.
     * @param message The message.
     */
    public void addErrorMessage(String message) {
        this.errorMessages.add(message);
    }

    /**
     * Adds an error message.
     * @param messages The messages.
     */
    public void addErrorMessages(String[] messages) {
        for (int i = 0; i < messages.length; i++) {
            addErrorMessage(messages[i]);
        }
    }

    /**
     * Adds an info message.
     * @param message The message.
     */
    public void addInfoMessage(String message) {
        this.infoMessages.add(message);
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#checkExecutionConditions()
     */
    public final void checkExecutionConditions() throws UsecaseException {
        try {
            clearErrorMessages();
            clearInfoMessages();
            doCheckExecutionConditions();
            dumpErrorMessages();
        } catch (Exception e) {
            getLogger().error(e.getMessage(), e);
            addErrorMessage(e.getMessage() + " - Please consult the logfiles.");
            if (getLogger().isDebugEnabled()) {
                throw new UsecaseException(e);
            }
        }
    }

    /**
     * Checks the execution conditions.
     * @throws Exception if an error occurs.
     */
    protected void doCheckExecutionConditions() throws Exception {
        // do nothing
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#checkPreconditions()
     */
    public final void checkPreconditions() throws UsecaseException {
        try {
            clearErrorMessages();
            clearInfoMessages();
            doCheckPreconditions();
            dumpErrorMessages();
        } catch (Exception e) {
            getLogger().error(e.getMessage(), e);
            addErrorMessage(e.getMessage() + " - Please consult the logfiles.");
            if (getLogger().isDebugEnabled()) {
                throw new UsecaseException(e);
            }
        }
    }

    /**
     * Checks the preconditions.
     * @throws Exception if an error occurs.
     */
    protected void doCheckPreconditions() throws Exception {
        // do nothing
    }

    /**
     * Clears the error messages.
     */
    protected void clearErrorMessages() {
        this.errorMessages.clear();
    }

    /**
     * Clears the info messages.
     */
    protected void clearInfoMessages() {
        this.infoMessages.clear();
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#execute()
     */
    public final void execute() throws UsecaseException {
        try {
            clearErrorMessages();
            clearInfoMessages();
            doExecute();
            dumpErrorMessages();
        } catch (Exception e) {
            getLogger().error(e.getMessage(), e);
            addErrorMessage(e.getMessage() + " - Please consult the logfiles.");
            if (getLogger().isDebugEnabled()) {
                throw new UsecaseException(e);
            }
        }
    }

    /**
     * Dumps the error messages to the log and the command line.
     */
    protected void dumpErrorMessages() {
        List _errorMessages = getErrorMessages();
        for (int i = 0; i < _errorMessages.size(); i++) {
            if (getLogger().isDebugEnabled()) {
                System.out.println("+++ ERROR +++ " + _errorMessages.get(i));
            }
            getLogger().error((String) _errorMessages.get(i));
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#checkPostconditions()
     */
    public void checkPostconditions() throws UsecaseException {
        try {
            clearErrorMessages();
            clearInfoMessages();
            doCheckPostconditions();
            dumpErrorMessages();
        } catch (Exception e) {
            getLogger().error(e.getMessage(), e);
            addErrorMessage(e.getMessage() + " - Please consult the logfiles.");
            if (getLogger().isDebugEnabled()) {
                throw new UsecaseException(e);
            }
        }
    }

    /**
     * Checks the post conditions.
     * @throws Exception if an error occured.
     */
    protected void doCheckPostconditions() throws Exception {
        // do nothing
    }

    /**
     * Executes the operation.
     * @throws Exception when something went wrong.
     */
    protected void doExecute() throws Exception {
        // do nothing
    }

    private Map parameters = new HashMap();

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#setParameter(java.lang.String,
     *      java.lang.Object)
     */
    public void setParameter(String name, Object value) {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Setting parameter [" + name + "] = [" + value + "]");
        }
        this.parameters.put(name, value);
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#getParameter(java.lang.String)
     */
    public Object getParameter(String name) {
        return this.parameters.get(name);
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#getParameterAsString(java.lang.String)
     */
    public String getParameterAsString(String name) {
        String valueString = null;
        Object value = getParameter(name);
        if (value != null) {
            valueString = value.toString();
        }
        return valueString;
    }

    /**
     * Return a map of all parameters
     * @return the map
     */
    public Map getParameters() {
        return Collections.unmodifiableMap(this.parameters);
    }

    /**
     * Returns one of the strings "true" or "false" depending on whether the
     * corresponding checkbox was checked.
     * @param name The parameter name.
     * @return A string.
     */
    public String getBooleanCheckboxParameter(String name) {
        String value = "false";
        if (getParameter(name) != null && getParameter(name).equals("on")) {
            value = "true";
        }
        return value;
    }

    private String targetUrl = null;

    protected void setTargetURL(String url) {
        this.targetUrl = url;
    }

    /**
     * If {@link #setTargetURL(String)}was not called, the source document (
     * {@link #getSourceURL()}) is returned.
     * @see org.apache.lenya.cms.usecase.Usecase#getTargetURL(boolean)
     */
    public String getTargetURL(boolean success) {
        String url;
        if (this.targetUrl != null) {
            url = this.targetUrl;
        } else {
            url = getSourceURL();
        }
        return url;
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#setPart(java.lang.String,
     *      org.apache.cocoon.servlet.multipart.Part)
     */
    public void setPart(String name, Part value) {
        if (!Part.class.isInstance(value)) {
            throw new RuntimeException("[" + value.getClass() + "] [" + value
                    + "] is not a part object. Maybe you have to enable uploads?");
        }
        setParameter(name, value);
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#getPart(java.lang.String)
     */
    public Part getPart(String name) {
        return (Part) getParameter(name);
    }

    private Context context;

    /**
     * @see org.apache.avalon.framework.context.Contextualizable#contextualize(org.apache.avalon.framework.context.Context)
     */
    public void contextualize(Context _context) throws ContextException {
        this.context = _context;
    }

    private DocumentManager documentManager;

    protected DocumentManager getDocumentManager() {
        return this.documentManager;
    }

    /**
     * @see org.apache.avalon.framework.activity.Initializable#initialize()
     */
    public final void initialize() throws Exception {
        super.initialize();
        this.documentManager = (DocumentManager) this.manager.lookup(DocumentManager.ROLE);
        doInitialize();
        initParameters();
    }

    /**
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose() {
        super.dispose();

        if (this.documentManager != null) {
            this.manager.release(documentManager);
        }
    }

    /**
     * Does the actual initialization. Template method.
     */
    protected void doInitialize() {
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#advance()
     */
    public void advance() {
        // do nothing
    }

    /**
     * Deletes a parameter.
     * @param name The parameter name.
     */
    protected void deleteParameter(String name) {
        this.parameters.remove(name);
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#isInteractive()
     */
    public boolean isInteractive() {
        return true;
    }
    
    protected void schedule(Date date) {
        
        JobScheduler scheduler = null;
        try {
            scheduler = (JobScheduler) this.manager.lookup(JobScheduler.ROLE);
            
            Parameters parameters = new Parameters();
            String[] names = getParameterNames();
            for (int i = 0; i < names.length; i++) {
                parameters.setParameter(names[i], getParameterAsString(names[i]));
            }
            
            Map objects = new HashMap();
            objects.put(UsecaseCronJob.USECASE_NAME, getName());
            objects.put(UsecaseCronJob.SOURCE_URL, getSourceURL());
            
            String role = CronJob.class.getName() + "/usecase";
            scheduler.fireJobAt(date, "foo", role, parameters, objects);
            
        } catch (Exception e) {
            getLogger().error("Could not create job: ", e);
            throw new RuntimeException(e);
        }
        finally {
            if (scheduler != null) {
                this.manager.release(scheduler);
            }
        }
    }
    
    private String name;

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#setName(java.lang.String)
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#getName()
     */
    public String getName() {
        return this.name;
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#getParameterNames()
     */
    public String[] getParameterNames() {
        Set keys = this.parameters.keySet();
        return (String[]) keys.toArray(new String[keys.size()]);
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#setSourceURL(java.lang.String)
     */
    public void setSourceURL(String url) {
        this.sourceUrl = url;
    }

}