package ch.uzh.marugoto.shell.helpers;

import java.util.Arrays;

public class StringHelper {

    public static boolean stringContains(String inputStr, String[] items) {
        return Arrays.stream(items).parallel().anyMatch(inputStr::contains);
    }
}
