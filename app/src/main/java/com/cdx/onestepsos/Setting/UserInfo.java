package com.cdx.onestepsos.Setting;

/**
 * Created by CDX on 2017/5/4.
 */

public class UserInfo {
    String mobile;
    String name;
    String age;
    String gender;
    String default_location;
    String medical_history;

    public UserInfo(String mobile, String name, String age, String gender, String default_location, String medical_history) {
        this.mobile = mobile;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.default_location = default_location;
        this.medical_history = medical_history;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDefault_location() {
        return default_location;
    }

    public void setDefault_location(String default_location) {
        this.default_location = default_location;
    }

    public String getMedical_history() {
        return medical_history;
    }

    public void setMedical_history(String medical_history) {
        this.medical_history = medical_history;
    }
}
