package com.github.alexeylisyutenko.windowsserviceplugin

import groovy.transform.CompileStatic
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
/**
 * Class which contains all settings needed for creating a windows service.
 *
 * Created by Алексей Лисютенко on 28.02.2017.
 */
@CompileStatic
class WindowsServicePluginConfiguration {

    /**
     * An output directory where results will be placed.
     */
    @Input
    String outputDir= 'windows-service'

    /**
     * Service executable architecture.
     */
    @Input
    Architecture architecture = Architecture.X86

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

}
