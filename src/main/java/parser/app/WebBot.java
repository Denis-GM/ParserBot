package parser.app;

import parser.models.BusinessInfo;
import parser.models.BusinessInfoSearch;
import parser.models.Filters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import parser.models.Person;
import parser.parsers.ListorgParser;

import java.time.Duration;
import java.util.*;

public class WebBot {
    private static WebDriver driver = new ChromeDriver(new ChromeOptions().addArguments("--remote-allow-origins=*"));
    private static Map<String,String> dictTypesPerson = new HashMap<String,String>();

    public WebBot(){
        initDictTypesPerson();
    }

    public static void Start(String url){
        driver.get(url);
        driver.manage().timeouts().implicitlyWait(java.time.Duration.ofMillis(500));
    }

    public static void Stop(){
        driver.quit();
    }


    public static BusinessInfo StartSearchChecko(BusinessInfoSearch searchText){
        LinkedList<String> fullAddress = new LinkedList<>(
                Arrays.asList(searchText.getAddress().split(",")));
        BusinessInfo businessInfo = SearchJuristicPersonChecko(searchText.getFullName(), fullAddress);
        if(businessInfo.isEmpty()){

        }
        return businessInfo;
    }

    private static int GetCountPages(){
        List<WebElement> paginationButtons =
                driver.findElements(By.cssSelector(".uk-pagination li"));
        return paginationButtons.size() < 1 ? 1 : paginationButtons.size();
    }

    private static BusinessInfo SearchJuristicPersonChecko(String name, LinkedList<String> fullAddress){
        int page = 1;
        BusinessInfo businessInfo = new BusinessInfo();
        String address = String.join(",", fullAddress);

        String current_url = String.format("https://checko.ru/search?query=%s&page=%s", name, page);
        driver.get(current_url);
        CheckRecaptcha();

        int countPages = GetCountPages();
        for(int i = 1; i <= countPages; i++) {
            List<WebElement> listJuristicPerson =
                    driver.findElements(By.cssSelector(".uk-container .uk-table tr"));
            for(var juristicPerson : listJuristicPerson){
                List<WebElement> juristicPersonInfo = juristicPerson
                        .findElements(By.cssSelector("td"))
                        .get(1).findElements(By.cssSelector("div"));
//                System.out.println(juristicPersonInfo.get(0).getText());
//                System.out.println(juristicPersonInfo.get(2).getText());
            }
        }
        return businessInfo;
    }

    private static BusinessInfo RecursiveSearchPhysicalPersonChecko(String name, int page, LinkedList<String> fullAddress){
        BusinessInfo businessInfo = new BusinessInfo();
        String address = String.join(",", fullAddress);

        String current_url = String.format("https://checko.ru/search?query=%s&page=%s", name, page);
        driver.get(current_url);
        CheckRecaptcha();

        if(GetNumberItemsOnPage() == 1){
            businessInfo = GetPhysicalPerson();
        }
        else {
            CheckRecaptcha();

            if(fullAddress.size() > 1){
                fullAddress.removeLast();
                businessInfo = RecursiveSearchPhysicalPersonListOrg(name, fullAddress);
            }
            else {
                return businessInfo;
            }
        }
        return businessInfo;
    }



