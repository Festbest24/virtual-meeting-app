package com.webilestudio.vmeet.bean;

import java.io.Serializable;

public class UserBean implements Serializable {

    String id;
    String name;
    String email;
    String profile_pic;
    String device_type;
    String device_id;

  /*  - device_type // a or i (string)
- device_id   // (string)
- plan_id
- plan_name
- user_type // Free/Paid*/

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public String getDevice_type() {
        return device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }
}
