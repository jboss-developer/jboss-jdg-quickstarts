package org.jboss.as.quickstarts.datagrid.subsystem;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.findby.FindByJQuery;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.net.URL;

import static org.jboss.arquillian.graphene.Graphene.waitModel;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
@RunAsClient
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SubsystemClusteredTest {
    @FindBy(id = "invokeForm")
    private WebElement form_ID;
    @FindBy(id = "response")
    private WebElement responseForm_ID;
    @FindBy(id = "invokeForm:add")
    private WebElement addValueButton;

    @FindBy(id = "invokeForm:clear")
    private WebElement clearFormButton;

    @FindBy(id = "invokeForm:list")
    private WebElement listValuesButton;

    @FindBy(id = "invokeForm:get")
    private WebElement getValueButton;

    @FindBy(id = "invokeForm:del")
    private WebElement deleteValueButton;

    @FindByJQuery("#invokeForm:contains('Key:')")
    private WebElement keyLabel;
    @FindByJQuery(":input[type=text]:eq(0)")
    private WebElement keyInput;
    @FindByJQuery(":input[type=text]:eq(1)")
    private WebElement valueInput;
    @FindByJQuery("#response table:eq(0) tr:eq(0) td:eq(0)")
    private WebElement cacheEntries;
    @FindByJQuery("#response table:eq(0) tr:eq(1) td:eq(0)")
    private WebElement cacheSize;

    @Drone
    WebDriver browser;

    @ArquillianResource
    private URL contextPath;

    @Deployment(name = "eap1", testable = false)
    @TargetsContainer("clustered-jbossas-managed-1")
    public static WebArchive createTestDeployment1() {
        return Deployments.createDeployment();
    }

    @Deployment(name = "eap2", testable = false)
    @TargetsContainer("clustered-jbossas-managed-2")
    public static WebArchive createTestDeployment2() {
        return Deployments.createDeployment();
    }

    @Test
    @OperateOnDeployment("eap1")
    public void insertIntoCacheContainerOneTest() {
        browser.get(contextPath.toExternalForm());
        System.out.println("Inside basicOperationsTest, contextPath: " + contextPath);

        waitModel().until().element(form_ID).is().present();
        assertTrue("form_ID element is not present", form_ID.isDisplayed());

        waitModel().until().element(addValueButton).is().present();
        assertTrue("addValueButton is not present", addValueButton.isDisplayed());
        waitModel().until().element(listValuesButton).is().present();
        assertTrue("listValuesButton is not present", listValuesButton.isDisplayed());
        waitModel().until().element(getValueButton).is().present();
        assertTrue("getValueButton is not present", getValueButton.isDisplayed());

        waitModel().until().element(keyInput).is().present();
        assertTrue("keyInput is not present", keyInput.isDisplayed());
        waitModel().until().element(valueInput).is().present();
        assertTrue("valueInput is not present", valueInput.isDisplayed());
        waitModel().until().element(cacheSize).is().present();
        assertTrue("cacheSize is not present", cacheSize.isDisplayed());

        keyInput.sendKeys("key1");
        valueInput.sendKeys("value1");
        addValueButton.click();

        String cacheSizeAfterFirstEntry = cacheSize.getText();
        System.out.println(cacheSizeAfterFirstEntry);
        assertTrue("cacheSize after first entry into first container is not 1 but " + cacheSizeAfterFirstEntry, cacheSizeAfterFirstEntry.equals("1"));

        clearFormButton.click();
        keyInput.sendKeys("key2");
        valueInput.sendKeys("value2");
        addValueButton.click();

        String cacheSizeAfterSecondEntry = cacheSize.getText();
        System.out.println(cacheSizeAfterSecondEntry);
        assertTrue("cacheSize after second entry is not 2 but " + cacheSizeAfterSecondEntry, cacheSizeAfterSecondEntry.equals("2"));
    }

    @Test
    @OperateOnDeployment("eap2")
    public void listCacheValuesContainerTwoTest() {
        browser.get(contextPath.toExternalForm());
        System.out.println("Inside basicOperationsTest, contextPath: " + contextPath);

        waitModel().until().element(form_ID).is().present();
        assertTrue("form_ID element is not present", form_ID.isDisplayed());

        waitModel().until().element(listValuesButton).is().present();
        assertTrue("listValuesButton is not present", listValuesButton.isDisplayed());
        waitModel().until().element(cacheSize).is().present();
        assertTrue("cacheSize is not present", cacheSize.isDisplayed());

        listValuesButton.click();
        waitModel().until().element(cacheEntries).is().present();
        assertTrue("cacheEntries is not present", cacheEntries.isDisplayed());
        String listOfEntries = cacheEntries.getText().trim();
        System.out.println(listOfEntries);
        assertTrue("cacheEntries number loaded from second application is wrong: " + listOfEntries, listOfEntries.equals("key1 : value1 || key2 : value2 ||"));
    }
}
