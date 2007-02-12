@echo off
rem @version $Revision$ ($Author$)  $Date$
title Jetty:microemu-webstart

call mvn -P debug webstart:jnlp
call mvn %* jetty:run

title Jetty:microemu-webstart - ended

pause
