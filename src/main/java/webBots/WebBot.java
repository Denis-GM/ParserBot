package webBots;

import models.LegalEntityInfo;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import parsers.ListorgParser;

import java.util.List;

public class WebBot {

    private static final WebDriver driver = new ChromeDriver();
    //WebDriver driver = new PhantomDriver();

    public static void Connect(String url){
        url = "https://www.list-org.com/";
        driver.get(url);
        driver.manage().timeouts().implicitlyWait(java.time.Duration.ofMillis(500));
    }

    public static LegalEntityInfo Search(String searchText){
//        searchText = "Федеральное государственное автономное образовательное учреждение высшего образования \"Сибирский федеральный университет\"";

        WebElement textBox = driver.findElement(By.name("val"));
        textBox.clear();
        WebElement submitButton;
        try {
            submitButton = driver.findElement(By.cssSelector(".input-group .btn-primary"));
        }
        catch (Exception e){
            submitButton = driver.findElement(By.cssSelector(".input-group button"));
        }

        textBox.sendKeys(searchText);
        submitButton.click();

//      На странице только ОДИН элемент
        WebElement content = driver.findElement(By.cssSelector(".content p"));
        if(content != null && Integer.parseInt(content.getText().split(" ")[1]) == 1){
            WebElement btn = driver.findElement(By.cssSelector(".org_list p label a"));
            btn.click();

            return GetInfo();
        }
        return new LegalEntityInfo();
    }

    public static LegalEntityInfo GetInfo() {
        List<WebElement> contactInformation = driver.findElements(By.cssSelector(".content .card"));
        String mainInfo = contactInformation.get(1).getText();
        String contactInfo = contactInformation.get(2).getText();

        ListorgParser listorgParser = new ListorgParser();
        LegalEntityInfo legalEntityInfo = listorgParser.Parser(String.join("\n", mainInfo, contactInfo));
        return legalEntityInfo;
    }
}
