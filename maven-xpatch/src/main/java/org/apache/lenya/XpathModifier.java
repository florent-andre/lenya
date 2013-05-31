package org.apache.lenya;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * This class modify matching part of xpath
 * This is usefull for building the cocoon test configuration that have a different structure than the main cocoon.xconf
 * @author florent
 *
 */
public class XpathModifier {
	
	private Map<String,String> modifiersList = new HashMap<String, String>();
	
	public void addModifier(String source,String target){
		modifiersList.put(source,target);
	}
	
	/**
	 * This apply only the first modification found
	 * And apply a String.remplace function
	 * If no one match, return the originalXpath
	 * @param originalXpath
	 */
	public String process(String originalXpath){
		for(Entry<String,String> entry : modifiersList.entrySet()){
			if(originalXpath.contains(entry.getKey())){
				return originalXpath.replace(entry.getKey(), entry.getValue());
			}
		}
		return originalXpath;
	}
}
