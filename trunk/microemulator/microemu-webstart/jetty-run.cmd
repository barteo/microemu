@echo off
rem @version $Revision$ ($Author$)  $Date$
title *Jetty:microemu-webstart

call mvn -P debug webstart:jnlp
echo Go to http://localhost:8080/microemu-webstart/
call mvn %* jetty:run

title Jetty:microemu-webstart - ended

pause
