/*
 * DocumentTypeBuilder.java
 *
 * Created on 9. April 2003, 10:11
 */

package org.apache.lenya.cms.publication;

import java.io.File;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.lenya.cms.authoring.ParentChildCreatorInterface;

/**
 * A builder for document types.
 *
 * @author <a href="mailto:andreas.hartmann@wyona.org">Andreas Hartmann</a>
 */
public final class DocumentTypeBuilder {

    /** Creates a new instance of DocumentTypeBuilder */
    private DocumentTypeBuilder() {
    }

    /**
     * The default document types configuration directory, relative to the publication directory.
     */
    public static final String DOCTYPE_DIRECTORY =
        "config/doctypes".replace('/', File.separatorChar);
    /*
     * The default document types configuration file, relative to the publication directory.
     */
    public static final String CONFIG_FILE = "doctypes.xconf".replace('/', File.separatorChar);

    public static final String DOCTYPES_ELEMENT = "doctypes";
    public static final String DOCTYPE_ELEMENT = "doc";
    public static final String TYPE_ATTRIBUTE = "type";
    public static final String CREATOR_ELEMENT = "creator";
    public static final String SRC_ATTRIBUTE = "src";
    public static final String WORKFLOW_ELEMENT = "workflow";

    /**
     * Builds a document type for a given name.
     * 
     * @param name A string value.
     * @param publication The publication the document type belongs to.
     * @return A document type object.
     * @throws DocumentTypeBuildException When something went wrong.
     */
    public static DocumentType buildDocumentType(String name, Publication publication)
        throws DocumentTypeBuildException {

        DocumentType type = new DocumentType(name);
            
        File configDirectory = new File(publication.getDirectory(), DOCTYPE_DIRECTORY);
        File configFile = new File(configDirectory, CONFIG_FILE);

        try {
            
            Configuration configuration = new DefaultConfigurationBuilder().buildFromFile(configFile);
            
            Configuration doctypeConfigurations[] = configuration.getChildren(DOCTYPE_ELEMENT);
            Configuration doctypeConf = null;
            for (int i = 0; i < doctypeConfigurations.length; i++) {
                if (doctypeConfigurations[i].getAttribute(TYPE_ATTRIBUTE).equals(name)) {
                    doctypeConf = doctypeConfigurations[i];
                }
            }
            if (doctypeConf == null) {
                throw new DocumentTypeBuildException("No definition found for doctype '" + name + "'!");
            }
            
            ParentChildCreatorInterface creator;
            Configuration creatorConf = doctypeConf.getChild(CREATOR_ELEMENT, false);
            if (creatorConf != null) {
                String creatorClassName = creatorConf.getAttribute(SRC_ATTRIBUTE);
                Class creatorClass = Class.forName(creatorClassName);
                creator = (ParentChildCreatorInterface) creatorClass.newInstance();
                creator.init(creatorConf);
            }
            else {
                creator = new org.apache.lenya.cms.authoring.DefaultBranchCreator();
            }
            type.setCreator(creator);
            
            Configuration workflowConf = doctypeConf.getChild(WORKFLOW_ELEMENT, false);
            if (workflowConf != null) {
                String workflowFileName = workflowConf.getAttribute(SRC_ATTRIBUTE);
                type.setWorkflowFileName(workflowFileName);
            }

        } catch (Exception e) {
            throw new DocumentTypeBuildException(e);
        }

        return type;
    }

}
