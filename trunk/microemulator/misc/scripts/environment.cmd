@echo off
rem @version $Revision$ ($Author$)  $Date$

call %~dp0version.cmd

set BUILD_HOME=%~dp0
for /f %%i in ("%DEFAULT_BUILD_HOME%..\..") do @set BUILD_HOME=%%~fi

echo BUILD_HOME=[%BUILD_HOME%]

