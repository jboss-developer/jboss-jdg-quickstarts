package org.jboss.as.quickstarts.datagrid.helloworld;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.findby.FindByJQuery;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import java.net.MalformedURLException;
import java.net.URL;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.jboss.arquillian.graphene.Graphene.waitModel;


/**
 * Test for Helloworld-jdg quickstart using Drone Arquillian extension and Graphenebrowser framework for testing app UI.
 *
 * There are 2 tests, one for the put.jsf/get.jsf pages, where a user can add arbitrary entries, and one for
 * TestServletPut (adds entry <hello, world>) and TestServletGet (retrieves key <hello>) pages.
 *
 * @author jmarkos@redhat.com
 * @author jholusa@redhat.com
 */
@RunWith(Arquillian.class)
@RunAsClient
public class HelloworldTest {
    // Result is at the same position for both put/get pages - in the 2nd <table> (numbering from 0) in the 2nd <td>
    @FindByJQuery("table:eq(1) td:eq(1)")
    private WebElement result;

    // put page
    @FindBy(id = "putForm")
    private WebElement put_putForm;
    @FindBy(id = "putForm:key")
    private WebElement put_keyInput;
    @FindBy(id = "putForm:value")
    private WebElement put_valueInput;
    @FindBy(id = "putForm:Put")
    private WebElement put_putButton;
    @FindByJQuery("a:contains('Get Some')")
    private WebElement put_getSomeLink;

    // get page
    @FindBy(id = "getForm:key")
    private WebElement get_getInput;
    @FindBy(id = "getForm:Get")
    private WebElement get_getButton;
    @FindBy(id = "getForm:GetAll")
    private WebElement get_getAllButton;
    @FindByJQuery("a:contains('Put Some More')")
    private WebElement get_putSomeMoreLink;

    // TestServletPut and TestServletGet pages
    @FindByJQuery("h1")
    private WebElement servlets_mainText;

    @Drone
    WebDriver browser;
    
    @ArquillianResource
    @OperateOnDeployment("container1")
    private URL contextPath1;

    @ArquillianResource
    @OperateOnDeployment("container2")
    private URL contextPath2;

    @Deployment(name = "container1", testable = false)
    @TargetsContainer("container1")
    public static WebArchive createTestDeploymentRemote1() {
        return Deployments.createDeployment();
    }

    @Deployment(name = "container2", testable = false)
    @TargetsContainer("container2")
    public static WebArchive createTestDeploymentRemote2() {
        return Deployments.createDeployment();
    }

    // Test that pages are OK, cache empty at the beginning, proper replication and expiration after 60 seconds
    @Test
    public void testbasicOperations() {
        System.out.println("contextPath: " + contextPath1);
        System.out.println("contextPath2: " + contextPath2);

        testEmptyCache(contextPath1);
        testEmptyCache(contextPath2);

        putEntry(contextPath1, "key1", "value1");
        testEntryPresent(contextPath1, "key1", "value1");
        testEntryPresent(contextPath2, "key1", "value1");
        putEntry(contextPath2, "key2", "value2");
        testEntryPresent(contextPath1, "key2", "value2");

        // entries should expire after 60 seconds
        try {
            Thread.sleep(61000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        testEmptyCache(contextPath1);
        testEmptyCache(contextPath2);
    }

    @Test
    public void testPredefinedServlets() {
        URL getURL = null;
        URL putURL = null;
        try {
            putURL = new URL(contextPath1 + "TestServletPut");
            getURL = new URL(contextPath2 + "TestServletGet");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            fail("Are URLs '" + contextPath1 + "TestServletPut' and '" + contextPath2 + "TestServletGet' correct?");
        }
        browser.get(getURL.toExternalForm());
        waitModel().until().element(servlets_mainText).is().present();

        assertTrue("Put button is not present.", servlets_mainText.isDisplayed());
        String mainText = servlets_mainText.getText();
        assertTrue("TestServletGet page (container2) doesn't contain text 'Get Infinispan: null'", mainText.contains("Get Infinispan: null"));

        browser.get(putURL.toExternalForm());
        waitModel().until().element(servlets_mainText).is().present();

        assertTrue("Put button is not present.", servlets_mainText.isDisplayed());
        mainText = servlets_mainText.getText();
        assertTrue("TestServletPut page (container1) doesn't contain text 'Put Infinispan: world'", mainText.contains("Put Infinispan: world"));

        browser.get(getURL.toExternalForm());
        waitModel().until().element(servlets_mainText).is().present();

        assertTrue("Put button is not present.", servlets_mainText.isDisplayed());
        mainText = servlets_mainText.getText();
        assertTrue("TestServletGet page (container2) doesn't contain text 'Get Infinispan: world'", mainText.contains("Get Infinispan: world"));
    }


    private void putEntry(URL path, String key, String value) {
        browser.get(path.toExternalForm());
        waitModel().until().element(put_keyInput).is().present();
        waitModel().until().element(put_valueInput).is().present();
        waitModel().until().element(put_putButton).is().present();

        assertTrue("Input for key is not present.", put_keyInput.isDisplayed());
        assertTrue("Input for value is not present.", put_valueInput.isDisplayed());
        assertTrue("Put button is not present.", put_putButton.isDisplayed());

        put_keyInput.sendKeys(key);
        put_valueInput.sendKeys(value);

        put_putButton.click();

        waitModel().until().element(result).is().present();
        String cacheEntries = result.getText();
        System.out.println("cacheEntries = " + cacheEntries);
        assertTrue("Cache does not contain entry key1=value1", cacheEntries.contains(key + "=" + value + " added"));
    }

    // tests both get and getAll buttons
    // Note: It's possible to directly go to URL /Get.jsf, but that adds a dependency on the URL format, so we
    //   navigate there using the Get Some link
    private void testEntryPresent(URL path, String key, String value) {
        browser.get(path.toExternalForm());
        waitModel().until().element(put_getSomeLink).is().present();

        assertTrue("Get Some link is not present.", put_getSomeLink.isDisplayed());

        put_getSomeLink.click();

        waitModel().until().element(get_getButton).is().present();
        waitModel().until().element(get_getAllButton).is().present();

        assertTrue("Input for get key is not present.", get_getInput.isDisplayed());
        assertTrue("Get button is not present.", get_getButton.isDisplayed());

        waitModel().until().element(get_getInput).is().present();
        get_getInput.sendKeys(key);

        waitModel().until().element(get_getButton).is().present();
        get_getButton.click();

        waitModel().until().element(result).is().present();
        String cacheEntries = result.getText();
        assertTrue("Cache does not contain key: " + key, cacheEntries.contains(value));

        waitModel().until().element(get_getAllButton).is().present();
        assertTrue("Get all button is not present.", get_getAllButton.isDisplayed());
        get_getAllButton.click();

        waitModel().until().element(result).is().present();
        cacheEntries = result.getText();
        assertTrue("Cache does not contain entry: " + key + "=" + value, cacheEntries.contains(key + "=" + value));
    }

    private void testEmptyCache(URL path) {
        browser.get(path.toExternalForm());
        waitModel().until().element(put_getSomeLink).is().present();

        assertTrue("Get Some link is not present.", put_getSomeLink.isDisplayed());

        put_getSomeLink.click();

        waitModel().until().element(get_getAllButton).is().present();
        assertTrue("Get all button is not present.", get_getAllButton.isDisplayed());

        get_getAllButton.click();

        waitModel().until().element(result).is().present();
        String cacheEntries = result.getText();
        assertTrue("Cache is not empty!", cacheEntries.contains("Nothing in the Cache"));
    }
}

