package ch.uzh.marugoto.core.helpers;

import java.util.Arrays;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringHelper {
    private static final int URL_MAX_LENGTH = 50;
    private static final Pattern URL_MATCH_PATTERN = Pattern.compile(
            "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
                    + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
                    + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

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
     * Removes special characters from string
     *
     * @param inputString
     * @return
     */
    public static String removeSpecialCharartersFromString(String inputString) {
        
    	inputString = inputString.replace(" ", "-");
    	return inputString.replaceAll("[^a-zA-Z0-9_-]", "");
    }
    

    /**
     * Get the Enum values by name
     * @param e class
     * @return
     */
    public static String[] getEnumValues(Class<? extends Enum<?>> e) {
        return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
    }

    /**
     * Generates random string
     *
     * @param length
     * @return
     */
    public static String generateRandomString(int length) {
        char[] letters = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'w', 'y', 'x', 'y', 'z'};
        StringBuilder generatedString = new StringBuilder();
        int count = 0;

        while (count < length) {
            int randomIndex = (new Random()).nextInt(letters.length);
            generatedString.append(letters[randomIndex]);
            count++;
        }

        return generatedString.toString();
    }

    public static String shortenString(String stringToCheck, int maxLength, String suffix) {
        StringBuilder stringBuilder = new StringBuilder();

        if (stringToCheck.length() < maxLength) {
            stringBuilder.append(stringToCheck);
        } else {
            stringBuilder.append(stringToCheck.substring(0, maxLength));
            if (suffix != null) {
            	if (suffix.isEmpty() == false) {
                    stringBuilder.append(suffix);
                }
            }
        }

        return stringBuilder.toString();
    }

    public static String replaceUrlsToMarkdownLinks(String markdownText) {
        Matcher matcher = StringHelper.URL_MATCH_PATTERN.matcher(markdownText);
        StringBuffer buffer = new StringBuffer();

        while(matcher.find()) {
            int matchStart = matcher.start(1);
            int matchEnd = matcher.end();
            String link = markdownText.substring(matchStart, matchEnd);
            // shorten link label if it's too long
            int length = matchEnd - matchStart;
            if (length > URL_MAX_LENGTH) {
                String shortenLink = StringHelper.shortenString(link, URL_MAX_LENGTH, "...");
                matcher.appendReplacement(buffer, "[" + shortenLink + "](" + link + ")");
            } else {
                matcher.appendReplacement(buffer, "[" + link + "](" + link + ")");
            }
        }

        matcher.appendTail(buffer);
        return buffer.toString();
    }
}
