@echo off

REM  Startup script for ArgoUML-MDR Win32
REM  (derived from the Ant startup script)

if "%OS%"=="Windows_NT" @setlocal
if "%OS%"=="WINNT" @setlocal

rem %~dp0 is expanded pathname of the current script under NT
set ARGO_HOME=%~dp0

if "%JAVA_HOME%" == "" goto noJavaHome
if not exist "%JAVA_HOME%\bin\java.exe" goto noJavaHome
set _JAVACMD="%JAVA_HOME%\bin\java.exe"
goto runArgo

:noJavaHome
echo JAVA_HOME not set or java.exe not find! Please adjust it in your environment settings.
goto end

:runArgo
%_JAVACMD% -Dargouml.model.implementation=org.argouml.model.mdr.MDRModelImplementation -jar "%ARGO_HOME%argouml-mdr.jar" %*
goto end

:end
set _JAVACMD=
set ARGO_HOME=
if "%OS%"=="Windows_NT" @endlocal
if "%OS%"=="WINNT" @endlocal
