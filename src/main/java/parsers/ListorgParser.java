package parsers;

import models.BusinessInfo;

import java.util.Arrays;

public class ListorgParser {

    public BusinessInfo Parser(String info) {
        BusinessInfo businessInfo = new BusinessInfo();

        for(var el : info.split("\\n")){
            var line = el.split(":");

            String fieldName = line[0];
            String fieldValue = line.length > 1 ? line[1].trim() : "";

            switch (fieldName){
                case "Полное юридическое наименование":
                    businessInfo.setFullLegalName(fieldValue);
                    break;

                case "Руководитель":
                    String[] fullNameDirector = fieldValue.split(" ");
                    int count = fullNameDirector.length;
                    businessInfo.setDirector(
                            fullNameDirector[count - 3] + " " + fullNameDirector[count - 2] + " " + fullNameDirector[count - 1]);
                    break;

                case "ИНН / КПП":
                    businessInfo.setInn(fieldValue.split("/")[0].trim());
                    break;

                case "Статус":
                    businessInfo.setStatus(fieldValue);
                    break;

                case "Адрес":
                    businessInfo.setAddress(fieldValue);
                    break;

                case "Телефон":
                    businessInfo.setPhoneNumbers(Arrays.stream(fieldValue.split(",")).toList());
                    break;

                case "E-mail":
                    businessInfo.setEmails(Arrays.stream(fieldValue.split(",")).toList());
                    break;

                case "Сайт":
                    businessInfo.setSites(Arrays.stream(fieldValue.split(",")).toList());
                    break;
            }
        }

        return businessInfo;
    }
}
