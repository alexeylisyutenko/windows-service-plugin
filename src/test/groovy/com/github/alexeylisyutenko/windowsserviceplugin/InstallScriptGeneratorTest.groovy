package com.github.alexeylisyutenko.windowsserviceplugin

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import com.github.alexeylisyutenko.windowsserviceplugin.script.InstallScriptGenerator

Project project = ProjectBuilder.builder().build()



WindowsServicePluginConfiguration configuration = new WindowsServicePluginConfiguration()

configuration.architecture = 'AMD64'

configuration.displayName = 'TestService'
configuration.description = 'Service generated with using gradle plugin'

configuration.startClass = 'ru.icbcom.Main'
configuration.startMethod = 'main'
configuration.startParams = 'start'

configuration.stopClass = 'ru.icbcom.Main'
configuration.stopMethod = 'main'
configuration.stopParams = 'stop'


InstallScriptGenerator test = new InstallScriptGenerator('TestApplication', project.fileTree('.'), configuration, new File("."))
test.generate()