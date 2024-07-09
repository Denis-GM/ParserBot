package webBots;

import models.BusinessInfo;
import models.BusinessInfoSearch;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import parsers.ListorgParser;

import java.time.Duration;
import java.util.List;

public class WebBot {

    private static final WebDriver driver = new ChromeDriver();
    //WebDriver driver = new PhantomDriver();

    public static void Connect(String url){
        driver.get(url);
        driver.manage().timeouts().implicitlyWait(java.time.Duration.ofMillis(500));
    }

    public static BusinessInfo Search(BusinessInfoSearch searchText){
        CheckRecaptcha();
        WebElement textBox = driver.findElement(By.name("val"));
        textBox.clear();
        WebElement submitButton;

        try {
            submitButton = driver.findElement(By.cssSelector(".input-group .btn-primary"));
        }
        catch (Exception e){
            submitButton = driver.findElement(By.cssSelector(".input-group button"));
        }

        textBox.sendKeys(searchText.getName());
        submitButton.click();
        CheckRecaptcha();

//      На странице только ОДИН элемент
        WebElement content = driver.findElement(By.cssSelector(".content p"));
        if(content != null && Integer.parseInt(content.getText().split(" ")[1]) == 1){
            WebElement btn = driver.findElement(By.cssSelector(".org_list p label a"));
            btn.click();

            return GetInfo();
        }
        return new BusinessInfo();
    }

    public static BusinessInfo GetInfo() {
        List<WebElement> contactInformation = driver.findElements(By.cssSelector(".content .card"));
        String mainInfo = contactInformation.get(1).getText();
        String contactInfo = contactInformation.get(2).getText();

        ListorgParser listorgParser = new ListorgParser();
        BusinessInfo businessInfo = listorgParser.Parser(String.join("\n", mainInfo, contactInfo));
        return businessInfo;
    }

    public static void WaitRecaptcha() {
        driver.switchTo().frame(0);
        WebElement recaptchaCheckbox = driver.findElement(By.cssSelector("#recaptcha-anchor"));
        recaptchaCheckbox.click();
        driver.switchTo().defaultContent();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(300));

        WebElement btn = driver.findElement(By.id("recaptcha-verify-button"));
        wait.until(ExpectedConditions.elementToBeClickable(btn));
        btn.click();
    }

    public static void CheckRecaptcha() {
        try{
            driver.findElement(By.name("frm"));
            WaitRecaptcha();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
