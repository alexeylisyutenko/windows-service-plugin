package com.github.alexeylisyutenko.windowsserviceplugin

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class WindowsServicePluginTest extends Specification {

    @Rule
    public final TemporaryFolder testProjectDir = new TemporaryFolder()
    File buildFile
    File settingsFile

    def setup() {
        buildFile = testProjectDir.newFile('build.gradle')
        settingsFile = testProjectDir.newFile('settings.gradle')
    }

    private Project setupBasicProject() {
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'com.github.alexeylisyutenko.windows-service-plugin'
        return project
    }

    def "Applying the plugin adds corresponding task to project"() {
        given: "a basic project with com.github.alexeylisyutenko.windows-service-plugin"
        Project project = setupBasicProject()

        when: "project evaluated"
        project.evaluate()

        then: "createWindowsService task added"
        assert project.tasks.createWindowsService instanceof WindowsServicePluginTask
    }

    def "Applying the plugin applies java plugin to project"() {
        given: "a basic project with com.github.alexeylisyutenko.windows-service-plugin"
        Project project = setupBasicProject()

        when: "project evaluated"
        project.evaluate()

        then: "java plugin applied"
        assert project.plugins.hasPlugin('java')
    }

    def 'Applying the plugin provides properties'() {
        given:
        buildFile << """
            plugins {
                id 'com.github.alexeylisyutenko.windows-service-plugin'
            }
            task printProperties {
                doLast {
                    println windowsService.outputDir
                }
            }
        """

        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments('-q', 'printProperties')
                .withPluginClasspath()
                .build()

        then:
        result.task(':printProperties').outcome == SUCCESS
        result.output.trim() == 'windows-service'
    }

    def "testProject should be successfully executed"() {
        given:
        settingsFile << "rootProject.name = 'testProject'"
        buildFile << """
            plugins {
                id 'com.github.alexeylisyutenko.windows-service-plugin'
            }
            windowsService {
                architecture = 'amd64'
                displayName = 'TestService'
                description = 'Service generated with using gradle plugin'
                startClass = 'Main'
                startMethod = 'main'
                startParams = 'start'
                stopClass = 'Main'
                stopMethod = 'main'
                stopParams = 'stop'
                startup = 'auto'
            }
        """

        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments('createWindowsService')
                .withPluginClasspath()
                .build()

        then:
        result.task(":createWindowsService").outcome == SUCCESS
        new File(testProjectDir.root, "build/windows-service/testProject.exe").exists()
        new File(testProjectDir.root, "build/windows-service/testProjectw.exe").exists()
        new File(testProjectDir.root, "build/windows-service/testProject-install.bat").exists()
        new File(testProjectDir.root, "build/windows-service/testProject-uninstall.bat").exists()
        new File(testProjectDir.root, "build/windows-service/lib/testProject.jar").exists()
    }

    def "outputDir property should change plugin output directory"() {
        given:
        settingsFile << "rootProject.name = 'testProject'"
        buildFile << """
            plugins {
                id 'com.github.alexeylisyutenko.windows-service-plugin'
            }
            windowsService {
                outputDir = 'new-output-directory'
                architecture = 'amd64'
                displayName = 'TestService'
                description = 'Service generated with using gradle plugin'
                startClass = 'Main'
                startMethod = 'main'
                startParams = 'start'
                stopClass = 'Main'
                stopMethod = 'main'
                stopParams = 'stop'
                startup = 'auto'
            }
        """

        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments('createWindowsService')
                .withPluginClasspath()
                .build()

        then:
        result.task(":createWindowsService").outcome == SUCCESS
        !new File(testProjectDir.root, "build/windows-service").exists()
        new File(testProjectDir.root, "build/new-output-directory/testProject.exe").exists()
        new File(testProjectDir.root, "build/new-output-directory/testProjectw.exe").exists()
        new File(testProjectDir.root, "build/new-output-directory/testProject-install.bat").exists()
        new File(testProjectDir.root, "build/new-output-directory/testProject-uninstall.bat").exists()
        new File(testProjectDir.root, "build/new-output-directory/lib/testProject.jar").exists()
    }

    def "description, startParams, stopParams properties should be optional"() {
        given:
        settingsFile << "rootProject.name = 'testProject'"
        buildFile << """
            plugins {
                id 'com.github.alexeylisyutenko.windows-service-plugin'
            }
            windowsService {
                architecture = 'amd64'
                displayName = 'TestService'
                startClass = 'Main'
                startMethod = 'main'
                stopClass = 'Main'
                stopMethod = 'main'
                startup = 'auto'
            }
        """

        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments('createWindowsService')
                .withPluginClasspath()
                .build()

        then:
        result.task(":createWindowsService").outcome == SUCCESS
    }

}
