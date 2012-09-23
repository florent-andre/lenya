package org.apache.lenya;

import org.apache.lenya.xpatch.Xpatch;

import junit.framework.TestCase;

public class XpatchTest extends TestCase {

	//@Test
	public void testpatch(){
		Xpatch xp = new Xpatch();
		
		
		
		xp.patch(topatch, patch);
	}
}
