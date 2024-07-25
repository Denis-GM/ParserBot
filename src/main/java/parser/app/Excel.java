package parser.app;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import parser.models.BusinessInfo;
import parser.models.BusinessInfoSearch;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import parser.models.ExcelParseNamePerson;
import parser.models.Person;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;

public class Excel {
    private static String file = "";
    private static FileInputStream fileExcel;
    private static XSSFWorkbook workbook;
    private static XSSFSheet sheet;
    private static XSSFCellStyle style;

    public Excel(String file) {
        try {
            this.file = file;
            fileExcel = new FileInputStream(file);
            workbook = new XSSFWorkbook(fileExcel);
            sheet = workbook.getSheetAt(0);

            for(int i = 7; i < 21; i++){
                sheet.setColumnWidth(i,10000);
            }

            style = workbook.createCellStyle();
            style.setAlignment(XSSFCellStyle.ALIGN_LEFT);
            style.setVerticalAlignment(XSSFCellStyle.VERTICAL_TOP);
            style.setWrapText(true);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public static void readFromExcel() throws IOException {
        try {
            int startIndex = 2;
            int counter = 0;
            for(int i = GetCountRow(); i >= startIndex; i--){
                if(sheet.getRow(i).getCell(6) != null && sheet.getRow(i).getCell(6).toString() != ""){
                    startIndex = i + 1;
                    break;
                }
            }
            for (Row row : sheet) {
                counter++;
                if(counter < startIndex){
                    continue;
                }
                Application.UpdateProgress(counter);
                var infoSearch = ParseRow(row);
                System.out.println(counter);
                System.out.println(infoSearch.getFullName());

                WebBot webBot = new WebBot();
                BusinessInfo info = webBot.StartSearch(infoSearch);
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
        Cell cell_7 = row.createCell(6);
        cell_7.setCellStyle(style);
        cell_7.setCellValue(businessInfo.getInn());

        Cell cell_8 = row.createCell(7);
        cell_8.setCellStyle(style);
        cell_8.setCellValue(businessInfo.getStatus());

        Cell cell_9 = row.createCell(8);
        cell_9.setCellStyle(style);
        if(!businessInfo.isEmpty()){
            String innExists = (businessInfo.getInn() != null) ? "Есть" : "Нет";
            cell_9.setCellValue(innExists);
        }

        Cell cell_10 = row.createCell(9);
        cell_10.setCellStyle(style);
        cell_10.setCellValue(businessInfo.getDirector());

        Cell cell_11 = row.createCell(10);
        cell_11.setCellStyle(style);
        cell_11.setCellValue(businessInfo.getAddress());

        int index = 12;
        if(businessInfo.getPhoneNumbers() != null)
            for(var phoneNumber : businessInfo.getPhoneNumbers()) {
                if(index > 16) break;
                Cell cellIndex = row.createCell(index);
                cellIndex.setCellStyle(style);
                cellIndex.setCellValue(phoneNumber);
                index++;
            }

        index = 17;
        if(businessInfo.getEmails() != null)
            for(var email : businessInfo.getEmails()) {
                if(index > 18) break;
                Cell cellIndex = row.createCell(index);
                cellIndex.setCellStyle(style);
                cellIndex.setCellValue(email);
                index++;
            }

        index = 19;
        if(businessInfo.getSites() != null)
            for(var Site : businessInfo.getSites()) {
                if(index > 20) break;
                Cell cellIndex = row.createCell(index);
                cellIndex.setCellStyle(style);
                cellIndex.setCellValue(Site);
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
                if(i == 0){
                    String fullNamePerson = fourthCellArray.get(i).trim();
                    ExcelParseNamePerson excelParseNamePerson = CreateNamePerson(fullNamePerson);
                    infoSearch.setShortName(excelParseNamePerson.getShortName());
                    infoSearch.setFullName(excelParseNamePerson.getFullName());
                    infoSearch.setType(excelParseNamePerson.getType());
                    infoSearch.setTypePerson(excelParseNamePerson.getTypePerson());
                }
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
            infoSearch.setFullName(fourthCellArray.getFirst());

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

    private static ExcelParseNamePerson CreateNamePerson(String fullName){
        ExcelParseNamePerson excelParseNamePerson = new ExcelParseNamePerson();
        excelParseNamePerson.setFullName(fullName);
        int index = fullName.indexOf('"');
        if(index != -1){
            int startIndex = index != -1 ? index + 1 : 0;
            excelParseNamePerson.setShortName(fullName.substring(startIndex, fullName.length() - 1));
            excelParseNamePerson.setTypePerson(Person.Juristic);
            excelParseNamePerson.setType(fullName.substring(0, startIndex - 1));
        }
        else {
            excelParseNamePerson.setShortName(fullName);
            excelParseNamePerson.setTypePerson(Person.Physical);
            excelParseNamePerson.setType("Индивидуальный предприниматель");
        }
        return excelParseNamePerson;
    }

    private static LinkedList<String> ConvertArrayToLinkedList(String[] array){
        LinkedList<String> linkedList = new LinkedList<>();
        Collections.addAll(linkedList, array);
        return linkedList;
    }

    public static int GetCountRow(){
        return sheet.getLastRowNum();
    }
}
