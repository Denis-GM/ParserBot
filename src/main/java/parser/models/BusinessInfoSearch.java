package parser.models;

public class BusinessInfoSearch {
    private String fullName;
    private String shortName;

    private String type;
    private Person typePerson;

    private String index;
    private String address;

    private String trademarkName;
    private String trademarkNumber;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTrademarkName() {
        return trademarkName;
    }

    public void setTrademarkName(String trademarkName) {
        this.trademarkName = trademarkName;
    }

    public String getTrademarkNumber() {
        return trademarkNumber;
    }

    public void setTrademarkNumber(String trademarkNumber) {
        this.trademarkNumber = trademarkNumber;
    }

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
}
