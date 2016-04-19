@ECHO off
SETLOCAL ENABLEEXTENSIONS
SETLOCAL ENABLEDELAYEDEXPANSION

ECHO -------------------------------------------------------------------------

ECHO eap-cluster-app quickstart setup

ECHO -------------------------------------------------------------------------

ECHO

ECHO A simple script which sets up the quickstart, runs it and stops the servers.

IF [%1] == [--setup] GOTO :SETUP
IF [%1] == [--setup-domain] GOTO :SETUP
IF [%1] == [--run] GOTO :RUN_QUICKSTART
IF [%1] == [--teardown] GOTO :TEARDOWN

ECHO Please provide one of the following parameters for proper script execution:
ECHO --setup         For setting up and running EAP servers in standalone mode;
ECHO --setup-domain  For setting up and running EAP servers in domain mode;
ECHO --run           For running the quickstarts;
ECHO --teardown      For stopping the started servers;
GOTO :EOF

:SETUP
	CALL :TEARDOWN

	MD setupOutput\
	CD setupOutput

	IF DEFINED JDG_MODULES_ZIP_PATH GOTO :UNZIP_JDG_MODULES

	ECHO The JDG_MODULES_ZIP_PATH environment variable has not been defined

	GOTO :EOF

	:UNZIP_JDG_MODULES

	ECHO Unzipping JDG Modules...

	"%JAVA_HOME%"\bin\jar xf "%JDG_MODULES_ZIP_PATH%"
	if errorlevel 1 (
	   ECHO Something went wrong while extracting JDG modules by given path "%JDG_MODULES_ZIP_PATH%"
	   exit /b %errorlevel%
	) else (
	   FOR /D %%a IN (*) DO (
		SET "JDG_MODULES_HOME=%CD%\%%a"
		GOTO :CONTINUE_WITH_EAP
	   )
	)

	:CONTINUE_WITH_EAP
	md eap-server\
	cd eap-server\
	SET "EAP_DIRS=%CD%"

	IF DEFINED EAP_SERVER_ZIP_PATH GOTO :UNZIP_EAP

	ECHO The EAP_SERVER_ZIP_PATH environment variable has not been defined

	GOTO :EOF

	:UNZIP_EAP
	REM C:\ProgramData\Oracle\Java\javapath;C:\Windows\system32;C:\Windows;C:\Windows\System32\Wbem;C:\Windows\System32\WindowsPowerShell\v1.0\
	ECHO Unzipping EAP Server ...
	"%JAVA_HOME%"\bin\jar xf "%EAP_SERVER_ZIP_PATH%"
	if errorlevel 1 (
	   ECHO Something went wrong while extracting EAP server by given path "%EAP_SERVER_ZIP_PATH%"
	   exit /b %errorlevel%
	) else (
	   FOR /D %%a IN (*) DO (
		SET "EAP_HOME=%CD%\%%a"
		GOTO :CONTINUE_SETUP
	   )
	)
	:CONTINUE_SETUP
	cd ..\..
	ECHO Copying the JDG modules to EAP Home directory..
	XCOPY /e/i/f "%JDG_MODULES_HOME%"\modules "%EAP_HOME%"\modules >nul 2>&1
	ECHO Adding user to EAP
	@ECHO | call "%EAP_HOME%"\bin\add-user.bat -a -u quickuser -p quick-123 >nul 2>&1

	ECHO Building quickstart ..
	CALL mvn clean install > setupOutput\mvn_install.log

	IF "%1" EQU "--setup" (
	   ECHO Copying EAP server to directories ...

	   XCOPY /e/i/f "%EAP_HOME%" "%EAP_DIRS%\server1" >nul 2>&1
	   SET "EAP_HOME1=%EAP_DIRS%\server1"

	   XCOPY /e/i/f "%EAP_HOME%" "%EAP_DIRS%\server2" >nul 2>&1
	   SET "EAP_HOME2=%EAP_DIRS%\server2"

	   XCOPY /e/i/f "%EAP_HOME%" "%EAP_DIRS%\server3" >nul 2>&1
	   SET "EAP_HOME3=%EAP_DIRS%\server3"

	   XCOPY /e/i/f "%EAP_HOME%" "%EAP_DIRS%\server4" >nul 2>&1
	   SET "EAP_HOME4=%EAP_DIRS%\server4"

	   ECHO Running quckstart in Standalon mode. Starting all 4 instances of the server...

	   START /B "Running EAP node1" "!EAP_HOME1!\bin\standalone.bat" -Djboss.node.name=node1 > "!EAP_HOME1!\server.log"
	   START /B "Running EAP node2" "!EAP_HOME2!\bin\standalone.bat" -Djboss.node.name=node2 -Djboss.socket.binding.port-offset=100 > "!EAP_HOME2!\server.log"
	   START /B "Running EAP node3" "!EAP_HOME3!\bin\standalone.bat" -Djboss.node.name=node3 -Djboss.socket.binding.port-offset=200 -c standalone-ha.xml > "!EAP_HOME3!\server.log"
	   START /B "Running EAP node4" "!EAP_HOME4!\bin\standalone.bat" -Djboss.node.name=node4 -Djboss.socket.binding.port-offset=300 -c standalone-ha.xml > "!EAP_HOME4!\server.log"

	   ECHO Waiting for 2 minutes until the servers are started...
	   TIMEOUT 120

	   ECHO Adding the configuration for EJB server-to-server invocation ...
	   @ECHO | CALL "!EAP_HOME1!\bin\jboss-cli.bat" -c --controller=localhost:9990 --file=install-appOne-standalone.cli > setupOutput/install.log
	   @ECHO | CALL "!EAP_HOME2!\bin\jboss-cli.bat" -c --controller=localhost:10090 --file=install-appOne-standalone.cli >> setupOutput/install.log
	   @ECHO | CALL "!EAP_HOME3!\bin\jboss-cli.bat" -c --controller=localhost:10190 --file=install-appOne-standalone.cli >> setupOutput/install.log
	   @ECHO | CALL "!EAP_HOME4!\bin\jboss-cli.bat" -c --controller=localhost:10290 --file=install-appOne-standalone.cli >> setupOutput/install.log

	   ECHO Waiting until the configurations are set properly..
	   TIMEOUT 10

	   ECHO Copying the resources to EAP servers..
	   ECHO f | XCOPY /F/Y adminApp\ear\target\jboss-eap-application-adminApp.ear "!EAP_HOME1!\standalone\deployments\jboss-eap-application-adminApp.ear" >nul 2>&1
	   ECHO f | XCOPY /F/Y appOne\ear\target\jboss-eap-application-AppOne.ear "!EAP_HOME2!\standalone\deployments\jboss-eap-application-AppOne.ear" >nul 2>&1
	   ECHO f | XCOPY /F/Y appTwo\ear\target\jboss-eap-application-AppTwo.ear "!EAP_HOME3!\standalone\deployments\jboss-eap-application-AppTwo.ear" >nul 2>&1
	   ECHO f | XCOPY /F/Y appTwo\ear\target\jboss-eap-application-AppTwo.ear "!EAP_HOME4!\standalone\deployments\jboss-eap-application-AppTwo.ear" >nul 2>&1

	   ECHO Waiting until the servers are deployed...
	   TIMEOUT 40
	) ELSE (
	   IF "%1" EQU "--setup-domain" (
         ECHO Running quickstart in Domain Mode ..

         START /B "Running EAP Server" "%EAP_HOME%\bin\domain.bat" > "%EAP_HOME%\server.log"
         ECHO Waiting for 2 minutes until the server is started...
         TIMEOUT 120

         ECHO Applying the configuration for the quickstart, the domain will contain 4 nodes ..
         @ECHO | CALL "%EAP_HOME%\bin\jboss-cli.bat" -c --file=install-domain.cli > setupOutput\install.log

         ECHO Waiting for 75 seconds until the nodes are started for deploying the application ...
         TIMEOUT 75

         ECHO Deploying application for domain mode ...
         @ECHO | CALL "%EAP_HOME%\bin\jboss-cli.bat" -c --file=deploy-domain.cli > setupOutput/deploy.log
	   )
	)
	ECHO Setup is Done!
	GOTO :EOF

