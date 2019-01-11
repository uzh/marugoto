package ch.uzh.marugoto.core.helpers;

import java.util.Arrays;

public class StringHelper {

    /**
     * Check if array of strings contain String
     * @param inputStr
     * @param String [] items
     * @return boolean
     */
    public static boolean stringContains(String inputStr, String[] items) {
        return Arrays.stream(items).parallel().anyMatch(inputStr::contains);
    }

    /**
     * Get the Enum values by name
     * @param Enum class
     * @return
     */
    public static String[] getEnumValues(Class<? extends Enum<?>> e) {
        return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
    }
}
