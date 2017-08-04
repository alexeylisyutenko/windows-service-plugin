@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  testProject uninstall script
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set APP_HOME=%~dp0
if "%APP_HOME%" == "" set APP_HOME=.\
set SERVICE_EXE=%APP_HOME%testProject.exe

@rem Uninstall testProject
"%SERVICE_EXE%" delete

@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal