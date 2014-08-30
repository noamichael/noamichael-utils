package org.noamichael.utils.se;

/**
 *
 * @author michael
 */
public class StringUtil {

    /**
     * Returns true if the {@link String} parameter is null or empty. Any white
     * space is trimmed before the empty check.
     *
     * @param s
     * @return
     */
    public static boolean isNullOrEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }
}
