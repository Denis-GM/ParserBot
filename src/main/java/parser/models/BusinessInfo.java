package parser.models;

import java.util.ArrayList;
import java.util.List;

public class BusinessInfo {
    private String fullName;
    private String name;
    private String director;
    private String inn;
    private String status;
    private String address;
    private List<String> phoneNumbers;
    private List<String> emails;
    private List<String> sites;

    public BusinessInfo(){ }

    public BusinessInfo(String inn, String status){
        this.inn = inn;
        this.status = status;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

//    public List<String> getAddresses() {
//        return addresses;
//    }
//
//    public void setAddresses(List<String> addresses) {
//        this.addresses = addresses;
//    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    public List<String> getSites() {
        return sites;
    }

    public void setSites(List<String> sites) {
        this.sites = sites;
    }

    public boolean isEmpty(){
        boolean con = fullName == null && director == null && inn == null &&
                status == null && address == null && phoneNumbers == null &&
                emails == null && sites == null;
        return con;
    }
}
