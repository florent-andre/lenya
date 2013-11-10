package org.apache.lenya;

import java.util.ArrayList;
import java.util.List;

public class PatchTree {
	
	List<PatchNode> rootNodes = new ArrayList<PatchNode>();

	public List<PatchNode> getRootNodes() {
		return rootNodes;
	}

	public void setRootNodes(List<PatchNode> rootNodes) {
		this.rootNodes = rootNodes;
	}
	
	

}
