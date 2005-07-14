/*
 * Copyright  1999-2005 The Apache Software Foundation
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
package org.apache.lenya.transaction;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.lenya.ac.Identity;

/**
 * Default implementation of a unit of work.
 * 
 * @version $Id$
 */
public class UnitOfWorkImpl extends AbstractLogEnabled implements UnitOfWork {

    /**
     * Ctor.
     */
    public UnitOfWorkImpl() {
    }

    private IdentityMap identityMap;

    /**
     * @see org.apache.lenya.transaction.UnitOfWork#getIdentityMap()
     */
    public IdentityMap getIdentityMap() {
        if (this.identityMap == null) {
            this.identityMap = new IdentityMapImpl(getLogger());
            this.identityMap.setUnitOfWork(this);
        }
        return this.identityMap;
    }

    private Set newObjects = new HashSet();
    private Set modifiedObjects = new HashSet();
    private Set removedObjects = new HashSet();

    /**
     * @see org.apache.lenya.transaction.UnitOfWork#registerNew(org.apache.lenya.transaction.Transactionable)
     */
    public void registerNew(Transactionable object) throws TransactionException {
        if (!object.isLocked()) {
            throw new LockException("Object [" + object
                    + "] cannot be registered, it is not locked.");
        }
        this.newObjects.add(object);
    }

    /**
     * @throws TransactionException
     * @throws LockException
     * @see org.apache.lenya.transaction.UnitOfWork#registerDirty(org.apache.lenya.transaction.Transactionable)
     */
    public void registerDirty(Transactionable object) throws TransactionException {
        if (!object.isLocked()) {
            throw new LockException("Object [" + object
                    + "] cannot be registered, it is not locked.");
        }
        this.modifiedObjects.add(object);
    }

    /**
     * @see org.apache.lenya.transaction.UnitOfWork#registerRemoved(org.apache.lenya.transaction.Transactionable)
     */
    public void registerRemoved(Transactionable object) throws TransactionException {
        if (!object.isLocked()) {
            throw new LockException("Object [" + object
                    + "] cannot be registered, it is not locked.");
        }
        this.removedObjects.add(object);
    }

    /**
     * @see org.apache.lenya.transaction.UnitOfWork#commit()
     */
    public synchronized void commit() throws TransactionException {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("UnitOfWorkImpl::commit() called");
        }

        Set involvedObjects = new HashSet();
        involvedObjects.addAll(this.newObjects);
        involvedObjects.addAll(this.modifiedObjects);
        involvedObjects.addAll(this.removedObjects);
        
        for (Iterator i = involvedObjects.iterator(); i.hasNext();) {
            Transactionable t = (Transactionable) i.next();
            if (t.hasChanged()) {
                throw new LockException("Cannot commit transaction: The object [" + t
                        + "] was modified after it has been locked.");
            }
            t.checkout();
        }

        for (Iterator i = this.newObjects.iterator(); i.hasNext();) {
            Transactionable t = (Transactionable) i.next();
            t.createTransactionable();
            t.saveTransactionable();
        }
        for (Iterator i = this.modifiedObjects.iterator(); i.hasNext();) {
            Transactionable t = (Transactionable) i.next();
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("UnitOfWorkImpl::commit() calling save on [" + t + "]");
            }
            t.saveTransactionable();
        }
        for (Iterator i = this.removedObjects.iterator(); i.hasNext();) {
            Transactionable t = (Transactionable) i.next();
            t.deleteTransactionable();
        }

        if (getIdentityMap() != null) {
            Identifiable[] objects = getIdentityMap().getObjects();
            for (int i = 0; i < objects.length; i++) {
                if (objects[i] instanceof Transactionable) {
                    Transactionable t = (Transactionable) objects[i];
                    if (t.isCheckedOut()) {
                        t.checkin();
                    }
                    if (t.isLocked()) {
                        t.unlock();
                    }
                }
            }
        }
    }

    private Identity identity;

    /**
     * @see org.apache.lenya.transaction.UnitOfWork#getIdentity()
     */
    public Identity getIdentity() {
        return this.identity;
    }

    /**
     * @see org.apache.lenya.transaction.UnitOfWork#setIdentity(org.apache.lenya.ac.Identity)
     */
    public void setIdentity(Identity identity) {
        this.identity = identity;
    }

    /**
     * @see org.apache.lenya.transaction.UnitOfWork#isDirty(org.apache.lenya.transaction.Transactionable)
     */
    public boolean isDirty(Transactionable transactionable) {
        return this.modifiedObjects.contains(transactionable);
    }

    /**
     * @see org.apache.lenya.transaction.UnitOfWork#rollback()
     */
    public synchronized void rollback() throws TransactionException {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("UnitOfWorkImpl::rollback() called");
        }
        if (getIdentityMap() != null) {
            Identifiable[] objects = getIdentityMap().getObjects();
            for (int i = 0; i < objects.length; i++) {
                if (objects[i] instanceof Transactionable) {
                    Transactionable t = (Transactionable) objects[i];
                    if (t.isCheckedOut()) {
                        t.checkin();
                    }
                    if (t.isLocked()) {
                        t.unlock();
                    }
                }
            }
        }
    }

}
