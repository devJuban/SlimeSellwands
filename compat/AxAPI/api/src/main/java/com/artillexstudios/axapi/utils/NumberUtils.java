package com.artillexstudios.axapi.utils;

public class NumberUtils {

    public static boolean isDouble(String value) {
        if (value == null) return false;
        try {
            Double.parseDouble(value);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    public static boolean isInt(String value) {
        if (value == null) return false;
        try {
            Integer.parseInt(value);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }
}
