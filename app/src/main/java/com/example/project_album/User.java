package com.example.project_album;

import java.util.Map;

public class User {
    private String username;
    private String password;
    private  String email;
    private String phone;
    private String key;
    private String hidepass;
    public User(){

    }
    public User(String username, String pass, String email, String phone)
    {
        this.username=username;
        this.email=email;
        this.password=pass;
        this.phone=phone;
    }
    public User(Map<String,Object> map){
        username = map.get("username").toString();
        password = map.get("password").toString();
        email = map.get("email").toString();
        phone = map.get("phone").toString();
        hidepass = map.get("hidepass").toString();
    }
    public String getHidepass() {
        return hidepass;
    }

    public void setHidepass(String hidepass) {
        this.hidepass = hidepass;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setPass(String pass){
        this.password = pass;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public void setPhone(String phone){
        this.phone = phone;
    }
    public String getUsername(){return username;}
    public String getPassword(){return  password;}
    public String getEmail(){return email;}
    public String getPhone(){return phone;}
    public void setKey(String key){
        this.key =key;
    }
    public String getKey(){
        return key;
    }
}
