@echo off
rem $Id$

rem This is Environemtn specific file stored here for reference
rem located in build environment in ${BUILD_HOME}/bin

setlocal

set ANT_OPTS=%ANT_OPTS% -Djava.awt.headless=true
set ANT_OPTS=%ANT_OPTS% -Xms80m
set ANT_OPTS=%ANT_OPTS% -Xmx150m

set JAVA_HOME=C:\j2sdk1.4.2
call :runAnt %*

set JAVA_HOME=C:\jdk1.5.0
call :runAnt %*

set JAVA_HOME=C:\jdk1.6.0
call :runAnt %*

endlocal

:Subroutines
goto SubroutinesEND

:runAnt
echo START ANT BUILD [%JAVA_HOME%]
call %ANT_HOME%\bin\ant.bat  %*
if errorlevel 1 (
    echo Error in %JAVA_HOME% build
    exit 1
)
echo ANT BUILD [%JAVA_HOME%] SUCCESSFUL
goto :EOF

:SubroutinesEND