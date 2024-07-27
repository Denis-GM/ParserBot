package parser.app;

import parser.models.BusinessInfo;
import parser.models.BusinessInfoSearch;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import parser.models.Person;
import parser.parsers.CheckoParser;
import parser.parsers.ListorgParser;

import java.time.Duration;
import java.util.*;

public class WebBot {
    private static int delayRecaptchaSolution = 120;
    private static WebDriver driver;
    private static Map<String,String> dictTypesPerson = new HashMap<String,String>();

    public WebBot(){
        initDictTypesPerson();
    }

    public static void start(){
        driver = new ChromeDriver(new ChromeOptions().addArguments("--remote-allow-origins=*"));
        driver.get("https://www.google.com/");
        driver.manage().timeouts().implicitlyWait(java.time.Duration.ofMillis(500));
    }

    public static void stop(){
        if (driver != null) {
            driver.quit();
        }
    }

    private static void goToPageByUrl(String url, String value1, String value2){
        String current_url = String.format(url, value1, value2);
        driver.get(current_url);
        checkRecaptcha();
    }

    private static BusinessInfo searchJuristicPersonChecko(String name, LinkedList<String> fullAddress){
        int page = 1;
        BusinessInfo businessInfo = new BusinessInfo();
        String address = String.join(",", fullAddress);
        goToPageByUrl("https://checko.ru/search?query=%s&page=%s", name, Integer.toString(page));
        int countPages = getCountPagesChecko();
        for(int i = 1; i <= countPages; i++) {
            List<WebElement> listJuristicPerson =
                    driver.findElements(By.cssSelector(".uk-container .uk-table tr"));
            for(var juristicPerson : listJuristicPerson){
                List<WebElement> juristicPersonInfo = juristicPerson
                        .findElements(By.cssSelector("td"))
                        .get(1).findElements(By.cssSelector("div"));
            }
        }
        return businessInfo;
    }

    private static BusinessInfo searchPhysicalPersonChecko(String name, LinkedList<String> fullAddress){
        int page = 1;
        BusinessInfo businessInfo = new BusinessInfo();
        goToPageByUrl("https://checko.ru/search?query=%s&page=%s", name, Integer.toString(page));
        int countPages = getCountPagesChecko();
        for(int i = 1; i <= countPages; i++) {
            List<WebElement> listPhysicalPerson =
                    driver.findElements(By.cssSelector(".uk-container .uk-table tr"));
            for(var juristicPerson : listPhysicalPerson){
                List<WebElement> physicalPersonInfo = juristicPerson
                        .findElements(By.cssSelector("td"))
                        .get(1).findElements(By.cssSelector("div"));

                String nameFromSite = physicalPersonInfo.get(0).getText().replace("ИП ", "").toLowerCase();
                String addressFromSite = physicalPersonInfo.get(2).getText();

                if(nameFromSite.equals(name.toLowerCase())){
                    for(var el : fullAddress){
                        if(addressFromSite.indexOf(el) != -1){
//                            try {
//                                System.out.println(physicalPersonInfo.get(0).getText());
//                                WebElement btn = physicalPersonInfo.get(0).findElement(By.cssSelector("a"));
//                                btn.click();
////                                CheckRecaptcha();
////                                GetInfoByPageChecko(Person.Physical);
//                            }
//                            catch (Exception e){
//                                System.out.println(e.getMessage());
//                            }
                        }
                    }
                }
            }
        }
        return businessInfo;
    }

    private static int getCountPagesChecko(){
        List<WebElement> paginationButtons =
                driver.findElements(By.cssSelector(".uk-pagination li"));
        return paginationButtons.size() < 1 ? 1 : paginationButtons.size();
    }

