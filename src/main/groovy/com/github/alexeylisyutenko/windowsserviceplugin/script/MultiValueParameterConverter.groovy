package com.github.alexeylisyutenko.windowsserviceplugin.script

/**
 * Some configuration parameters of the plugin are essentially lists or maps. For example, jvmOptions is a list of
 * options that will be passed to the JVM. Such parameters could be set in configuration as a normal String, as List or
 * as a Map, but as a Procrun argument it should be a string separated by # or ; characters.
 * This class converts all this representations to a single string separated by # or ; characters.
 */
class MultiValueParameterConverter {

    public static final String SEPARATOR = ';'

    /**
     * Converts a configuration parameter which takes lists or maps into a single string separated by # or ; characters.
     */
    static String convertToString(parameter) {
        if (isCollectionOrArray(parameter)) {
            return parameter
                    .collect { wrapSpecialCharacters(it) }
                    .join(SEPARATOR)
        } else if (isMap(parameter)) {
            return parameter
                    .collect { k, v -> "$k=$v" }
                    .collect { wrapSpecialCharacters(it) }
                    .join(SEPARATOR)
        } else {
            return parameter
        }
    }

    private static boolean isCollectionOrArray(object) {
        [Collection, Object[]].any { it.isAssignableFrom(object.getClass()) }
    }

    private static boolean isMap(object) {
        object instanceof Map
    }

    private static String wrapSpecialCharacters(parameter) {
        (parameter as String).replaceAll(/([#;])/) { all, sep -> "'$sep'" }
    }

}