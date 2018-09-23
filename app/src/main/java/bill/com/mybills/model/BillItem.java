package bill.com.mybills.model;

import java.io.Serializable;

public class BillItem implements Serializable {

    public String getParticulars() {
        return particulars;
    }

    public void setParticulars(String particulars) {
        this.particulars = particulars;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getGoldRate() {
        return goldRate;
    }

    public void setGoldRate(Double goldRate) {
        this.goldRate = goldRate;
    }

    public Double getAmtGold() {
        return amtGold;
    }

    public void setAmtGold(Double amtGold) {
        this.amtGold = amtGold;
    }

    public Double getMakingCharge() {
        return makingCharge;
    }

    public void setMakingCharge(Double makingCharge) {
        this.makingCharge = makingCharge;
    }

    private String particulars;
    private Double weight;
    private Double goldRate;
    private Double amtGold;
    private Double makingCharge;

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    private String phoneNo;

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    private String billNo;

    public String getItemUri() {
        return itemUri;
    }

    public void setItemUri(String itemUri) {
        this.itemUri = itemUri;
    }

    private String itemUri;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private String customerName;
    private String date;

    public BillItem() {

    }
}
