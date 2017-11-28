package com.github.alexeylisyutenko.windowsserviceplugin

/**
 * Useful helper methods.
 */
class Utils {

    /**
     * The line separator for Windows.
     */
    public static final String WINDOWS_LINE_SEPARATOR = '\r\n'

    /**
     * Returns a reader for a particular resource.
     */
    static Reader getResourceReader(Class<?> clazz, String resource) {
        new InputStreamReader(clazz.getClassLoader().getResourceAsStream(resource))
    }

    /**
     * Converts all line separators in the specified string to the windows line separator.
     */
    static String convertLineSeparatorsToWindows(String str) {
        str == null ? null : str.replaceAll(/\r\n|\r|\n/, WINDOWS_LINE_SEPARATOR)
    }

}