:RUN_QUICKSTART
 	ECHO Running the Step1.
	ECHO 	  Add values to App1 cache with the AdminApp and validate that they are replicated to the server instance of AppOne.
   ECHO    Add a value to App2 cache, rollback the transaction and check that it is not added to the cache after rollback.
   ECHO    The AdminServer and the AppOneServer are not configured as JBoss EAP cluster, only the Infinispan caches are configured by the application to communicate and replicate the caches.

	CALL mvn -Dexec.mainClass=org.jboss.as.quickstarts.datagrid.eap.app.AdminClient exec:java

   ECHO Running Step 2:
	ECHO 	 Add values to App2 cache with the AdminApp and access AppOne to show that the EJB invocation is clustered and both AppTwo instances are used.
   ECHO	 Show that the JBoss EAP and Infinispan clusters are not related and the Infinispan cluster is able to use a different JGroups implementation as the JBoss EAP server.

	CALL mvn -Dexec.mainClass=org.jboss.as.quickstarts.datagrid.eap.app.AppOneClient exec:java
	GOTO :EOF

:TEARDOWN
	ECHO Stopping All Servers
	"%JAVA_HOME%\bin\jps.exe" -v > "%TEMP%\tmp.txt"
	for /f "tokens=1" %%f in ('find "standalone" "%TEMP%\tmp.txt"') do TSKILL %%f >nul 2>&1
	for /f "tokens=1" %%f in ('find "process-controller" "%TEMP%\tmp.txt"') do SET process_id_domain=%%f

	IF DEFINED process_id_domain TSKILL %process_id_domain% >nul 2>&1

	REM Killing the child cmd.exe processes
	TASKKILL /f /fi "STATUS eq Unknown" /im cmd.exe >nul 2>&1

	ECHO Waiting 20 seconds until the servers are stopped...
	TIMEOUT 20

   IF EXIST setupOutput (
	   ECHO Deleting output folder...
	   RD /S /Q setupOutput
	)
	GOTO :EOF