package org.apache.lenya.tests;

import java.io.File;

import org.apache.lenya.ac.impl.AbstractAccessControlTest;
import org.apache.lenya.cms.export.Importer;
import org.apache.lenya.cms.publication.Area;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.repository.Session;

public class AbstractTestWithPublicationTest extends AbstractAccessControlTest{

	public void initPublicationTestContent() throws Exception{
    	Session session = login("lenya");

        Publication pub = getPublication(session, "test");
        Area area = pub.getArea("authoring");

        if (area.getDocuments().length == 0) {
            Publication defaultPub = getPublication(session, "default");
            Area defaultArea = defaultPub.getArea("authoring");
            String pubPath = defaultArea.getPublication().getDirectory().getAbsolutePath();
            String path = pubPath.replace(File.separatorChar, '/') + "/example-content";
            Importer importer = new Importer(getManager(), getLogger());
            importer.importContent(defaultPub, area, path);
            
            session.commit();
        }
    }
}
