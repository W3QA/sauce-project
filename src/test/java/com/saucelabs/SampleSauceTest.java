package com.saucelabs;

/**
 * @author Ross Rowe
 */

import com.saucelabs.common.SauceOnDemandAuthentication;
import com.saucelabs.common.SauceOnDemandSessionIdProvider;
import com.saucelabs.testng.SauceOnDemandAuthenticationProvider;
import com.saucelabs.testng.SauceOnDemandTestListener;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertEquals;


/**
 * Simple TestNG test which demonstrates being instantiated via a DataProvider in order to supply multiple browser combinations.
 *
 * @author Ross Rowe
 */
@Listeners({SauceOnDemandTestListener.class})
public class SampleSauceTest implements SauceOnDemandSessionIdProvider, SauceOnDemandAuthenticationProvider {

    /**
     * Constructs a {@link com.saucelabs.common.SauceOnDemandAuthentication} instance using the supplied user name/access key.  To use the authentication
     * supplied by environment variables or from an external file, use the no-arg {@link com.saucelabs.common.SauceOnDemandAuthentication} constructor.
     */
    public SauceOnDemandAuthentication authentication = new SauceOnDemandAuthentication("W3QA", "ab0d9419-186f-4a3f-8d15-8a6a5f45eaa2");

    /**
     * ThreadLocal variable which contains the  {@link WebDriver} instance which is used to perform browser interactions with.
     */
    private ThreadLocal<WebDriver> webDriver = new ThreadLocal<WebDriver>();

    /**
     * ThreadLocal variable which contains the Sauce Job Id.
     */
    private ThreadLocal<String> sessionId = new ThreadLocal<String>();

    /**
     * DataProvider that explicitly sets the browser combinations to be used.
     *
     * @param testMethod
     * @return
     */
    @DataProvider(name = "hardCodedBrowsers", parallel = false)
        public static Object[][] sauceBrowserDataProvider(Method testMethod) {
        return new Object[][]{
                //Working:
                //new Object[]{"internet explorer", "11", "Windows 8.1"},
                //new Object[]{"internet explorer", "10", "Windows 8"},
                //new Object[]{"firefox", "25", "Windows 8.1"},
                //new Object[]{"chrome", "30", "Windows 8"},

                //Not working:

                // Unknown:
                new Object[]{"safari", "6", "OSX 10.8"},
        };
    }




    /**
     * /**
     * Constructs a new {@link RemoteWebDriver} instance which is configured to use the capabilities defined by the browser,
     * version and os parameters, and which is configured to run against ondemand.saucelabs.com, using
     * the username and access key populated by the {@link #authentication} instance.
     *
     * @param browser Represents the browser to be used as part of the test run.
     * @param version Represents the version of the browser to be used as part of the test run.
     * @param os Represents the operating system to be used as part of the test run.
     * @return
     * @throws MalformedURLException if an error occurs parsing the url
     */
    private WebDriver createDriver(String browser, String version, String os) throws MalformedURLException {

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(CapabilityType.BROWSER_NAME, browser);
        if (version != null) {
            capabilities.setCapability(CapabilityType.VERSION, version);
        }
        capabilities.setCapability(CapabilityType.PLATFORM, os);
        capabilities.setCapability("name", "Sauce Sample Test");

        /*
        DesiredCapabilities caps = DesiredCapabilities.android();
        caps.setCapability("platform", "Linux");
        caps.setCapability("version", "4.3");
        caps.setCapability("device-type", "tablet");
        caps.setCapability("device-orientation", "portrait");
        */



        webDriver.set(new RemoteWebDriver(
                new URL("http://" + authentication.getUsername() + ":" + authentication.getAccessKey() + "@ondemand.saucelabs.com:80/wd/hub"),
                capabilities));
        sessionId.set(((RemoteWebDriver) getWebDriver()).getSessionId().toString());
        return webDriver.get();
    }




    /**
     * Runs a simple test verifying the title of the amazon.com homepage.
     *
     * @param browser Represents the browser to be used as part of the test run.
     * @param version Represents the version of the browser to be used as part of the test run.
     * @param os Represents the operating system to be used as part of the test run.
     * @throws Exception if an error occurs during the running of the test
     */
    @Test(dataProvider = "hardCodedBrowsers")
    public void checkMicroSitesBoroughs(String browser, String version, String os) throws Exception {

        WebDriver driver = createDriver(browser, version, os);
        String baseUrl = "http://boroughs.tfl.gov.uk";
        //String baseUrl = "www.amazon.com";
        driver.get(baseUrl);
        driver.manage().timeouts().pageLoadTimeout(200, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(100, TimeUnit.SECONDS);


        //driver.get(baseUrl + "/tfl/login.aspx?returnurl=%2fdefault.aspx");

        driver.findElement(By.id("TfLLogin_txt_email")).clear();
        driver.findElement(By.id("TfLLogin_txt_email")).sendKeys("jasonpitter@tfl.gov.uk");
        driver.findElement(By.id("TfLLogin_txt_password")).clear();
        driver.findElement(By.id("TfLLogin_txt_password")).sendKeys("password");
        driver.findElement(By.cssSelector("INPUT#TfLLogin_btn_login.button-default-2")).click();

        driver.findElement(By.linkText("Borough and regional information")).click();
        driver.findElement(By.linkText("Local Implementation Plans")).click();
        driver.findElement(By.linkText("News and events")).click();
        driver.findElement(By.linkText("Contact us")).click();
        driver.findElement(By.linkText("Staff directory")).click();
        driver.findElement(By.id("q")).clear();
        driver.findElement(By.id("q")).sendKeys("staff");
        driver.findElement(By.cssSelector("input.but_go")).click();
        // End of Boroughs

        assertEquals("Staff directory | Transport for London Boroughs", driver.getTitle());
        driver.quit();
    }





    @Test
    public void checkMicroSiteBoroughs () {

        /*
        baseUrl = "boroughs.tfl.gov.uk";
        driver.get(baseUrl + "/tfl/login.aspx?returnurl=%2fdefault.aspx");

        driver.findElement(By.id("TfLLogin_txt_email")).clear();
        driver.findElement(By.id("TfLLogin_txt_email")).sendKeys("jasonpitter@tfl.gov.uk");
        driver.findElement(By.id("TfLLogin_txt_password")).clear();
        driver.findElement(By.id("TfLLogin_txt_password")).sendKeys("password");
        driver.findElement(By.cssSelector("INPUT#TfLLogin_btn_login.button-default-2")).click();

        driver.findElement(By.linkText("Borough and regional information")).click();
        driver.findElement(By.linkText("Local Implementation Plans")).click();
        driver.findElement(By.linkText("News and events")).click();
        driver.findElement(By.linkText("Contact us")).click();
        driver.findElement(By.linkText("Staff directory")).click();
        driver.findElement(By.id("q")).clear();
        driver.findElement(By.id("q")).sendKeys("staff");
        driver.findElement(By.cssSelector("input.but_go")).click();
        // End of Boroughs

        Assert.assertEquals("Staff directory | Transport for London Boroughs", driver.getTitle());
        */

    }



    /**
     * @return the {@link WebDriver} for the current thread
     */
    public WebDriver getWebDriver() {
        System.out.println("WebDriver" + webDriver.get());
        return webDriver.get();
    }

    /**
     *
     * @return the Sauce Job id for the current thread
     */
    public String getSessionId() {
        return sessionId.get();
    }

    /**
     *
     * @return the {@link SauceOnDemandAuthentication} instance containing the Sauce username/access key
     */
    @Override
    public SauceOnDemandAuthentication getAuthentication() {
        return authentication;
    }
}

