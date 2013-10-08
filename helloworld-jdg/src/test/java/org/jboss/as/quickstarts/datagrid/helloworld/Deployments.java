package org.jboss.as.quickstarts.datagrid.helloworld;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import java.io.File;


/**
 * Creates a deployment from a build Web Archive using ShrinkWrap ZipImporter
 * 
 * @author jmarkos@redhat.com
 * 
 */
public class Deployments {
    //properties defined in pom.xml
    private static final String ARCHIVE_NAME = System.getProperty("helloworld-jdg.war.file");
    private static final String BUILD_DIRECTORY = System.getProperty("helloworld-jdg.war.directory");

    public static WebArchive createDeployment() {
        return ShrinkWrap.create(ZipImporter.class, ARCHIVE_NAME).importFrom(new File(BUILD_DIRECTORY + '/' + ARCHIVE_NAME))
                .as(WebArchive.class);
    }
}
