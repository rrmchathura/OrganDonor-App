package com.rrmchathura.organdonor_app.Model;

public class Users {

    private String fullName,email,bloodGroup,id,mobile,userID,userType,password;

    public Users() {
    }

    public Users(String fullName, String email, String bloodGroup, String id, String mobile, String userID, String userType,String password) {
        this.fullName = fullName;
        this.email = email;
        this.bloodGroup = bloodGroup;
        this.id = id;
        this.mobile = mobile;
        this.userID = userID;
        this.userType = userType;
        this.password = password;
    }

    public Users(String fullName, String bloodGroup, String mobile) {
        this.fullName = fullName;
        this.bloodGroup = bloodGroup;
        this.mobile = mobile;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
