@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  testProject install script as windows service
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set APP_HOME=%~dp0
if "%APP_HOME%" == "" set APP_HOME=.\
set SERVICE_EXE=%APP_HOME%testProject.exe

@rem Setup all necessary variables
set CLASSPATH=%APP_HOME%lib\testProject.jar

@rem Install testProject
"%SERVICE_EXE%" install ^
    --Classpath=%CLASSPATH% ^
    --Description="Service generated with using gradle plugin" ^
    --DisplayName=TestService ^
    --StartClass=Main ^
    --StartMethod=main ^
    ++StartParams=start ^
    --StartMode=jvm ^
    --StopClass=Main ^
    --StopMethod=main ^
    ++StopParams=stop ^
    --StopMode=jvm ^
    --Jvm=auto ^
    --Startup=auto

@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal