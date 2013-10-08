package org.jboss.as.quickstarts.datagrid.carmart;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import java.io.File;


/**
 * Creates a deployment from a build Web Archive using ShrinkWrap ZipImporter
 * 
 * @author tsykora@redhat.com
 * 
 */
public class Deployments {
    //properties defined in pom.xml
    private static final String ARCHIVE_NAME_TX = System.getProperty("carmart-tx.war.file");
    private static final String BUILD_DIRECTORY_TX = System.getProperty("carmart-tx.war.directory");

    public static WebArchive createDeploymentTx() {
        return ShrinkWrap.create(ZipImporter.class, ARCHIVE_NAME_TX).importFrom(new File(BUILD_DIRECTORY_TX + '/' + ARCHIVE_NAME_TX))
                .as(WebArchive.class);
    }
}
