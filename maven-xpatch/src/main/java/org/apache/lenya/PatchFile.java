package org.apache.lenya;

import java.io.File;

public class PatchFile {
	
	private File originalJarFile;
	
	private File patchFile;
	
	private boolean applied;
	private int round;
	
	public PatchFile(File originalJarFile,File patchFile){
		this.originalJarFile = originalJarFile;
		this.patchFile = patchFile;
		
		this.applied = false;
		this.round =0;
	}

	public File getOriginalJarFile() {
		return originalJarFile;
	}

	public void setOriginalJarFile(File originalJarFile) {
		this.originalJarFile = originalJarFile;
	}

	public File getPatchFile() {
		return patchFile;
	}

	public void setPatchFile(File patchFile) {
		this.patchFile = patchFile;
	}

	public boolean isApplied() {
		return applied;
	}

	public void setApplied(boolean applied) {
		this.applied = applied;
	}

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

	
}
