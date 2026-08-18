@echo off
setlocal
echo ===================================================
echo   TaskFlow Enterprise Backend - Build Script
echo ===================================================

set "JAVA_HOME=C:\Users\Kavi\.p2\pool\plugins\org.eclipse.justj.openjdk.hotspot.jre.full.win32.x86_64_21.0.10.v20260205-0638\jre"
set "PATH=%JAVA_HOME%\bin;%PATH%"
set "MVN_CMD=C:\Users\Kavi\.m2\wrapper\dists\apache-maven-3.9.16\0daed3be3ebd1c706f0e69e8b07c6b73f5cc4ea3dfce72a8d0ec2e849ca2ddb0\bin\mvn.cmd"

echo [1/2] Checking Java 21 environment...
"%JAVA_HOME%\bin\java.exe" -version

echo.
echo [2/2] Building project and running test suite...
call "%MVN_CMD%" clean package

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ===================================================
    echo   BUILD SUCCESSFUL!
    echo   Output JAR: target\taskflow-backend-1.0.0-SNAPSHOT.jar
    echo ===================================================
) else (
    echo.
    echo ===================================================
    echo   BUILD FAILED! Please check error output above.
    echo ===================================================
)

endlocal
