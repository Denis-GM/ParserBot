import models.LegalEntityInfo;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import parsers.ListorgParser;
import webBots.WebBot;

import java.io.IOException;
import java.util.List;


public class Program {
    public static void main(String[] args) {
        WebBot.Connect("");
        try {
            Excel.readFromExcel("src/main/resources/Выгрузка от 5 июня (2300).xlsx");
//            Excel.readFromExcel("src/main/resources/ex.xlsx");
        }
            catch (IOException e) {
            throw new RuntimeException(e);
        }
//        WebBot.Search("");
//        WebBot.GetInfo();
    }
}
