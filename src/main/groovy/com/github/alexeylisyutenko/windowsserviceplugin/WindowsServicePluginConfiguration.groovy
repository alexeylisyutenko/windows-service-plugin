package com.github.alexeylisyutenko.windowsserviceplugin

import groovy.transform.CompileStatic
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional

/**
 * Class which contains all settings needed for creating a windows service.
 */
@CompileStatic
class WindowsServicePluginConfiguration {

    /**
     * An output directory where results will be placed.
     */
    @Input
    String outputDir = 'windows-service'

    /**
     * Service executable architecture.
     */
    @Input
    Architecture architecture = Architecture.AMD64

    /**
     * A service description.
     */
    @Input
    @Optional
    String description

    /**
     * Service display name.
     */
    @Input
    String displayName

    /**
     * A class name that contains the startup method.
     */
    @Input
    String startClass

    /**
     * A name of method to be called when a service is started.
     */
    @Input
    String startMethod

    /**
     * A list of parameters that will be passed to StartClass.
     */
    @Input
    @Optional
    String startParams

    /**
     * A class name that will be used on Stop service signal.
     */
    @Input
    String stopClass

    /**
     * A name of method to be called when service is stopped.
     */
    @Input
    String stopMethod

    /**
     * A list of parameters that will be passed to StopClass.
     */
    @Input
    @Optional
    String stopParams

    /**
     * A startup mode for a service.
     */
    @Input
    Startup startup = Startup.MANUAL

    /**
     * Service type can be interactive to allow the service to interact with the desktop.
     */
    @Input
    @Optional
    Boolean interactive

    /**
     * List of services that this service depends on. Dependent services are separated using either # or ; characters
     */
    @Input
    @Optional
    String dependsOn

    /**
     * List of environment variables that will be provided to the service in the form key=value. They are separated
     * using either # or ; characters. If you need to embed either # or ; character within a value put them inside
     * single quotes.
     */
    @Input
    @Optional
    String environment

    /**
     * Directory added to the search path used to locate the DLLs for the JVM. This directory is added both in front of
     * the PATH environment variable and as a parameter to the SetDLLDirectory function.
     */
    @Input
    @Optional
    String libraryPath

    /**
     * Set a different JAVA_HOME than defined by JAVA_HOME environment variable.
     */
    @Input
    @Optional
    String javaHome

    /**
     * Use either auto (i.e. find the JVM from the Windows registry) or specify the full path to the jvm.dll.
     * You can use environment variable expansion here.
     */
    @Input
    @Optional
    String jvm = 'auto'

    /**
     * List of options in the form of -D or -X that will be passed to the JVM. The options are separated using
     * either # or ; characters. if you need to embed either # or ; character put them inside single quotes.
     */
    @Input
    @Optional
    String jvmOptions

    /**
     * Initial memory pool size in MB.
     */
    @Input
    @Optional
    Integer jvmMs

    /**
     * Maximum memory pool size in MB.
     */
    @Input
    @Optional
    Integer jvmMx

    /**
     * Thread stack size in KB.
     */
    @Input
    @Optional
    Integer jvmSs

    /**
     * Defines the timeout in seconds that procrun waits for service to exit gracefully.
     */
    @Input
    @Optional
    Integer stopTimeout

    /**
     * Defines the path for logging. Creates the directory if necessary.
     */
    @Input
    @Optional
    String logPath

    /**
     * Defines the service log filename prefix. The log file is created in the LogPath directory with
     * .YEAR-MONTH-DAY.log suffix.
     */
    @Input
    @Optional
    String logPrefix

    /**
     * Defines the logging level and can be either Error, Info, Warn or Debug.
     */
    @Input
    @Optional
    LogLevel logLevel

    /**
     * Set this non-zero (e.g. 1) to capture JVM jni debug messages in the procrun log file.
     * Is not needed if stdout/stderr redirection is being used.
     */
    @Input
    @Optional
    Integer logJniMessages

    /**
     * Redirected stdout filename. If named auto file is created inside LogPath with the name
     * service-stdout.YEAR-MONTH-DAY.log.
     */
    @Input
    @Optional
    String stdOutput

    /**
     * Redirected stderr filename. If named auto file is created in the LogPath directory with the name
     * service-stderr.YEAR-MONTH-DAY.log.
     */
    @Input
    @Optional
    String stdError

    /**
     * Defines the file name for storing the running process id. Actual file is created in the LogPath directory.
     */
    @Input
    @Optional
    String pidFile

    /**
     * Use this property to override the classpath if the default classpath does not provide the results you want.
     */
    @InputFiles
    @Optional
    FileCollection overridingClasspath

}
