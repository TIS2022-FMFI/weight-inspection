package com.example.utils;

public class TextFieldFilters {
    public static String formatTextToFloat(String oldValue) {
        System.out.println(oldValue);
        return oldValue.replaceAll("[^\\.\\d]", "");
    }

    public static String formatTextToInt(String oldValue) {
        System.out.println(oldValue);
        return oldValue.replaceAll("[^\\d]", "");
    }
}
