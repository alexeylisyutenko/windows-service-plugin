# Introduction

Gradle plugin for wrapping java application as a windows service. 
The plugin uses [Apache Commons Daemon Procrun](https://commons.apache.org/proper/commons-daemon/procrun.html) for creation of service executables.

# Quick start

To create a windows service application you need to go through several simple steps:

1. Create a class with the appropriate method:
```
public class MyService {

  public static void main(String[] args) {
    String command = "start";
    if (args.length > 0) {
      command = args[0];
    }
    if ("start".equals(command)) {
      // process service start function
    } else {
      // process service stop function
    }
  }

}
```

2. Include the plugin:
```
buildscript {
  repositories {
    maven {
      url "https://plugins.gradle.org/m2/"
    }
  }
  dependencies {
    classpath "gradle.plugin.com.github.alexeylisyutenko:windows-service-plugin:1.1.0"
  }
}

apply plugin: "com.github.alexeylisyutenko.windows-service-plugin"
```
    
The same script snippet for new, incubating, plugin mechanism introduced in Gradle 2.1:
```
plugins {
  id "com.github.alexeylisyutenko.windows-service-plugin" version "1.1.0"
}
```
    
3. Configure the plugin:

```
windowsService {
  architecture = 'amd64'
  displayName = 'TestService'
  description = 'Service generated with using gradle plugin'   
  startClass = 'MyService'
  startMethod = 'main'
  startParams = 'start'
  stopClass = 'MyService'
  stopMethod = 'main'
  stopParams = 'stop'
  startup = 'auto'
}
```
4. Run **createWindowsService** gradle task to create a windows service distribution.

That's all you need to do to create a simple windows service. 

In `${project.buildDir}/windows-service` directory you will find service executables, batch scripts for installation/uninstallation of the service and all runtime libraries. 
To install the service run `<project-name>-install.bat` and if you want to uninstall the service run `<project-name>-uninstall.bat`.
To start and stop the service use `<project-name>w.exe` executable.

Note that the method handling service start should create and start a separate thread to carry out the processing, and then return. The main method is called from different threads when you start and stop the service.

# Tasks

There is only one main task:
* **createWindowsService** - Task which generates windows service executables and creates batch scripts for installation/uninstallation. 
With defalut settings this creates the executable under `${project.buildDir}/windows-service` and puts all runtime libraries into the lib subfolder.

# Configuration

The configuration virtually follows of the Apache Commons Daemon Procrun command line parameters.

## How to include

Please see the [Gradle Plugin Portal](https://plugins.gradle.org/plugin/com.github.alexeylisyutenko.windows-service-plugin) for instructions on including this plugin in your project.

## How to configure

The plugin defines the following extension properties in the `windowsService` closure:

| Property  | Default Value | Required | Description                                        |
|-----------|---------------|----------|----------------------------------------------------|
| outputDir | *windows-service* | True | The output directory for createWindowsService task. |
| architecture | *amd64* | True | The type of windows service executables. Choose this value according to your operating system. <p>Possible values: **x86** - for 32-bit (x86) architectures, **amd64** - for AMD/EMT 64-bit.</p>**Note:** This version of the plugin does not support **ia64** option any more. |
| displayName | | True | Service display name. |
| description | | False | Service name description (maximum 1024 characters). |
| startClass | | True | Class that contains the startup method. |
| startMethod | | True | Name of method to be called when service is started. It must be static void and have argument (String args[]). |
| startParams | | False | List of parameters that will be passed to StartClass.<p>This property could be set in two ways: <ol style="margin-bottom: -1em; margin-top: -1em;"><li>As a string where parameters are separated using either # or ; character:<br>```windowsService.startParams='startParam1;startParam2'```</li><li>As a list:<br>```windowsService.startParams=['startParam1', 'startParam2']```</li></ol></p> |
| stopClass | | True | Class that will be used on Stop service signal. |
| stopMethod | | True | Name of method to be called when service is stopped. It must be static void and have argument (String args[]). |
| stopParams | | False | List of parameters that will be passed to StopClass.<p>This property could be set in two ways:<ol style="margin-bottom: -1em; margin-top: -1em;"><li>As a string where parameters are separated using either # or ; character:<br>```windowsService.stopParams='stopParam1;stopParam2'```</li><li>As a list:<br>```windowsService.stopParams=['stopParam1', 'stopParam2']```</li></ol></p> |
| startup | *manual* | True | Service startup mode can be either **auto** or **manual**. |
| interactive | *False* | False | Service type can be interactive to allow the service to interact with the desktop. Use this option only with Local system accounts. |
| dependsOn | | False | List of services that this service depends on.<p>This property could be set in two ways:<ol style="margin-bottom: -1em; margin-top: -1em;"><li>As a string where parameters are separated using either # or ; character:<br>```windowsService.dependsOn='ServiceOne;ServiceTwo'```</li><li>As a list:<br>```windowsService.dependsOn=['ServiceOne', 'ServiceTwo']```</li></ol></p> |
| environment | | False | List of environment variables that will be provided to the service in the form **key=value**.<p>This property could be set in two ways:<ol style="margin-top: -1em;"><li>As a string where key-value pairs are separated using either # or ; character:<br>```windowsService.environment='envKey1=value1;envKey2=value2'```</li><li>As a map:<br>```windowsService.environment=['key1': 'value1', 'key2': 'value2']```</li></ol></p>**Note**: If you use the first way and you need to embed either # or ; character within a value put them inside single quotes. |
| libraryPath | | False | Directory added to the search path used to locate the DLLs for the JVM. This directory is added both in front of the PATH environment variable and as a parameter to the SetDLLDirectory function. |
| javaHome | *JAVA_HOME* | False | Set a different **JAVA_HOME** than defined by **JAVA_HOME** environment variable. |
| jvm | *auto* | False | Use either **auto** (i.e. find the JVM from the Windows registry) or specify the full path to the **jvm.dll**. You can use environment variable expansion here. |
| jvmOptions | *-Xrs* | False | List of options in the form of **-D** or **-X** that will be passed to the JVM. <p>This property could be set in two ways:<ol style="margin-top: -1em;"><li>As a string where parameters are separated using either # or ; character:<br>```windowsService.jvmOptions='jvmOption1;jvmOption2'```</li><li>As a list:<br>```windowsService.jvmOptions=['jvmOption1', 'jvmOption2']```</li></ol></p>**Note:** If you use the first way and you need to embed either # or ; character within a value put them inside single quotes. |
| jvmOptions9 | | False | List of options in the form of **-D** or **-X** that will be passed to the JVM when running on Java 9 or later.<p>This property could be set in two ways:<ol style="margin-top: -1em;"><li>As a string where parameters are separated using either # or ; character:<br>```windowsService.jvmOptions9='jvmOption1;jvmOption2'```</li><li>As a list:<br>```windowsService.jvmOptions9=['jvmOption1', 'jvmOption2']```</li></ol></p>**Note:** If you use the first way and you need to embed either # or ; character within a value put them inside single quotes. |
| jvmMs | | False | Initial memory pool size in MB. |
| jvmMx | | False | Maximum memory pool size in MB. |
| jvmSs | | False | Thread stack size in KB. |
| stopTimeout | *No Timeout* | False | Defines the timeout in seconds that procrun waits for service to exit gracefully. |
| logPath | *%SystemRoot%\\<br>System32\\<br>LogFiles\Apache* | False | Defines the path for logging. Creates the directory if necessary. |
| logPrefix | *commons-daemon* | False | Defines the service log filename prefix. The log file is created in the LogPath directory with **.YEAR-MONTH-DAY.log** suffix |
| logLevel | *Info* | False | Defines the logging level and can be either Error, Info, Warn or Debug. |
| logJniMessages | *0* | False | Set this non-zero (e.g. 1) to capture JVM jni debug messages in the procrun log file. Is not needed if stdout/stderr redirection is being used. |
| stdOutput | | False | Redirected stdout filename. If named **auto** file is created inside **LogPath** with the name **service-stdout.YEAR-MONTH-DAY.log**. |
| stdError | | False | Redirected stderr filename. If named **auto** file is created in the **LogPath** directory with the name **service-stderr.YEAR-MONTH-DAY.log**. |
| pidFile | | False | Defines the file name for storing the running process id. Actual file is created in the **LogPath** directory. | 
| serviceUser | | False | Specifies the name of the account under which the service should run. Use an account name in the form **DomainName\UserName**. The service process will be logged on as this user. if the account belongs to the built-in domain, you can specify **.\UserName**. |
| servicePassword | | False | Password for user account set by **serviceUser** parameter. |
| overridingClasspath | | False | Use this property to override the default classpath. |

## Overriding the default classpath
By default, the plugin's classpath consists of `jar` task outputs and all runtime dependencies. When you execute the `createWindowsService` task, all jars which are in the classpath will be copied to `${project.buildDir}/windows-service/lib` directory and will be used in an install script. For majority of applications this works perfectly fine, but there could be some situations when you might need to change this behavior. 

The following example shows how to use this plugin hand in hand with the `shadow` plugin, which combines dependency classes and resources into a single output Jar:
```
windowsService {
	// Setup the main windows service parameters.
    ...
    
    // Set shadowJar task outputs as the plugin's classpath.
    overridingClasspath = project.files(project.tasks.shadowJar.outputs)
}

// Add shadowJar dependency. 
createWindowsService.dependsOn shadowJar
```
Now, when you execute `createWindowsService` task, you'll get only one jar file in the output lib directory and in the install script.

The `overridingClasspath` property is a Gradle's `FileCollection`, which is simply a set of files. A natural way to obtain a `FileCollection` instance is to use the `Project.files(java.lang.Object[])` method. You can pass this method any number of objects, which are then converted into a set of `File` objects. You can pass collections, iterables, maps and arrays to the `files()` method. These are flattened and the contents converted to `File` instances.

# Version

See [VERSION.md](VERSION.md) for more information.