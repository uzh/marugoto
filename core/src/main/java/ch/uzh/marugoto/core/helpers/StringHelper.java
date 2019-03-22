package ch.uzh.marugoto.core.helpers;

import java.util.Arrays;
import java.util.Random;

public class StringHelper {

    public static String replaceInText(String text, String textToReplace, String replacement) {
        return text.replace(textToReplace, replacement);
    }

    /**
     * Check if array of strings contain String
     * @param inputStr
     * @param items
     * @return boolean
     */
    public static boolean stringContains(String inputStr, String[] items) {
        return Arrays.stream(items).parallel().anyMatch(inputStr::contains);
    }

    /**
     * Removes numbers from string
     *
     * @param inputString
     * @return
     */
    public static String removeNumbers(String inputString) {
        return inputString.replaceAll("\\d", "");
    }

    /**
     * Get the Enum values by name
     * @param Enum class
     * @return
     */
    public static String[] getEnumValues(Class<? extends Enum<?>> e) {
        return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
    }

    public static String generateRandomString(int length) {
        char[] letters = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'w', 'y', 'x', 'y', 'z'};
        StringBuilder generatedString = new StringBuilder();
        int count = 0;

        while (count < length) {
            count++;
            int randomIndex = (new Random()).nextInt(letters.length);
            generatedString.append(letters[randomIndex]);
        }

        return generatedString.toString();
    }
}
