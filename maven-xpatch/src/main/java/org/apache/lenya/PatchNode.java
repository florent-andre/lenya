package org.apache.lenya;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.Artifact;

public class PatchNode {
	
	Artifact artifact;
	
	List<PatchFile> patches;
	
	public PatchNode(Artifact artifact){
		this.artifact = artifact;
		patches = new ArrayList<PatchFile>();
	}

	public Artifact getArtifact() {
		return artifact;
	}

	public void setArtifact(Artifact artifact) {
		this.artifact = artifact;
	}

	public List<PatchFile> getPatches() {
		return patches;
	}

	public void setPatches(List<PatchFile> patches) {
		this.patches = patches;
	}
	
	

}
