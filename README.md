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
    classpath "gradle.plugin.com.github.alexeylisyutenko:windows-service-plugin:1.0.1"
  }
}

apply plugin: "com.github.alexeylisyutenko.windows-service-plugin"
```
    
The same script snippet for new, incubating, plugin mechanism introduced in Gradle 2.1:
```
plugins {
  id "com.github.alexeylisyutenko.windows-service-plugin" version "1.0.1"
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

The configuration follows of the Apache Commons Daemon Procrun command line parameters.

## How to include

Please see the [Gradle Plugin Portal](https://plugins.gradle.org/plugin/com.github.alexeylisyutenko.windows-service-plugin) for instructions on including this plugin in your project.

## How to configure

The plugin defines the following extension properties in the `windowsService` closure:

| Property  | Default Value | Required | Description                                        |
|-----------|---------------|----------|----------------------------------------------------|
| outputDir | "windows-service" | True | The output directory for createWindowsService task. |
| architecture | amd64 | True | The type of windows service executables. Choose this value according to your operating system. <br />Possible values: **x86** - for 32-bit (x86) architectures, **amd64** - for AMD/EMT 64-bit, **ia64** - for Intel Itanium 64-bit.  |
| displayName | | True | Service display name. |
| description | | False | Service name description (maximum 1024 characters). |
| startClass | | True | Class that contains the startup method. |
| startMethod | | True | Name of method to be called when service is started. It must be static void and have argument (String args[]). |
| startParams | | False | List of parameters that will be passed to either StartImage or StartClass. Parameters are separated using either # or ; character. |
| stopClass | | True | Class that will be used on Stop service signal. |
| stopMethod | | True | Name of method to be called when service is stopped. It must be static void and have argument (String args[]). |
| stopParams | | False | List of parameters that will be passed to either StopImage or StopClass. Parameters are separated using either # or ; character. |
| startup | manual | True | Service startup mode can be either auto or manual. |
