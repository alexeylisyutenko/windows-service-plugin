package com.github.alexeylisyutenko.windowsserviceplugin.script

import com.github.alexeylisyutenko.windowsserviceplugin.Utils
import groovy.text.SimpleTemplateEngine
import groovy.text.Template

/**
 * A class which generates batch script for uninstalling a service.
 */
class UninstallScriptGenerator {

    private String applicationName
    private File outputDirectory

    UninstallScriptGenerator(String applicationName, File outputDirectory) {
        this.applicationName = applicationName
        this.outputDirectory = outputDirectory
    }

    void generate() {
        def binding = [
                applicationName : applicationName,
                serviceExeName : applicationName + ".exe"
        ]
        generateOutputFor(binding)
    }

    private void generateOutputFor(Map<String, String> binding) {
        def reader = Utils.getResourceReader(this.getClass(), "uninstallScript.txt")
        def engine = new SimpleTemplateEngine()
        Template template = engine.createTemplate(reader)

        new File(outputDirectory, "${applicationName}-uninstall.bat").withWriter { writer ->
            String output = template.make(binding).toString()
            writer.write(Utils.convertLineSeparatorsToWindows(output))
        }
    }

}
