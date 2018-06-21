package bill.com.mybills.model;

import java.io.Serializable;

public class BusinessProfile implements Serializable{

    public String phone;
    public String orgName;
    public String gstIN;
    public String address;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getGstIN() {
        return gstIN;
    }

    public void setGstIN(String gstIN) {
        this.gstIN = gstIN;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BusinessProfile(){

    }


    public BusinessProfile(String phone,String orgName,String gstIN,String address){
        this.phone = phone;
        this.orgName = orgName;
        this.gstIN = gstIN;
        this.address = address;


    }

}
