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

    def "Some configuration parameters which contain paths should be converted to windows style paths"() {
        given:
        def configuration = setupBasicWindowsServicePluginConfiguration()
        configuration.libraryPath = './runtime/bin'
        configuration.javaHome = './runtime'
        configuration.jvm = 'C:/Program Files/Java/jdk1.8.0_112/jre/bin/server/jvm.dll'
        configuration.logPath = './procrun-logs'

        def fileCollection = setupBasicClasspathFileCollection()
        def generator = new InstallScriptGenerator('testProject', fileCollection, configuration, temporaryFolder.root)

        when:
        generator.generate()
        def installScriptLines = new File(temporaryFolder.root, "testProject-install.bat").readLines()

        then:
        installScriptLines.any { it.contains("--LibraryPath=.\\runtime\\bin") }
        installScriptLines.any { it.contains("--JavaHome=.\\runtime") }
        installScriptLines.any { it.contains("--Jvm=\"C:\\Program Files\\Java\\jdk1.8.0_112\\jre\\bin\\server\\jvm.dll\"") }
        installScriptLines.any { it.contains("--LogPath=.\\procrun-logs") }
    }

    def "All additional procrun configuration parameters should be present in a resulting install.bat file if they're set"() {
        given:
        def configuration = setupBasicWindowsServicePluginConfiguration()
        configuration.interactive = true
        configuration.dependsOn = 'AnotherWindowsService'
        configuration.environment = 'envKey1=value1;envKey2=value2;envKey3=value3'
        configuration.libraryPath = '.\\runtime\\bin'
        configuration.javaHome = '.\\runtime'
        configuration.jvm = 'C:\\Program Files\\Java\\jdk1.8.0_112\\jre\\bin\\server\\jvm.dll'
        configuration.jvmOptions = '-XX:NewRatio=1#-XX:+UseConcMarkSweepGC'
        configuration.jvmMs = 1024
        configuration.jvmMx = 2048
        configuration.jvmSs = 512
        configuration.stopTimeout = 60
        configuration.logPath = '.\\procrun-logs'
        configuration.logPrefix = 'log-prefix'
        configuration.logLevel = 'ERROR'
        configuration.logJniMessages = 1
        configuration.stdOutput = 'auto'
        configuration.stdError = 'stderr.txt'
        configuration.pidFile = 'pid.txt'

        def fileCollection = setupBasicClasspathFileCollection()
        def generator = new InstallScriptGenerator('testProject', fileCollection, configuration, temporaryFolder.root)

        when:
        generator.generate()
        def installScriptLines = new File(temporaryFolder.root, "testProject-install.bat").readLines()

        then:
        installScriptLines.any { it.contains("--Type=interactive") }
        installScriptLines.any { it.contains("++DependsOn=AnotherWindowsService") }
        installScriptLines.any { it.contains("++Environment=envKey1=value1;envKey2=value2;envKey3=value3") }
        installScriptLines.any { it.contains("--LibraryPath=.\\runtime\\bin") }
        installScriptLines.any { it.contains("--JavaHome=.\\runtime") }
        installScriptLines.any { it.contains("--Jvm=\"C:\\Program Files\\Java\\jdk1.8.0_112\\jre\\bin\\server\\jvm.dll\"") }
        installScriptLines.any { it.contains("+JvmOptions=-XX:NewRatio=1#-XX:+UseConcMarkSweepGC") }
        installScriptLines.any { it.contains("--JvmMs=1024") }
        installScriptLines.any { it.contains("--JvmMx=2048") }
        installScriptLines.any { it.contains("--JvmSs=512") }
        installScriptLines.any { it.contains("--StopTimeout=60") }
        installScriptLines.any { it.contains("--LogPath=.\\procrun-logs") }
        installScriptLines.any { it.contains("--LogPrefix=log-prefix") }
        installScriptLines.any { it.contains("--LogLevel=Error") }
        installScriptLines.any { it.contains("--LogJniMessages=1") }
        installScriptLines.any { it.contains("--StdOutput=auto") }
        installScriptLines.any { it.contains("--StdError=stderr.txt") }
        installScriptLines.any { it.contains("-PidFile=pid.txt") }
    }

}
