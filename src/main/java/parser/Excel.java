package parser;

import org.apache.poi.ss.usermodel.CellStyle;
import parser.models.BusinessInfo;
import parser.models.BusinessInfoSearch;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;

public class Excel {
    private static String file = "";
    private static FileInputStream fileExcel;
    private static XSSFWorkbook workbook;

    public Excel(String file) {
        try {
            this.file = file;
            fileExcel = new FileInputStream(file);
            workbook = new XSSFWorkbook(fileExcel);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public static void readFromExcel() throws IOException {
        try {
            XSSFSheet sheet = workbook.getSheetAt(0);

            int counter = 0;
            for (Row row : sheet) {
                counter++;
                if(counter < 3){
                    continue;
                }
                var infoSearch = ParseRow(row);

                System.out.println(infoSearch.getName());
                System.out.println(infoSearch.getIndex());
                System.out.println(infoSearch.getAddress());
                System.out.println(counter);

                BusinessInfo info = WebBot.StartSearchListOrg(infoSearch);
                FillRow(row, info, file, workbook);
                System.out.printf(
                        "\nПолное юридическое наименование: %s\nРуководитель: %s\nИНН: %s\nСтатус: %s",
                        info.getFullName(), info.getDirector(), info.getInn(), info.getStatus());
                System.out.println("\n------------------------------------------------");
            }
            fileExcel.close();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void FillRow(Row row, BusinessInfo businessInfo, String file, XSSFWorkbook workbook){
        row.createCell(7).setCellValue(businessInfo.getInn());
        row.createCell(8).setCellValue(businessInfo.getStatus());

        String innExists = (businessInfo.getInn() != null) ? "Есть" : "Нет";
        row.createCell(9).setCellValue(innExists);

        row.createCell(10).setCellValue(businessInfo.getDirector());

        row.createCell(11).setCellValue(businessInfo.getAddress());

        int index = 13;
        if(businessInfo.getPhoneNumbers() != null)
            for(var phoneNumber : businessInfo.getPhoneNumbers()) {
                if(index > 17) break;
                row.createCell(index).setCellValue(phoneNumber);
                index++;
            }
        index = 18;

        if(businessInfo.getEmails() != null)
            for(var email : businessInfo.getEmails()) {
                if(index > 19) break;
                row.createCell(index).setCellValue(email);
                index++;
            }
        index = 20;

        if(businessInfo.getSites() != null)
            for(var email : businessInfo.getSites()) {
                if(index > 21) break;
                row.createCell(index).setCellValue(email);
                index++;
            }

        // Работа с файлом завершена, он закрыт
        try (FileOutputStream out = new FileOutputStream(file)) {
            workbook.write(out);
        }
        catch (IOException e) {
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

    private static LinkedList<String> ConvertArrayToLinkedList(String[] array){
        LinkedList<String> linkedList = new LinkedList<>();
        Collections.addAll(linkedList, array);
        return linkedList;
    }
}
