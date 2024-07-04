import models.LegalEntityInfo;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import webBots.WebBot;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;

public class Excel {
    public static void readFromExcel(String file) throws IOException {
        try {
            FileInputStream fileExcel = new FileInputStream(file);
            XSSFWorkbook workbook = new XSSFWorkbook(fileExcel);

            XSSFSheet sheet = workbook.getSheetAt(0);

            int counter = 0;
            for (Row row : sheet) {
                counter++;
                if(counter < 3){
                    continue;
                }
                Iterator<Cell> cellIterator = row.cellIterator();

                String searchText = row.getCell(3).toString();
                searchText = searchText.substring(0, searchText.length() - 5);

                LegalEntityInfo info = WebBot.Search(searchText);
//                LegalEntityInfo info = WebBot.GetInfo();
                System.out.printf(
                        "Полное юридическое наименование: %s\nРуководитель: %s\nИНН: %s\nСтатус: %s",
                        info.getFullLegalName(), info.getDirector(), info.getInn(), info.getStatus());
                System.out.println("\n------------------------------------------------");

//                while (cellIterator.hasNext()) {
//                    Cell cell = cellIterator.next();
//                    switch (cell.getCellType()) {
//                        case Cell.CELL_TYPE_NUMERIC:
//                            System.out.print(cell.getNumericCellValue() + "t");
//                            break;
//                        case Cell.CELL_TYPE_STRING:
//                            System.out.print(cell.getStringCellValue() + "t");
////                            WebBot.Search();
//                            break;
//                    }
//                }
//                System.out.println("t");
                System.out.println(counter);
            }
            fileExcel.close();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
