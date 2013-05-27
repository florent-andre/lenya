package org.apache.lenya.filter;

public class ResourcesFilter {
	
	String patchFolder;
	
	/**
	 * @param patchFolder : a relative folder from the jar or the resource folder
	 */
	public ResourcesFilter(String patchFolder){
		this.patchFolder = patchFolder;
	}
	
	
	public boolean isToKeep(String filePath){
		return filePath == null ? false : filePath.startsWith(patchFolder);
	}

}
