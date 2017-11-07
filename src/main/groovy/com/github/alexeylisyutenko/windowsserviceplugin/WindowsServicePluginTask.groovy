package com.github.alexeylisyutenko.windowsserviceplugin

import com.github.alexeylisyutenko.windowsserviceplugin.script.InstallScriptGenerator
import com.github.alexeylisyutenko.windowsserviceplugin.script.UninstallScriptGenerator
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.FileCollection
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
/**
 * The main plugin's task that creates a windows service distribution.
 *
 * Created by Алексей Лисютенко on 28.02.2017.
 */
class WindowsServicePluginTask extends DefaultTask {

    /**
     * Plugin configuration for current project.
     */
    @Nested
    private WindowsServicePluginConfiguration configuration
    WindowsServicePluginConfiguration getConfiguration() {
        configuration
    }

    /**
     * Classpath automatically obtained from the jar task.
     */
    FileCollection automaticClasspath = project.files()

    /**
     * Input files which this task is dependent to.
     */
    @InputFiles
    FileCollection getClasspath() {
        configuration.overridingClasspath ?: automaticClasspath
    }

    /**
     * An output directory of the plugin.
     */
    @OutputDirectory
    File getOutputDirectory() {
        project.file("${project.buildDir}/${configuration.outputDir}")
    }

    WindowsServicePluginTask() {
        this.configuration = project.getConvention().getByType(WindowsServicePluginConfiguration.class)

        // Apply Java gradle plugin.
        project.pluginManager.apply(JavaPlugin.class)

        // Make this task depended on the jar task.
        dependsOn.add(project.tasks[JavaPlugin.JAR_TASK_NAME])

        // Populate classpath with jar task outputs and runtime dependencies.
        project.afterEvaluate {
            automaticClasspath = automaticClasspath + project.files(project.tasks[JavaPlugin.JAR_TASK_NAME])
            automaticClasspath = automaticClasspath + project.configurations[JavaPlugin.RUNTIME_CONFIGURATION_NAME]
        }
    }

    @TaskAction
    void run() {
        // Clean output directory.
        project.delete(outputDirectory)

        // Copy the project dependency jars to the configured library directory.
        def libraryDirectory = new File(outputDirectory, 'lib')
        copyAllDependencies(libraryDirectory)

        // Copy apache commons daemon exe files to the output directory.
        extractCommonsDaemonBinaries(outputDirectory)

        // Create batch files.
        new InstallScriptGenerator(project.name, classpath, configuration, outputDirectory).generate()
        new UninstallScriptGenerator(project.name, outputDirectory).generate()
    }

    /**
     * Copies the project dependency jars to the configured library directory.
     */
    def copyAllDependencies(File libraryDirectory) {
        project.copy {
            from { classpath }
            into { libraryDirectory }
        }
    }

    /**
     * Extracts apache commons daemon service executables into output directory.
     */
    def extractCommonsDaemonBinaries(File outputDirectory) {
        // Check if apache commons daemon windows binaries archive is available.
        def commonsDaemonBinariesZip = project.configurations.getByName(WindowsServicePlugin.COMMONS_DAEMON_BIN_CONFIGURATION_NAME)
                .find { File file -> file.name =~ /commons-daemon-.+-bin-windows\.zip/ }
        if (!commonsDaemonBinariesZip) {
            throw new GradleException("Archive with apache commons daemon binaries is not found!")
        }

        // Prepare FileCollection containing service binaries.
        def architecture = configuration.architecture
        def commonsDaemonBinaries = project.zipTree(commonsDaemonBinariesZip).matching {
            include 'prunmgr.exe'
            switch (architecture) {
                case Architecture.X86:
                    include 'prunsrv.exe'
                    break
                case Architecture.AMD64:
                    include 'amd64/prunsrv.exe'
                    break
                case Architecture.IA64:
                    include 'ia64/prunsrv.exe'
                    break
            }
        }.files

        // Copy service binaries to output directory.
        project.copy {
            from { commonsDaemonBinaries }
            into { outputDirectory }
            rename 'prunsrv.exe', "${project.name}.exe"
            rename 'prunmgr.exe', "${project.name}w.exe"
        }
    }
}
