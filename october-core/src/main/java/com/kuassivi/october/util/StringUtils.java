package com.kuassivi.october.util;

import java.util.Locale;

/**
 * String utilities.
 */
public class StringUtils {

    /**
     * Any given String uncapitalized.
     *
     * @param string String to be uncapitalized
     * @return The new uncapitalized string
     */
    public static String uncapitalize(String string) {
        if (string == null || string.length() == 0) {
            return string;
        }
        char c[] = string.toCharArray();
        c[0] = String.valueOf(c[0]).toLowerCase(Locale.getDefault()).charAt(0);
        return new String(c);
    }
}
