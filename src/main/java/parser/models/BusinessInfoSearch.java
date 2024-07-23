package parser.models;

public class BusinessInfoSearch {
    private String name;
    private String index;
    private String address;

    private String trademarkName;
    private String trademarkNumber;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
