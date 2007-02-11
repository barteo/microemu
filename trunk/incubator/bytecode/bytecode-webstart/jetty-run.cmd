@echo off
rem @version $Revision$ ($Author$)  $Date$
title *Jetty:bytecode-webstart

call mvn -P debug webstart:jnlp

echo Go to http://localhost:8080/bytecode-webstart/

call mvn %* jetty:run

title Jetty:bytecode-webstart - ended

pause
