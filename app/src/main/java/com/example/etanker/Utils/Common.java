package com.example.etanker.Utils;

public class Common {

    public static final String User="Users";
    public static final String Email="email";
    public static final String phone="phone";

    public static final String Consumers="customers";
    public static final String Suppliers="suppliers";
    public static final String Name="name";
    public static final String Tanker="tankers";


    public static String convertCodeToStatus(String code){
        if(code.equals("0"))
            return "available";
        else
            return "currently busy";
    }



}
