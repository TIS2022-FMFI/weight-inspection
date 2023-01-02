package com.example.utils;

public class NumericTextField {
    public static String fortmatFloatText(String oldValue) {
        System.out.println(oldValue);
        return oldValue.replaceAll("[^\\.\\d]", "");
    }

}
