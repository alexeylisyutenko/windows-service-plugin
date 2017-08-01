package com.github.alexeylisyutenko.windowsserviceplugin.script

import groovy.text.SimpleTemplateEngine
import groovy.text.Template
import org.gradle.api.file.FileCollection
import com.github.alexeylisyutenko.windowsserviceplugin.Utils
import com.github.alexeylisyutenko.windowsserviceplugin.WindowsServicePluginConfiguration

import java.util.stream.Collectors

/**
 * A class which generates batch script for installing a service.
 *
 * Created by Алексей Лисютенко on 10.03.2017.
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
                classpath      : createJoinedClasspath(),
                installOptions : createInstallOptions()
        ]
        generateOutputFor(binding)
    }

    private void generateOutputFor(Map<String, String> binding) {
        def reader = Utils.getResourceReader(this.getClass(), "installScript.txt")
        def engine = new SimpleTemplateEngine()
        Template template = engine.createTemplate(reader)
        Writer writer = new FileWriter(new File(outputDirectory, "${applicationName}-install.bat"))
        template.make(binding).writeTo(writer)
    }

    private Map createInstallOptionsMapFor(WindowsServicePluginConfiguration configuration) {
        def options = [
                "--Classpath"  : "%CLASSPATH%",
                "--Description": configuration.description,
                "--DisplayName": configuration.displayName,
                "--StartClass" : configuration.startClass,
                "--StartMethod": configuration.startMethod,
                "++StartParams": configuration.startParams,
                "--StartMode"  : "jvm",
                "--StopClass"  : configuration.stopClass,
                "--StopMethod" : configuration.stopMethod,
                "++StopParams" : configuration.stopParams,
                "--StopMode"   : "jvm",
                "--Jvm"        : "auto",
                "--Startup"    : configuration.startup.name().toLowerCase()
        ]
        options
    }

    private String createInstallOptions() {
        Map options = createInstallOptionsMapFor(configuration)
        options.entrySet().stream()
                .filter { it.value != null }
                .map { it.key + '=' + addQuotesIfNeeded(it.value) }
                .collect(Collectors.joining(' ^\n    ', '^\n    ', ''))
    }

    private Object addQuotesIfNeeded(String value) {
        value.contains(" ") ? "\"" + value + "\"" : value
    }

    private String createJoinedClasspath() {
        classpath.files
                .collect { file -> '%APP_HOME%lib\\' + file.getName() }
                .join(';')
    }

}
