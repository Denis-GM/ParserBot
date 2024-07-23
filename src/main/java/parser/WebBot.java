package parser;

import parser.models.BusinessInfo;
import parser.models.BusinessInfoSearch;
import parser.models.Filters;
import parser.models.Persons;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import parser.parsers.ListorgParser;

import java.time.Duration;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class WebBot {
    private static WebDriver driver = new ChromeDriver(new ChromeOptions().addArguments("--remote-allow-origins=*"));
    //WebDriver driver = new PhantomDriver();

    private WebBot(){ }

    public static void Start(String url){
        driver.get(url);
        driver.manage().timeouts().implicitlyWait(java.time.Duration.ofMillis(500));
    }

    public static void Stop(){
        driver.quit();
    }

    public static BusinessInfo StartSearchListOrg(BusinessInfoSearch searchText){
        GetListByFilter(searchText, Filters.all);
        CheckRecaptcha();

        LinkedList<String> fullAddress = new LinkedList<>(
                Arrays.asList(searchText.getAddress().split(",")));
        BusinessInfo businessInfo = RecursiveSearchJuristicPerson(searchText.getName(), fullAddress);
        if(businessInfo.isEmpty()){
            GetListByFilter(searchText, Filters.fio);
            businessInfo = RecursiveSearchPhysicalPerson(searchText.getName(), fullAddress);
        }
        return businessInfo;
    }

    private static int GetNumberItemsOnPage(){
        WebElement content = driver.findElement(By.cssSelector(".content p"));
        if(content != null){
            return Integer.parseInt(content.getText().split(" ")[1]);
        }
        return -1;
    }

    private static BusinessInfo RecursiveSearchJuristicPerson(String name, LinkedList<String> fullAddress){
        BusinessInfo businessInfo = new BusinessInfo();
        String address = String.join(",", fullAddress);
        String searchText = String.join(",", name, address);

        String current_url = String.format("https://www.list-org.com/search?val=%s&type=all", searchText);
        driver.get(current_url);

        if(GetNumberItemsOnPage() == 1){
            businessInfo = GetJuristicPerson();
        }
        else {
            CheckRecaptcha();

            if(fullAddress.size() > 1){
                fullAddress.removeLast();
                businessInfo = RecursiveSearchJuristicPerson(name, fullAddress);
            }
            //скать ИП
            else {
//                if(name.split(" ").length <= 3)
//                    businessInfo = RecursiveSearch(name, fullAddress, Filters.fio);
//                else
                return businessInfo;
            }
        }
        return businessInfo;
    }

    private static BusinessInfo RecursiveSearchPhysicalPerson(String name, LinkedList<String> fullAddress){
        BusinessInfo businessInfo = new BusinessInfo();
        String address = String.join(",", fullAddress);
        String searchText = String.join(",", name, address);

        String current_url = String.format("https://www.list-org.com/search?val=%s&type=fio", searchText);
        driver.get(current_url);

        if(GetNumberItemsOnPage() == 1){
            businessInfo = GetPhysicalPerson();
        }
        else {
            CheckRecaptcha();

            if(fullAddress.size() > 1){
                fullAddress.removeLast();
                businessInfo = RecursiveSearchPhysicalPerson(name, fullAddress);
            }
            else {
                return businessInfo;
            }
        }
        return businessInfo;
    }

    private static void GetListByFilter(BusinessInfoSearch searchTextInfo, Filters type){
        String searchText = switch (type) {
            case Filters.all -> String.join(",",
                    searchTextInfo.getName(), searchTextInfo.getIndex(), searchTextInfo.getAddress());
            case Filters.name -> searchTextInfo.getName();
            case Filters.fio -> searchTextInfo.getName();
            case Filters.address -> String.join(",", searchTextInfo.getIndex(), searchTextInfo.getAddress());
            case Filters.trademark -> searchTextInfo.getTrademarkNumber();
            default -> "";
        };
        String current_url = String.format("https://www.list-org.com/search?val=%s&type=%s", searchText, type);
        driver.get(current_url);
    }

    public static BusinessInfo GetJuristicPerson(){
        WebElement btn = driver.findElement(By.cssSelector(".org_list p label a"));
        btn.click();
        return GetInfoByPage(Persons.Juristic);
    }

    public static BusinessInfo GetPhysicalPerson(){
        try {
            WebElement btn = driver.findElement(By.cssSelector(".org_list p a"));
            btn.click();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        return GetInfoByPage(Persons.Physical);
    }

    public static BusinessInfo GetInfoByPage(Persons type) {
        String mainInfo = "", contactInfo = "";
        if(Persons.Juristic == type){
            List<WebElement> contactInformation = driver.findElements(By.cssSelector(".content .card"));
            mainInfo = contactInformation.get(1).getText();
            contactInfo = contactInformation.get(2).getText();
        }
        else if(Persons.Physical == type){
            List<WebElement> contactInformation = driver.findElements(By.cssSelector(".content .card"));
            mainInfo = contactInformation.get(1).getText();
        }

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
//            System.out.println(e.getMessage());
        }
    }
}
