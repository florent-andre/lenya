/*
* Copyright 1999-2004 The Apache Software Foundation
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

//
// Modify a group.
//
function groupChangeProfile() {

	resolve();
	try {
	
	    var redirectUri = getRequestUri();
        var groupId = getAccreditableId();
        
	    var groupManager = getAccreditableManager().getGroupManager();
	    var group = groupManager.getGroup(groupId);
		var name = group.getName();
		var description = group.getDescription();
		
		// at the moment the loop is executed only once (no form validation)
		
	    while (true) {
		    cocoon.sendPageAndWait("groups/profile.xml", {
		    	"group-id" : groupId,
		    	"name" : name,
		    	"description" : description,
		    	"page-title" : "Edit Group"
		    });
		    
		    if (cocoon.request.get("cancel")) {
		    	break;
		    }
		    
		    if (cocoon.request.get("submit")) {
			    name = cocoon.request.get("name");
		       	group.setName(name);
		       	description = cocoon.request.get("description");
		       	group.setDescription(description);
		   		group.save();
		    	break;
		    }
	
	    }
	    
	   	cocoon.redirectTo(redirectUri);
   	}
   	finally {
	   	release();
   	}
}


//
// Change the members of a group.
//
function groupChangeMembers() {

	resolve();
	try {
	
	    var redirectUri = getRequestUri();
        var groupId = getAccreditableId();
        
	    var userManager = getAccreditableManager().getUserManager();
	    var groupManager = getAccreditableManager().getGroupManager();
	    var group = groupManager.getGroup(groupId);
	    
	    var memberArray = group.getMembers();
	    
	    var groupUsers = new java.util.ArrayList();
	    var otherUsers = new java.util.ArrayList();
	    var groupMachines = new java.util.ArrayList();
	    var otherMachines = new java.util.ArrayList();
	    
	    for (var i = 0; i < memberArray.length; i++) {
	    	var member = memberArray[i];
	    	if (member instanceof Packages.org.apache.lenya.ac.User) {
	    		groupUsers.add(member);
	    	}
	    	if (member instanceof Packages.org.apache.lenya.ac.Machine) {
	    		groupMachines.add(machine);
	    	}
	    }
	    
	    var users = userManager.getUsers();
	    for (var i = 0; i < users.length; i++) {
	    	if (!groupUsers.contains(users[i])) {
	    		otherUsers.add(users[i]);
	    	}
	    }
	    
	    while (true) {
		    cocoon.sendPageAndWait("groups/members.xml", {
		    	"group-id" : groupId,
		    	"users" : otherUsers,
		    	"group-users" : groupUsers,
		    	"machines" : otherMachines,
		    	"group-machines" : groupMachines,
		    });
		    
			var otherUserId = cocoon.request.get("user");
			if (cocoon.request.get("add_user") && otherUserId != null) {
				var user = userManager.getUser(otherUserId);
				if (!groupUsers.contains(user)) {
					groupUsers.add(user);
					otherUsers.remove(user);
				}
			}
		    
			var groupUserId = cocoon.request.get("group_user");
			if (cocoon.request.get("remove_user") && groupUserId != null) {
				var user = userManager.getUser(groupUserId);
				if (groupUsers.contains(user)) {
					groupUsers.remove(user);
					otherUsers.add(user);
				}
			}
	
		    if (cocoon.request.get("cancel")) {
		    	break;
		    }
		    
			if (cocoon.request.get("submit")) {
				group.removeAllMembers();
				
				var userIterator = groupUsers.iterator();
				while (userIterator.hasNext()) {
					var user = userIterator.next();
					group.add(user);
					user.save();
				}
				
				break;
			}
		}
	   	cocoon.redirectTo(redirectUri);
   	}
   	finally {
	   	release();
   	}
}