    private static BusinessInfo getInfoByPageChecko(Person type) {
        String mainInfo = "", directorInfo = "";
        if(Person.Juristic == type){
            List<WebElement> contactInformation = driver.findElements(By.cssSelector("basic .uk-grid"));
            mainInfo = contactInformation.get(0).getText();
            directorInfo = contactInformation.get(1).getText();
        }
        else if(Person.Physical == type){
            List<WebElement> contactInformation = driver.findElements(By.cssSelector("basic .uk-grid"));
            mainInfo = contactInformation.get(0).getText();
            directorInfo = contactInformation.get(1).getText();
        }

        CheckoParser checkoParser = new CheckoParser();
        BusinessInfo businessInfo = checkoParser.Parser(String.join("\n", mainInfo, directorInfo));
        return businessInfo;
    }


    public static BusinessInfo startSearch(BusinessInfoSearch searchText){
        BusinessInfo businessInfo;
        LinkedList<String> fullAddress = new LinkedList<>(
                Arrays.asList(searchText.getAddress().split(",")));
        if(searchText.getTypePerson() == Person.Juristic){
            businessInfo = searchJuristicPersonListOrg(searchText.getFullName(), fullAddress);
        }
        else {
            businessInfo = searchPhysicalPersonListOrg(searchText.getShortName(), fullAddress);
        }
        return businessInfo;
    }

    private static BusinessInfo searchJuristicPersonListOrg(String name, LinkedList<String> fullAddress){
        BusinessInfo businessInfo = new BusinessInfo();
        String address = String.join(",", fullAddress);
        String searchText = String.join(",", name, address);
        goToPageByUrl("https://www.list-org.com/search?val=%s&type=%s", searchText, "all");
        if(getNumberItemsOnPage() == 1){
            businessInfo = getJuristicPerson();
        }
        else {
            checkRecaptcha();

            if(fullAddress.size() > 1){
                fullAddress.removeLast();
                businessInfo = searchJuristicPersonListOrg(name, fullAddress);
            }
            else {
                return businessInfo;
            }
        }
        return businessInfo;
    }

    private static BusinessInfo searchPhysicalPersonListOrg(String name, LinkedList<String> fullAddress){
        BusinessInfo businessInfo = new BusinessInfo();
        goToPageByUrl("https://www.list-org.com/search?val=%s&type=%s", name, "fio");
        if(getNumberItemsOnPage() == 1) {
            businessInfo = getPhysicalPerson();
        }
        else {
            searchPhysicalPersonChecko(name, fullAddress);
        }
        return businessInfo;
    }

    private static int getNumberItemsOnPage(){
        WebElement content = driver.findElement(By.cssSelector(".content p"));
        if(content != null){
            return Integer.parseInt(content.getText().split(" ")[1]);
        }
        return -1;
    }

    private static BusinessInfo getJuristicPerson(){
        try {
            WebElement btn = driver.findElement(By.cssSelector(".org_list p label a"));
            btn.click();
            checkRecaptcha();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        return getInfoByPageListOrg(Person.Juristic);
    }

    private static BusinessInfo getPhysicalPerson(){
        try {
            WebElement btn = driver.findElement(By.cssSelector(".org_list p a"));
            btn.click();
            checkRecaptcha();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        return getInfoByPageListOrg(Person.Physical);
    }

    private static BusinessInfo getInfoByPageListOrg(Person type) {
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

    private static void waitRecaptcha() {
        driver.switchTo().frame(0);
        WebElement recaptchaCheckbox = driver.findElement(By.cssSelector("#recaptcha-anchor"));
        recaptchaCheckbox.click();
        driver.switchTo().defaultContent();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(delayRecaptchaSolution));

        Application.setStatus("Ожидает решения ReCaptcha");
        Application.startTimer();

        wait.until(ExpectedConditions.elementToBeClickable
                (By.cssSelector("div.recaptcha-checkbox-checkmark")))
                .click();

        Application.stopTimer();
    }

    private static void checkRecaptcha() {
        try{
            driver.findElement(By.name("frm"));
            waitRecaptcha();
        }
        catch (Exception e){
//            System.out.println(e.getMessage());
        }
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

    public static int getDelayRecaptchaSolution(){
        return delayRecaptchaSolution;
    }

    public static void setDelayRecaptchaSolution(int delay){
        delayRecaptchaSolution = delay;
    }
}
