package com.github.alexeylisyutenko.windowsserviceplugin.script

import com.github.alexeylisyutenko.windowsserviceplugin.Utils
import com.github.alexeylisyutenko.windowsserviceplugin.WindowsServicePluginConfiguration
import groovy.text.SimpleTemplateEngine
import groovy.text.Template
import org.gradle.api.file.FileCollection

import java.util.stream.Collectors

/**
 * A class which generates batch script for installing a service.
 */
class InstallScriptGenerator {

    private String applicationName
    private FileCollection classpath
    private WindowsServicePluginConfiguration configuration
    private File outputDirectory

    InstallScriptGenerator(String applicationName, FileCollection classpath, WindowsServicePluginConfiguration configuration, File outputDirectory) {
        this.applicationName = applicationName
        this.classpath = classpath
        this.configuration = configuration
        this.outputDirectory = outputDirectory
    }

    void generate() {
        def binding = [
                applicationName: applicationName,
                serviceExeName : applicationName + ".exe",
                classpath      : JoinedClasspathBuilder.build(classpath),
                installOptions : InstallOptionsBuilder.build(configuration)
        ]
        generateOutputFor(binding)
    }

    private void generateOutputFor(Map<String, String> binding) {
        def reader = Utils.getResourceReader(this.getClass(), "installScript.txt")
        def engine = new SimpleTemplateEngine()
        Template template = engine.createTemplate(reader)

        new File(outputDirectory, "${applicationName}-install.bat").withWriter { writer ->
            String output = template.make(binding).toString()
            writer.write(Utils.convertLineSeparatorsToWindows(output))
        }
    }

    private static class JoinedClasspathBuilder {
        static String build(FileCollection classpath) {
            classpath.files
                    .collect { file -> '%APP_HOME%lib\\' + file.getName() }
                    .join(';')
        }
    }

    private static class InstallOptionsBuilder {

        static String build(WindowsServicePluginConfiguration configuration) {
            Map<String, String> options = createInstallOptionsMapFor(configuration)
            options.entrySet().stream()
                    .filter { it.value != null }
                    .map { it.key + '=' + addQuotesIfNeeded(it.value) }
                    .collect(Collectors.joining(' ^\r\n    ', '^\r\n    ', ''))
        }

        private static Map<String, String> createInstallOptionsMapFor(WindowsServicePluginConfiguration configuration) {
            def options = [
                    "--Classpath"     : "%CLASSPATH%",
                    "--Description"   : configuration.description,
                    "--DisplayName"   : configuration.displayName,
                    "--StartClass"    : configuration.startClass,
                    "--StartMethod"   : configuration.startMethod,
                    "++StartParams"   : configuration.startParams,
                    "--StartMode"     : "jvm",
                    "--StopClass"     : configuration.stopClass,
                    "--StopMethod"    : configuration.stopMethod,
                    "++StopParams"    : configuration.stopParams,
                    "--StopMode"      : "jvm",
                    "--Jvm"           : toWindowsPath(configuration.jvm),
                    "--Startup"       : configuration.startup.name().toLowerCase(),
                    "--Type"          : configuration.interactive ? "interactive" : null,
                    "++DependsOn"     : configuration.dependsOn,
                    "++Environment"   : configuration.environment,
                    "--LibraryPath"   : toWindowsPath(configuration.libraryPath),
                    "--JavaHome"      : toWindowsPath(configuration.javaHome),
                    "++JvmOptions"    : configuration.jvmOptions,
                    "--JvmMs"         : configuration.jvmMs?.toString(),
                    "--JvmMx"         : configuration.jvmMx?.toString(),
                    "--JvmSs"         : configuration.jvmSs?.toString(),
                    "--StopTimeout"   : configuration.stopTimeout?.toString(),
                    "--LogPath"       : toWindowsPath(configuration.logPath),
                    "--LogPrefix"     : configuration.logPrefix,
                    "--LogLevel"      : configuration.logLevel?.name()?.toLowerCase()?.capitalize(),
                    "--LogJniMessages": configuration.logJniMessages?.toString(),
                    "--StdOutput"     : configuration.stdOutput,
                    "--StdError"      : configuration.stdError,
                    "--PidFile"       : configuration.pidFile
            ]
            options
        }

        private static String toWindowsPath(String path) {
            path?.replace('/', '\\')
        }

        private static Object addQuotesIfNeeded(String value) {
            value.contains(" ") ? "\"" + value + "\"" : value
        }

    }

}