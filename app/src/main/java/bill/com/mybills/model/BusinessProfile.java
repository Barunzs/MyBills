package bill.com.mybills.model;

import android.net.Uri;

import java.io.Serializable;

public class BusinessProfile implements Serializable {

    public String phone;
    public String orgName;
    public String gstIN;
    public String address;
    private Uri businessLogoURI;
    private String pincode;
    public boolean isActive;


    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }


    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public Uri getBusinessLogoURI() {
        return businessLogoURI;
    }

    public void setBusinessLogoURI(Uri businessLogoURI) {
        this.businessLogoURI = businessLogoURI;
    }

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

    public BusinessProfile() {

    }


    public BusinessProfile(String phone, String orgName, String gstIN, String address,Uri URI,String pincode,boolean isActive) {
        this.phone = phone;
        this.orgName = orgName;
        this.gstIN = gstIN;
        this.address = address;
        this.businessLogoURI = URI;
        this.pincode = pincode;
        this.isActive = isActive;
    }

}
