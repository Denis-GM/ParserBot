package parsers;

import models.LegalEntityInfo;

import java.util.Arrays;
import java.util.Collections;
import java.util.Stack;

public class ListorgParser {

    public LegalEntityInfo Parser(String info) {
        LegalEntityInfo legalEntityInfo = new LegalEntityInfo();

        for(var el : info.split("\\n")){
            var line = el.split(":");

            String fieldName = line[0];
            String fieldValue = line.length > 1 ? line[1].trim() : "";

            switch (fieldName){
                case "Полное юридическое наименование":
                    legalEntityInfo.setFullLegalName(fieldValue);
                    break;

                case "Руководитель":
                    String[] fullNameDirector = fieldValue.split(" ");
                    int count = fullNameDirector.length;
                    legalEntityInfo.setDirector(
                            fullNameDirector[count - 3] + " " + fullNameDirector[count - 2] + " " + fullNameDirector[count - 1]);
                    break;

                case "ИНН / КПП":
                    legalEntityInfo.setInn(fieldValue.split("/")[0].trim());
                    break;

                case "Статус":
                    legalEntityInfo.setStatus(fieldValue);
                    break;

                case "Адрес":
                    legalEntityInfo.setAddress(fieldValue);
                    break;

                case "Телефон":
                    legalEntityInfo.setPhoneNumbers(Arrays.stream(fieldValue.split(",")).toList());
                    break;

                case "E-mail":
                    legalEntityInfo.setEmails(Arrays.stream(fieldValue.split(",")).toList());
                    break;

                case "Сайт":
                    legalEntityInfo.setSites(Arrays.stream(fieldValue.split(",")).toList());
                    break;
            }
        }

        return legalEntityInfo;
    }
}
