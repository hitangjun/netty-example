package io.netty.example.serialize;

import java.io.Serializable;

/**
 * @author JohnTang
 * @date 2017/2/7
 */
public class SubscribeReq implements Serializable{

    private static final long serialVersionUID = 1L;
    private int subReqID;
    private String userName;
    private String productName;
    private String phoneName;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    private String phoneNumber;
    private String address;


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getSubReqID() {
        return subReqID;
    }

    public void setSubReqID(int subReqID) {
        this.subReqID = subReqID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getPhoneName() {
        return phoneName;
    }

    public void setPhoneName(String phoneName) {
        this.phoneName = phoneName;
    }

    public String toString(){
        return "SubscribeReq [subReqID=" + subReqID +", userName="+
                userName +",productName="+productName+",phoneNumber="
                +phoneNumber+",address="+address+"]";
    }
}
