package com.example.utils;

public class TextFieldFilters {
    public static String formatTextToFloat(String oldValue) {
        return oldValue.replaceAll("[^\\.\\d]", "");
    }

    public static String formatTextToInt(String oldValue) {
        return oldValue.replaceAll("[^\\d]", "");
    }
}
