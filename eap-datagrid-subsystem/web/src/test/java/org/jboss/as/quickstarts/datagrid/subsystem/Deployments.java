package org.jboss.as.quickstarts.datagrid.subsystem;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import java.io.File;

public class Deployments {
    private static final String ARCHIVE_NAME = System.getProperty("subsystem.war.file");
    private static final String BUILD_DIRECTORY = System.getProperty("subsystem.war.directory");

    public static WebArchive createDeployment() {
        System.out.println(BUILD_DIRECTORY + '/' + ARCHIVE_NAME);
        return ShrinkWrap.create(ZipImporter.class, ARCHIVE_NAME).importFrom(new File(BUILD_DIRECTORY + '/' + ARCHIVE_NAME))
                .as(WebArchive.class);
    }

}
