@echo off
rem @version $Revision$ ($Author$)  $Date$
SETLOCAL

call %~dp0environment.cmd %*
if errorlevel 1 (
    echo Error calling environment.cmd
    endlocal
    pause
    exit /b 1
)

set jour_version=2.0.1-SNAPSHOT
set javassist_version=3.4.ga
set SIGNATURE_TOOL_CP=%MAVEN2_REPO%\net\sf\jour\jour-instrument\%jour_version%\jour-instrument-%jour_version%.jar
set SIGNATURE_TOOL_CP=%SIGNATURE_TOOL_CP%;%MAVEN2_REPO%\jboss\javassist\%javassist_version%\javassist-%javassist_version%.jar

java -cp "%SIGNATURE_TOOL_CP%" net.sf.jour.SignatureGenerator --src %WTK_HOME%\lib\cldcapi10.jar  --packages java;javax --dst cldcapi10-signature.xml
java -cp "%SIGNATURE_TOOL_CP%" net.sf.jour.SignatureGenerator --src %WTK_HOME%\lib\cldcapi10.jar  --packages javax.microedition.io --dst cldcapi10-javax.microedition.io-signature.xml
java -cp "%SIGNATURE_TOOL_CP%" net.sf.jour.SignatureGenerator --src %WTK_HOME%\lib\cldcapi11.jar  --packages java;javax --dst cldcapi11-signature.xml
java -cp "%SIGNATURE_TOOL_CP%" net.sf.jour.SignatureGenerator --src %WTK_HOME%\lib\cldcapi11.jar  --packages javax.microedition.io --dst cldcapi11-javax.microedition.io-signature.xml
java -cp "%SIGNATURE_TOOL_CP%" net.sf.jour.SignatureGenerator --src %WTK_HOME%\lib\midpapi20.jar  --packages java;javax --dst midpapi20-signature.xml --jars %WTK_HOME%\lib\cldcapi11.jar;%WTK_HOME%\lib\mmapi.jar;%WTK_HOME%\lib\wma20.jar;%WTK_HOME%\lib\satsa-pki.jar

java -cp "%SIGNATURE_TOOL_CP%" net.sf.jour.SignatureGenerator --src %WTK_HOME%\lib\jsr082.jar  --packages javax.bluetooth --dst jsr82-signature.xml --jars %WTK_HOME%\lib\cldcapi11.jar
java -cp "%SIGNATURE_TOOL_CP%" net.sf.jour.SignatureGenerator --src %WTK_HOME%\lib\jsr082.jar  --packages javax.obex --dst jsr82-obex-signature.xml --jars %WTK_HOME%\lib\cldcapi11.jar

if errorlevel 1 goto errormark
echo [Launched OK]
goto endmark
:errormark
	ENDLOCAL
	echo Error in start
	rem pause
:endmark
ENDLOCAL
