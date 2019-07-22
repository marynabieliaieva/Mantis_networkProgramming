package ru.stqa.pft.mantis.appmanager;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.BrowserType;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class ApplicationManager {
  private WebDriver wd;
  private final Properties properties;

  private String browser;
  private String registrationHelper;
  private FtpHelper ftp;


  public ApplicationManager(String browser) {
    this.browser = browser;
    properties = new Properties();
  }

  public void init() throws IOException, InterruptedException {
    String target = System.getProperty("target", "local");
    properties.load(new FileReader(new File(String.format("src/test/resources/%s.properties", target))));
  }


  public void stop() {
    if(wd != null){
      wd.quit();
    }
  }

  public HttpSession newSession() {
    return new HttpSession(this);
  }


  public String getProperty(String key) {
    if (registrationHelper == null){
      registrationHelper =  properties.getProperty(key);
    }
    return registrationHelper;
  }

  public RegistrationHelper registration() {
    return new RegistrationHelper(this);
  }

  public FtpHelper ftp(){
    if (ftp == null){
      ftp = new FtpHelper(this);
    }
    return ftp;
  }


  public WebDriver getDriver() {
    if (wd == null) {
      if (browser.equals(BrowserType.FIREFOX)) {
        wd = new FirefoxDriver();
      } else if (browser.equals(BrowserType.CHROME)) {
        wd = new ChromeDriver();
      } else if (browser.equals(BrowserType.IE)) {
        wd = new InternetExplorerDriver();
      }
      wd.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
      //wd.get("https://login.szn.cz/");
      wd.get(properties.getProperty("web.baseUrl"));
      wd.manage().window().maximize();

    }
    return wd;
  }
}
