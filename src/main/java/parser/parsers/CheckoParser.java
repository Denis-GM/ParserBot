package parser.parsers;

import parser.models.BusinessInfo;

import java.util.Arrays;

public class CheckoParser {
    public BusinessInfo Parser(String info) {
        BusinessInfo businessInfo = new BusinessInfo();

        for(var el : info.split("\\n")){
            var line = el.split(" ");

            String fieldName = line[0];
            String fieldValue = line.length > 1 ? line[1].trim() : "";

            switch (fieldName){
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
                    if(line.length > 2){
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 1; i < line.length; i++)
                            stringBuilder.append(line[i]);
                        String value = stringBuilder.toString().trim();
                        businessInfo.setSites(Arrays.stream(value.split(",")).toList());
                    }
                    else
                        businessInfo.setSites(Arrays.stream(fieldValue.split(",")).toList());
                    break;

                case "ФИО":
                    businessInfo.setFullName(fieldValue);
                    break;
                case "ИНН":
                    businessInfo.setInn(fieldValue);
                    break;
            }
        }

        return businessInfo;
    }
}