    public static BusinessInfo StartSearchListOrg(BusinessInfoSearch searchText){
        GetListByFilter(searchText, Filters.all);

        LinkedList<String> fullAddress = new LinkedList<>(
                Arrays.asList(searchText.getAddress().split(",")));
        BusinessInfo businessInfo = RecursiveSearchJuristicPersonListOrg(searchText.getFullName(), fullAddress);
        if(businessInfo.isEmpty()){
            GetListByFilter(searchText, Filters.fio);
            businessInfo = RecursiveSearchPhysicalPersonListOrg(searchText.getFullName(), fullAddress);
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

    private static BusinessInfo RecursiveSearchJuristicPersonListOrg(String name, LinkedList<String> fullAddress){
        BusinessInfo businessInfo = new BusinessInfo();
        String address = String.join(",", fullAddress);
        String searchText = String.join(",", name, address);

        String current_url = String.format("https://www.list-org.com/search?val=%s&type=all", searchText);
        driver.get(current_url);
        CheckRecaptcha();

        if(GetNumberItemsOnPage() == 1){
            businessInfo = GetJuristicPerson();
        }
        else {
            CheckRecaptcha();

            if(fullAddress.size() > 1){
                fullAddress.removeLast();
                businessInfo = RecursiveSearchJuristicPersonListOrg(name, fullAddress);
            }
            else {
                return businessInfo;
            }
        }
        return businessInfo;
    }

    private static BusinessInfo RecursiveSearchPhysicalPersonListOrg(String name, LinkedList<String> fullAddress){
        BusinessInfo businessInfo = new BusinessInfo();
        String address = String.join(",", fullAddress);
        String searchText = String.join(",", name, address);

        String current_url = String.format("https://www.list-org.com/search?val=%s&type=fio", searchText);
        driver.get(current_url);
        CheckRecaptcha();

        if(GetNumberItemsOnPage() == 1){
            businessInfo = GetPhysicalPerson();
        }
        else {
            CheckRecaptcha();

            if(fullAddress.size() > 1){
                fullAddress.removeLast();
                businessInfo = RecursiveSearchPhysicalPersonListOrg(name, fullAddress);
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
                    searchTextInfo.getFullName(), searchTextInfo.getIndex(), searchTextInfo.getAddress());
            case Filters.name -> searchTextInfo.getFullName();
            case Filters.fio -> searchTextInfo.getFullName();
            case Filters.address -> String.join(",", searchTextInfo.getIndex(), searchTextInfo.getAddress());
            case Filters.trademark -> searchTextInfo.getTrademarkNumber();
            default -> "";
        };
        String current_url = String.format("https://www.list-org.com/search?val=%s&type=%s", searchText, type);
        driver.get(current_url);
        CheckRecaptcha();
    }

    private static BusinessInfo GetJuristicPerson(){
        try {
            WebElement btn = driver.findElement(By.cssSelector(".org_list p label a"));
            btn.click();
            CheckRecaptcha();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        return GetInfoByPage(Person.Juristic);
    }

    private static BusinessInfo GetPhysicalPerson(){
        try {
            WebElement btn = driver.findElement(By.cssSelector(".org_list p a"));
            btn.click();
            CheckRecaptcha();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        return GetInfoByPage(Person.Physical);
    }

    private static BusinessInfo GetInfoByPage(Person type) {
        String mainInfo = "", contactInfo = "";
        if(Person.Juristic == type){
            List<WebElement> contactInformation = driver.findElements(By.cssSelector(".content .card"));
            mainInfo = contactInformation.get(1).getText();
            contactInfo = contactInformation.get(2).getText();
        }
        else if(Person.Physical == type){
            List<WebElement> contactInformation = driver.findElements(By.cssSelector(".content .card"));
            mainInfo = contactInformation.get(1).getText();
        }

        ListorgParser listorgParser = new ListorgParser();
        BusinessInfo businessInfo = listorgParser.Parser(String.join("\n", mainInfo, contactInfo));
        return businessInfo;
    }

    private static void WaitRecaptcha() {
        driver.switchTo().frame(0);
        WebElement recaptchaCheckbox = driver.findElement(By.cssSelector("#recaptcha-anchor"));
        recaptchaCheckbox.click();
        driver.switchTo().defaultContent();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(180));

        wait.until(ExpectedConditions.elementToBeClickable
                (By.cssSelector("div.recaptcha-checkbox-checkmark")))
                .click();
    }

    private static void CheckRecaptcha() {
        try{
            driver.findElement(By.name("frm"));
            WaitRecaptcha();
        }
        catch (Exception e){
//            System.out.println(e.getMessage());
        }
    }

    private static boolean EqualNamePerson(String webName, String excelName){
        String str = "";
        switch (webName){
            case "":
                break;
        }
        return true;
    }

    private static void initDictTypesPerson(){
        dictTypesPerson.put("Акционерное общество", "АО");
        dictTypesPerson.put("Индивидуальный предприниматель", "ИП");
        dictTypesPerson.put("Открытое акционерное общество", "ОАО");
        dictTypesPerson.put("Государственное автономное учреждение", "ГАУ");
        dictTypesPerson.put("Автономная Некоммерческая Организация", "АНО");
        dictTypesPerson.put("Общество с ограниченной ответственностью", "OOO");
        dictTypesPerson.put("Краевое государственное автономное учреждение", "КГАУ");
        dictTypesPerson.put("Федеральное государственное автономное образовательное учреждение высшего образования", "ФГАОУ ВО");
    }
}
