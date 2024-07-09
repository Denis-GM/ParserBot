import models.BusinessInfo;
import models.BusinessInfoSearch;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import webBots.WebBot;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Stack;

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
                var infoSearch = ParseRow(row);
//                BusinessInfo info = WebBot.Search(infoSearch);
//                System.out.printf(
//                        "Полное юридическое наименование: %s\nРуководитель: %s\nИНН: %s\nСтатус: %s",
//                        info.getFullLegalName(), info.getDirector(), info.getInn(), info.getStatus());
//                System.out.println("\n------------------------------------------------");

                System.out.println(infoSearch.getName());
                System.out.println(infoSearch.getIndex());
                System.out.println(infoSearch.getAddress());
                System.out.println(counter);
                System.out.println("\n------------------------------------------------\n");
            }
            fileExcel.close();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static BusinessInfoSearch ParseRow(Row row) {
        var infoSearch = new BusinessInfoSearch();
        infoSearch.setTrademarkNumber(row.getCell(1).toString());
        infoSearch.setTrademarkName(row.getCell(2).toString());

        String fourthCell = row.getCell(3).toString().trim();
        LinkedList<String> fourthCellArray = ConvertArrayToLinkedList(fourthCell.split(","));
        int fourthCellArrayLen = fourthCellArray.size();

        if(fourthCellArrayLen > 1){
            for(int i = 0; i < fourthCellArrayLen; i++){
                if(i == 0)
                    infoSearch.setName(fourthCellArray.get(i).trim());
                else if(i == 1)
                    infoSearch.setIndex(fourthCellArray.get(i).trim());
                else {
                    fourthCellArray.removeFirst();
                    fourthCellArray.removeFirst();

                    String address = String.join(",", fourthCellArray);
                    infoSearch.setAddress(address.substring(0, address.length() - 5).trim());
                    break;
                }
            }
        }
        else {
            infoSearch.setName(fourthCellArray.getFirst());

            String fullAddress = row.getCell(4).toString().trim();
            LinkedList<String> fullAddressArray = ConvertArrayToLinkedList(fullAddress.split(","));
            int fullAddressArrayLen = fullAddressArray.size();

            for(int i = 0; i < fullAddressArrayLen; i++){
                if(i == 0)
                    infoSearch.setIndex(fullAddressArray.get(i).trim());
                else {
                    fullAddressArray.removeFirst();
                    fullAddressArray.removeFirst();

                    String address = String.join(",", fullAddressArray);
                    infoSearch.setAddress(address.substring(0, address.length() - 5).trim());
                    break;
                }
            }
        }
        return infoSearch;
    }

    public static LinkedList<String> ConvertArrayToLinkedList(String[] array){
        LinkedList<String> linkedList = new LinkedList<>();
        Collections.addAll(linkedList, array);
        return linkedList;
    }
}
