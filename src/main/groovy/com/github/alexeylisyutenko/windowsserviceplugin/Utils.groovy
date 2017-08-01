package com.github.alexeylisyutenko.windowsserviceplugin

/**
 * Useful helper methods.
 *
 * Created by Алексей Лисютенко on 17.05.2017.
 */
class Utils {

    /**
     * Returns a reader for a particular resource.
     */
    static Reader getResourceReader(Class<?> clazz, String resource) {
        new InputStreamReader(clazz.getClassLoader().getResourceAsStream(resource))
    }

}
