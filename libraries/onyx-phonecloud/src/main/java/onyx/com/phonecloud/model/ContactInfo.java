package onyx.com.phonecloud.model;

import java.util.List;

/**
 * Created by TonyXie on 2020-02-27
 */
public class ContactInfo {
    private String contactName;
    private String sipAddress;
    private String company;
    private String workName;
    private String note;
    private List<Website> webList;
    private List<ImInfo> imInfoList;
    private List<PhoneNumber> numbers;
    private List<Email> emails;
    private List<Address> addresses;


    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public List<PhoneNumber> getNumbers() {
        return numbers;
    }

    public void setNumbers(List<PhoneNumber> numbers) {
        this.numbers = numbers;
    }

    public List<Email> getEmails() {
        return emails;
    }

    public void setEmails(List<Email> emails) {
        this.emails = emails;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCompany() {
        return company;
    }

    public void setWorkName(String workName) {
        this.workName = workName;
    }

    public String getWorkName() {
        return workName;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getNote() {
        return note;
    }

    public void setWebList(List<Website> webList) {
        this.webList = webList;
    }

    public List<Website> getWebList() {
        return webList;
    }

    public void setImInfoList(List<ImInfo> imInfoList) {
        this.imInfoList = imInfoList;
    }

    public List<ImInfo> getImInfoList() {
        return imInfoList;
    }

    public void setSipAddress(String sipAddress) {
        this.sipAddress = sipAddress;
    }

    public String getSipAddress() {
        return sipAddress;
    }
}
