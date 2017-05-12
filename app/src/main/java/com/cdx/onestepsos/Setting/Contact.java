package com.cdx.onestepsos.Setting;

/**
 * Created by CDX on 2017/4/8.
 */

public class Contact {
    private String name;
    private String mobile;
    public Contact(String name ,String mobile){
        this.name = name;
        this.mobile = mobile;
    }
    public String getName() {
        return name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

}
