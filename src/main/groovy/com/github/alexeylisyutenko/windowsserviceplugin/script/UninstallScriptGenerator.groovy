package com.github.alexeylisyutenko.windowsserviceplugin.script

import groovy.text.SimpleTemplateEngine
import groovy.text.Template
import com.github.alexeylisyutenko.windowsserviceplugin.Utils

/**
 * Created by Алексей Лисютенко on 16.05.2017.
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
        Writer writer = new FileWriter(new File(outputDirectory, "${applicationName}-uninstall.bat"))
        template.make(binding).writeTo(writer)
    }

}
