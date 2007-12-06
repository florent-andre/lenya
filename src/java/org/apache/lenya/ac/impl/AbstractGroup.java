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

/* $Id: AbstractGroup.java 473841 2006-11-12 00:46:38Z gregor $  */

package org.apache.lenya.ac.impl;

import java.util.HashSet;
import java.util.Set;

import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Accreditable;
import org.apache.lenya.ac.AttributeRuleEvaluator;
import org.apache.lenya.ac.Group;
import org.apache.lenya.ac.Groupable;
import org.apache.lenya.ac.Message;
import org.apache.lenya.ac.User;

/**
 * A group is a set of {@link Groupable}s.
 */
public abstract class AbstractGroup extends AbstractItem implements Accreditable, Group {
    /**
     * Creates a new group.
     */
    public AbstractGroup() {
    }

    /**
     * Creates a new group.
     * @param id The group ID.
     */
    public AbstractGroup(String id) {
        setId(id);
    }

    private Set members = new HashSet();

    /**
     * Returns the members of this group.
     * @return An array of {@link Groupable}s.
     */
    public Groupable[] getMembers() {
        Set members = members();
        return (Groupable[]) members.toArray(new Groupable[members.size()]);
    }
    
    private boolean initializing = false;
    
    protected Set members() {
        // First we must make sure that the user and IP range managers
        // are initialized because otherwise the group won't contain their members
        if (!initializing) {
            // avoid race condition
            initializing = true;
            try {
                getItemManager().getAccreditableManager().getUserManager();
                getItemManager().getAccreditableManager().getIPRangeManager();
            } catch (AccessControlException e) {
                throw new RuntimeException(e);
            }
            initializing = false;
        }
        return this.members;
    }

    /**
     * Adds a member to this group.
     * @param member The member to add.
     */
    public void add(Groupable member) {
        Set members = members();
        assert (member != null) && !members.contains(member);
        members.add(member);
        member.addedToGroup(this);
    }

    /**
     * Removes a member from this group.
     * @param member The member to remove.
     */
    public void remove(Groupable member) {
        Set members = members();
        assert (member != null) && members.contains(member);
        members.remove(member);
        member.removedFromGroup(this);
    }

    /**
     * Removes all members from this group.
     */
    public void removeAllMembers() {
        Groupable[] members = getMembers();
        for (int i = 0; i < members.length; i++) {
            remove(members[i]);
        }
    }

    /**
     * Returns if this group contains this member.
     * @param member The member to check.
     * @return A boolean value.
     */
    public boolean contains(Groupable member) {
        boolean contains = members().contains(member);

        if (!contains && member instanceof User && getRule() != null) {
            User user = (User) member;
            AttributeRuleEvaluator evaluator = getAttributeRuleEvaluator();
            contains = evaluator.isComplied(user, getRule());
        }
        return contains;
    }

    protected AttributeRuleEvaluator getAttributeRuleEvaluator() {
        return getItemManager().getAttributeRuleEvaluator();
    }

    /**
     * @see org.apache.lenya.ac.Accreditable#getAccreditables()
     */
    public Accreditable[] getAccreditables() {
        Accreditable[] accreditables = { this };
        return accreditables;
    }

    /**
     * Delete a group
     * 
     * @throws AccessControlException if the delete failed
     */
    public void delete() throws AccessControlException {
        Groupable[] members = getMembers();
        for (int i = 0; i < members.length; i++) {
            remove(members[i]);
        }
    }

    private String rule;

    public void setRule(String rule) throws AccessControlException {
        if (rule != null) {
            AttributeRuleEvaluator evaluator = getAttributeRuleEvaluator();
            ValidationResult result = evaluator.validate(rule);
            if (!result.succeeded()) {
                StringBuffer msg = new StringBuffer();
                Message[] messages = result.getMessages();
                for (int i = 0; i < messages.length; i++) {
                    if (i > 0) {
                        msg.append("; ");
                    }
                    msg.append(messages[i].getText());
                }
                throw new AccessControlException("The rule for group [" + getId() + "] is not valid: "
                        + msg.toString());
            }
        }
        this.rule = rule;
    }

    public String getRule() {
        return this.rule;
    }

}
