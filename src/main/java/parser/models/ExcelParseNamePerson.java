package parser.models;

public class ExcelParseNamePerson {
    private String type;
    private Person typePerson;

    private String shortName;
    private String fullName;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Person getTypePerson() {
        return typePerson;
    }

    public void setTypePerson(Person typePerson) {
        this.typePerson = typePerson;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
