package com.github.alexeylisyutenko.windowsserviceplugin

import com.github.alexeylisyutenko.windowsserviceplugin.script.InstallScriptGenerator
import com.github.alexeylisyutenko.windowsserviceplugin.script.UninstallScriptGenerator
import org.apache.commons.io.FileUtils
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class ScriptGeneratorsTest extends Specification {

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder()

    private WindowsServicePluginConfiguration setupBasicWindowsServicePluginConfiguration() {
        WindowsServicePluginConfiguration configuration = new WindowsServicePluginConfiguration()

        configuration.architecture = 'AMD64'
        configuration.displayName = 'TestService'
        configuration.description = 'Service generated with using gradle plugin'
        configuration.startClass = 'Main'
        configuration.startMethod = 'main'
        configuration.startParams = 'start'
        configuration.stopClass = 'Main'
        configuration.stopMethod = 'main'
        configuration.stopParams = 'stop'
        configuration.startup = 'AUTO'

        configuration
    }

    private ConfigurableFileCollection setupBasicClasspathFileCollection() {
        ProjectBuilder.builder().build().files("testProject.jar")
    }

    def "InstallScriptGenerator should work properly"() {
        given:
        def configuration = setupBasicWindowsServicePluginConfiguration()
        def fileCollection = setupBasicClasspathFileCollection()
        def generator = new InstallScriptGenerator('testProject', fileCollection, configuration, temporaryFolder.root)

        when:
        generator.generate()

        then:
        new File(temporaryFolder.root, "testProject-install.bat").exists()
        compareSampleAndResultForEquality('testProject-install.bat', 'sample-testProject-install.bat')
    }

    def "UninstallScriptGenerator should work properly"() {
        given:
        def generator = new UninstallScriptGenerator('testProject', temporaryFolder.root)

        when:
        generator.generate()

        then:
        new File(temporaryFolder.root, "testProject-uninstall.bat").exists()
        compareSampleAndResultForEquality('testProject-uninstall.bat', 'sample-testProject-uninstall.bat')
    }

    void compareSampleAndResultForEquality(String generatedFilename, String sampleFilename) {
        def generatedFile = new File(temporaryFolder.root, generatedFilename)
        def resource = getClass().getClassLoader().getResource(sampleFilename)
        def sampleFile = new File(resource.toURI())

        assert FileUtils.contentEquals(generatedFile, sampleFile)
    }

}
