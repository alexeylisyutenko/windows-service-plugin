@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  TestApplication install script as windows service
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set APP_HOME=%~dp0
if "%APP_HOME%" == "" set APP_HOME=.\
set SERVICE_EXE=%APP_HOME%TestApplication.exe

@rem Setup all necessary variables
set CLASSPATH=%APP_HOME%\lib\native-platform.dll;%APP_HOME%\lib\native-platform.dll.lock;%APP_HOME%\lib\jansi.dll

@rem Install TestApplication
"%SERVICE_EXE%" install ^
    --Classpath=%CLASSPATH% ^
    --Description="Service generated with using gradle plugin" ^
    --DisplayName=TestService ^
    --StartClass=ru.icbcom.Main ^
    --StartMethod=main ^
    ++StartParams=start ^
    --StopClass=ru.icbcom.Main ^
    --StopMethod=main ^
    ++StopParams=stop ^
    --Startup=manual

@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal