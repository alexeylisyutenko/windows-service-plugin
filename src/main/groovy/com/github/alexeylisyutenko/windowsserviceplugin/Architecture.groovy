package com.github.alexeylisyutenko.windowsserviceplugin

/**
 * An enumeration which expresses an architecture of an executable file used in resulting distribution.
 */
enum Architecture {

    /**
     * 32-bit (x86) architecture.
     */
    X86,

    /**
     * AMD/EMT 64-bit architecture.
     */
    AMD64,

    /**
     * Intel Itanium 64-bit architecture.
     *
     * @deprecated There is no precompiled Intel Itanium 64-bit executable of Procrun in a maven artifact
     * since a version 1.1.0 of Apache Commons Daemon.
     */
    @Deprecated IA64

}
